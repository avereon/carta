package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.*;
import com.avereon.cartesia.cursor.Reticle;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.data.util.DesignPropertiesMap;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.snap.SnapGrid;
import com.avereon.cartesia.tool.*;
import com.avereon.data.NodeSettings;
import com.avereon.product.Rb;
import com.avereon.settings.Settings;
import com.avereon.util.DelayedAction;
import com.avereon.xenon.*;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetSwitchedEvent;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.asset.type.PropertiesType;
import com.avereon.xenon.task.Task;
import com.avereon.xenon.tool.guide.GuideNode;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.xenon.workpane.Workpane;
import com.avereon.xenon.workspace.StatusBar;
import com.avereon.xenon.workspace.Workspace;
import com.avereon.zarra.color.Paints;
import com.avereon.zarra.javafx.Fx;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;
import lombok.CustomLog;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@CustomLog
public class DesignToolV2 extends BaseDesignTool {

	// DEFAULTS

	public static final Reticle DEFAULT_RETICLE = Reticle.CROSSHAIR;

	public static final DesignValue DEFAULT_SELECT_TOLERANCE = new DesignValue( 2, DesignUnit.MILLIMETER );

	public static final Paint DEFAULT_SELECT_DRAW = Paints.parse( "#ff00c0ff" );

	public static final Paint DEFAULT_SELECT_FILL = Paints.parse( "#ff00c040" );

	// KEYS

	private static final String CURRENT_LAYER = "current-layer";

	// GUIDES

	private final LayersGuide layersGuide;

	//	private final ViewsGuide viewsGuide;

	//	private final PrintsGuide printsGuide;

	// RENDERER

	private static final Snap gridSnap = new SnapGrid();

	private final Label toast;

	private final DesignRenderer renderer;

	private final DesignWorkplane workplane;

	private final Stack<DesignPortal> portalStack;

	private final DesignPropertiesMap designPropertiesMap;

	// TOOL PROPERTIES
	// The renderer might also have some properties that should be exposed

	private final ObjectProperty<Reticle> reticle;

	private final ObjectProperty<DesignValue> selectTolerance;

	private final ObjectProperty<DesignShape> selectAperture;

	private final ObjectProperty<DesignLayer> currentLayer;

	private final StringProperty selectDrawPaint;

	private final StringProperty selectFillPaint;

	// OPTIONAL TOOL PROPERTIES
	// The renderer might also have some properties that should be exposed

	private ObjectProperty<DesignLayer> selectedLayer;

	private ObjectProperty<DesignView> currentView;

	// ACTIONS

	private final Map<String, ProgramAction> commandActions;

	private final PrintAction printAction;

	private final PropertiesAction propertiesAction;

	private final DeleteAction deleteAction;

	private final UndoAction undoAction;

	private final RedoAction redoAction;

	private final DelayedAction storePreviousViewAction;

	private BooleanProperty gridSnapEnabled;

	private ChangeListener<Boolean> gridVisibleToggleHandler;

	private ChangeListener<Boolean> snapGridToggleHandler;

	private com.avereon.event.EventHandler<AssetSwitchedEvent> assetSwitchListener;

	public DesignToolV2( XenonProgramProduct product, Asset asset ) {
		super( product, asset );
		addStylesheet( CartesiaMod.STYLESHEET );
		getStyleClass().add( "design-tool" );

		this.commandActions = new ConcurrentHashMap<>();
		this.designPropertiesMap = new DesignPropertiesMap( product );

		this.layersGuide = new LayersGuide( product, this );
		//		this.viewsGuide = new ViewsGuide( product, this );
		//		this.printsGuide = new PrintsGuide( product, this );

		// Create and associate the renderer and workplane
		this.renderer = new DesignRenderer();
		this.workplane = new DesignWorkplane();
		this.renderer.setWorkplane( workplane );

		// TODO Move this to tool settings like reticle and aperture
		this.workplane.setGridStyle( GridStyle.DOT );

		this.reticle = new SimpleObjectProperty<>( DEFAULT_RETICLE );
		this.selectTolerance = new SimpleObjectProperty<>( DEFAULT_SELECT_TOLERANCE );
		this.selectAperture = new SimpleObjectProperty<>();
		this.currentLayer = new SimpleObjectProperty<>();
		this.currentView = new SimpleObjectProperty<>();
		this.selectDrawPaint = new SimpleStringProperty( Paints.toString( DEFAULT_SELECT_DRAW ) );
		this.selectFillPaint = new SimpleStringProperty( Paints.toString( DEFAULT_SELECT_FILL ) );

		// Actions
		this.printAction = new PrintAction( product.getProgram() );
		this.propertiesAction = new PropertiesAction( product.getProgram() );
		this.deleteAction = new DeleteAction( product.getProgram() );
		this.undoAction = new UndoAction( product.getProgram() );
		this.redoAction = new RedoAction( product.getProgram() );

		this.storePreviousViewAction = new DelayedAction( getProgram().getTaskManager().getExecutor(), this::capturePreviousPortal );
		this.storePreviousViewAction.setMinTriggerLimit( 1000 );
		this.storePreviousViewAction.setMaxTriggerLimit( 5000 );
		this.portalStack = new Stack<>();

		// Create the toast label
		String loadingLabel = Rb.text( RbKey.LABEL, "loading" );
		this.toast = new Label( loadingLabel + "..." );

		// Add the components to the parent
		StackPane stack = new StackPane( renderer, toast );
		stack.setAlignment( Pos.CENTER );
		getChildren().addAll( stack );

		// NOTE Settings and settings listeners should go in the ready() method
	}

	@Override
	protected void ready( OpenAssetRequest request ) throws ToolException {
		super.ready( request );

		setTitle( getAsset().getName() );
		setGraphic( getProgram().getIconLibrary().getIcon( getProduct().getCard().getArtifact() ) );

		getAsset().register( Asset.NAME, e -> setTitle( e.getNewValue() ) );
		getAsset().register( Asset.ICON, e -> setIcon( e.getNewValue() ) );

		// Hide the toast message
		toast.setVisible( false );

		// Set the renderer design
		renderer.setDesign( getDesign() );
		renderer.setDpi( Screen.getPrimary().getDpi() );

		// Set "so settings" defaults
		setCurrentLayer( getDesign().getAllLayers().getFirst() );

		// Fire the design ready event (should be done after renderer.setDesign)
		fireEvent( new DesignToolEvent( this, DesignToolEvent.DESIGN_READY ) );

		getAsset().getUndoManager().undoAvailableProperty().addListener( ( v, o, n ) -> undoAction.updateEnabled() );
		getAsset().getUndoManager().redoAvailableProperty().addListener( ( v, o, n ) -> redoAction.updateEnabled() );

		layersGuide.ready( request );
		//		viewsGuide.ready( request );
		//		printsGuide.ready( request );
		//getGuideContext().getGuides().addAll( layersGuide, viewsGuide, printsGuide );
		getGuideContext().getGuides().addAll( layersGuide );
		getGuideContext().setCurrentGuide( layersGuide );

		//		// Keep the design pane centered when resizing
		//		// These should be added before updating the pan and zoom
		//		widthProperty().addListener( ( p, o, n ) -> Fx.run( designPane::updateView ) );
		//		heightProperty().addListener( ( p, o, n ) -> Fx.run( designPane::updateView ) );
		//
		//		// NOTE What listeners should be registered before configuring things???
		//		// There are two ways of handing the initialization of properties that change
		//		// 1. Initialize the properties from settings, then add listeners afterward.
		//		//    This is safer, but does not allow the listener logic to be triggered.
		//		// 2. Add listeners, then initialize the properties. This approach has the
		//		//    benefit of triggering the listeners, but, depending on the listener,
		//		//    that could be problematic if the listener gets caught in a loop.
		//		//    Another downside to this approach is that several listeners might call
		//		//    the same logic (possibly expensive logic) causing an unnecessary
		//		//    delay.
		//
		//		// Workplane settings
		//		configureWorkplane();

		Settings productSettings = getProduct().getSettings();
		Settings settings = getSettings();
		String defaultSelectSize = String.valueOf( DEFAULT_SELECT_TOLERANCE.getValue() );
		String defaultSelectUnit = DEFAULT_SELECT_TOLERANCE.getUnit().toString().toLowerCase();
		String defaultReferencePointType = DesignMarker.Type.CIRCLE.name().toLowerCase();
		String defaultReferencePointSize = "10";
		String defaultReferencePointPaint = "#808080";
		String defaultReticle = DEFAULT_RETICLE.name().toLowerCase();

		// Get tool settings
		double selectApertureSize = Double.parseDouble( productSettings.get( SELECT_APERTURE_SIZE, defaultSelectSize ) );
		DesignUnit selectApertureUnit = DesignUnit.valueOf( productSettings.get( SELECT_APERTURE_UNIT, defaultSelectUnit ).toUpperCase() );
		DesignMarker.Type referencePointType = DesignMarker.Type.valueOf( productSettings.get( REFERENCE_POINT_TYPE, defaultReferencePointType ).toUpperCase() );
		double referencePointSize = Double.parseDouble( productSettings.get( REFERENCE_POINT_SIZE, defaultReferencePointSize ) );
		Paint referencePointPaint = Paints.parse( productSettings.get( REFERENCE_POINT_PAINT, defaultReferencePointPaint ) );

		Point3D viewPoint = ParseUtil.parsePoint3D( settings.get( SETTINGS_VIEW_POINT, "0,0,0" ) );
		double viewZoom = Double.parseDouble( settings.get( SETTINGS_VIEW_ZOOM, "1.0" ) );
		double viewRotate = Double.parseDouble( settings.get( SETTINGS_VIEW_ROTATE, "0.0" ) );
		setView( viewPoint, viewZoom, viewRotate );
		setReticle( Reticle.valueOf( productSettings.get( RETICLE, defaultReticle ).toUpperCase() ) );
		setSelectTolerance( new DesignValue( selectApertureSize, selectApertureUnit ) );
		//		designPane.setReferencePointType( referencePointType );
		//		designPane.setReferencePointSize( referencePointSize );
		//		designPane.setReferencePointPaint( referencePointPaint );

		getDesign().findLayers( DesignLayer.ID, settings.get( CURRENT_LAYER, "" ) ).stream().findFirst().ifPresent( this::setCurrentLayer );
		getDesign().findViews( DesignView.ID, settings.get( CURRENT_VIEW, "" ) ).stream().findFirst().ifPresent( this::setCurrentView );

		//		// Restore the list of enabled layers
		//		Set<String> enabledLayerIds = settings.get( ENABLED_LAYERS, new TypeReference<>() {}, Set.of() );
		//		getDesign().getAllLayers().forEach( l -> designPane.setLayerEnabled( l, enabledLayerIds.contains( l.getId() ) ) );
		//
		//		// Restore the list of visible layers
		//		Set<String> visibleLayerIds = settings.get( VISIBLE_LAYERS, new TypeReference<>() {}, Set.of() );
		//		getDesign().getAllLayers().forEach( l -> designPane.setLayerVisible( l, visibleLayerIds.contains( l.getId() ) ) );

		// Restore the grid visible flag
		setGridVisible( Boolean.parseBoolean( settings.get( GRID_VISIBLE, DEFAULT_GRID_VISIBLE ) ) );

		// Restore the grid snap enabled flag
		setGridSnapEnabled( Boolean.parseBoolean( settings.get( GRID_SNAP_ENABLED, DEFAULT_GRID_SNAP_ENABLED ) ) );

		//		// Restore the reference view visibility
		//		setReferenceLayerVisible( Boolean.parseBoolean( settings.get( REFERENCE_LAYER_VISIBLE, Boolean.TRUE.toString() ) ) );
		//
		//		// Settings listeners
		productSettings.register( RETICLE, e -> setReticle( Reticle.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		productSettings.register( SELECT_APERTURE_SIZE, e -> setSelectTolerance( new DesignValue( Double.parseDouble( (String)e.getNewValue() ), getSelectTolerance().getUnit() ) ) );
		productSettings.register( SELECT_APERTURE_UNIT, e -> setSelectTolerance( new DesignValue( getSelectTolerance().getValue(), DesignUnit.valueOf( ((String)e.getNewValue()).toUpperCase() ) ) ) );
		//productSettings.register( REFERENCE_POINT_TYPE, e -> designPane.setReferencePointType( DesignMarker.Type.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		//productSettings.register( REFERENCE_POINT_SIZE, e -> designPane.setReferencePointSize( Double.parseDouble( (String)e.getNewValue() ) ) );
		//productSettings.register( REFERENCE_POINT_PAINT, e -> designPane.setReferencePointPaint( Paints.parse( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );

		//		// Add layout bounds property listener
		//		layoutBoundsProperty().addListener( ( p, o, n ) -> doUpdateGridBounds() );

		// Add view point property listener
		renderer.viewpointXProperty().addListener( ( p, o, n ) -> {
			storePreviousViewAction.request();
			Point2D vp = renderer.getViewpoint();
			settings.set( SETTINGS_VIEW_POINT, vp.getX() + "," + vp.getY() + ",0" );
			//doUpdateGridBounds();
		} );
		renderer.viewpointYProperty().addListener( ( p, o, n ) -> {
			storePreviousViewAction.request();
			Point2D vp = renderer.getViewpoint();
			settings.set( SETTINGS_VIEW_POINT, vp.getX() + "," + vp.getY() + ",0" );
			//doUpdateGridBounds();
		} );

		// Add view rotate property listener
		renderer.rotateProperty().addListener( ( p, o, n ) -> {
			storePreviousViewAction.request();
			settings.set( SETTINGS_VIEW_ROTATE, n.doubleValue() );
			//doUpdateGridBounds();
		} );

		// Add view zoom property listener
		renderer.zoomXProperty().addListener( ( p, o, n ) -> {
			storePreviousViewAction.request();
			getCoordinateStatus().updateZoom( n.doubleValue() );
			settings.set( SETTINGS_VIEW_ZOOM, n.doubleValue() );
			//doUpdateGridBounds();
		} );
		renderer.zoomYProperty().addListener( ( p, o, n ) -> {
			storePreviousViewAction.request();
			getCoordinateStatus().updateZoom( n.doubleValue() );
			settings.set( SETTINGS_VIEW_ZOOM, n.doubleValue() );
			//doUpdateGridBounds();
		} );

		//		// Add enabled layers listener
		//		designPane.enabledLayersProperty().addListener( this::doStoreEnabledLayers );
		//
		//		// Add visible layers listener
		//		designPane.visibleLayersProperty().addListener( this::doStoreVisibleLayers );

		// Add current layer property listener
		currentLayerProperty().addListener( ( p, o, n ) -> settings.set( CURRENT_LAYER, n.getId() ) );

		// Add current view property listener
		currentViewProperty().addListener( ( p, o, n ) -> settings.set( CURRENT_VIEW, n.getId() ) );

		// Add grid visible property listener
		gridVisible().addListener( ( p, o, n ) -> settings.set( GRID_VISIBLE, String.valueOf( n ) ) );

		// Add grid visible property listener
		gridSnapEnabled().addListener( ( p, o, n ) -> settings.set( GRID_SNAP_ENABLED, String.valueOf( n ) ) );

		//		// Add reference points visible property listener
		//		designPane.referenceLayerVisible().addListener( ( p, o, n ) -> settings.set( REFERENCE_LAYER_VISIBLE, String.valueOf( n ) ) );

		addEventFilter( MouseEvent.MOUSE_MOVED, e -> getDesignContext().setMouse( e ) );

		//addEventFilter( KeyEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( MouseEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( MouseDragEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ScrollEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ZoomEvent.ANY, e -> getCommandContext().handle( e ) );

		getCoordinateStatus().updateZoom( getZoom() );
		//		designPane.updateView();
		//		doUpdateGridBounds();

		// Get the reticle setting and bind the setting to the reticleProperty

		// Update the cursor if the reticle changes and cursor is currently a reticle
		reticle.addListener( ( p, o, n ) -> {
			if( getCursor() instanceof ReticleCursor ) setCursor( n.getCursor( getProgram() ) );
		} );

		// Request the initial geometry render
		renderer.render();
	}

	@Override
	protected void guideNodesSelected( Set<GuideNode> oldNodes, Set<GuideNode> newNodes ) {
		if( getCurrentGuide() == layersGuide ) {
			newNodes.stream().findFirst().ifPresent( n -> doSetSelectedLayerById( n.getId() ) );
			//		} else if( getCurrentGuide() == viewsGuide ) {
			//			newNodes.stream().findFirst().ifPresent( n -> doSetCurrentViewById( n.getId() ) );
			//		} else if( getCurrentGuide() == printsGuide ) {
			//			newNodes.stream().findFirst().ifPresent( n -> doSetCurrentPrintById( n.getId() ) );
		}
	}

	@Override
	protected void guideFocusChanged( boolean focused, Set<GuideNode> nodes ) {
		showPropertiesPage( getCurrentLayer() );
	}

	@Override
	protected void allocate() throws ToolException {
		super.allocate();

		// Add asset switch listener to remove command prompt
		getProgram().register( AssetSwitchedEvent.SWITCHED, assetSwitchListener = e -> {
			if( isDisplayed() && e.getOldAsset() == this.getAsset() && e.getNewAsset() != this.getAsset() ) {
				unregisterStatusBarItems();
			}
		} );
	}

	@Override
	protected void activate() throws ToolException {
		super.activate();

		getCommandContext().setLastActiveDesignTool( this );
		getCommandContext().setTool( this );

		registerStatusBarItems();
		registerCommandCapture();
		registerActions();

		requestFocus();
	}

	@Override
	protected void conceal() throws ToolException {
		unregisterCommandCapture();
		unregisterActions();

		super.conceal();
	}

	@Override
	protected void deallocate() throws ToolException {
		// Remove asset switch listener to unregister status bar items
		getProgram().unregister( AssetSwitchedEvent.SWITCHED, assetSwitchListener );

		super.deallocate();
	}

	@Override
	public Point3D getViewPoint() {
		return CadPoints.toPoint3d( renderer.getViewpoint() );
	}

	@Override
	public void setViewPoint( Point3D point ) {
		renderer.setViewpoint( CadPoints.toPoint2d( point ) );
	}

	public DoubleProperty viewpointXProperty() {
		return renderer.viewpointXProperty();
	}

	public DoubleProperty viewpointYProperty() {
		return renderer.viewpointYProperty();
	}

	public double getDpi() {
		return renderer.getDpiX();
	}

	public void setDpi( double dpi ) {
		renderer.setDpi( new Point2D( dpi, dpi ) );
	}

	@Override
	public double getZoom() {
		return renderer.getZoomX();
	}

	@Override
	public void setZoom( double zoom ) {
		renderer.setZoom( new Point2D( zoom, zoom ) );
	}

	public DoubleProperty zoomXProperty() {
		return renderer.zoomXProperty();
	}

	public DoubleProperty zoomYProperty() {
		return renderer.zoomYProperty();
	}

	@Override
	public double getViewRotate() {
		return renderer.getRotate();
	}

	@Override
	public void setViewRotate( double angle ) {
		renderer.setRotate( angle );
	}

	public DoubleProperty viewRotateProperty() {
		return renderer.rotateProperty();
	}

	@Override
	public final ReticleCursor getReticleCursor() {
		return getReticle().getCursor( getProgram() );
	}

	private Reticle getReticle() {
		return reticle.get();
	}

	private void setReticle( Reticle reticle ) {
		this.reticle.set( reticle );
	}

	public ObjectProperty<Reticle> reticle() {
		return reticle;
	}

	private CommandPrompt getCommandPrompt() {
		return getDesignContext().getCommandPrompt();
	}

	private CoordinateStatus getCoordinateStatus() {
		return getDesignContext().getCoordinateStatus();
	}

	@Override
	public void setView( DesignPortal portal ) {
		setView( portal.getViewpoint(), portal.getZoom(), portal.getRotate() );
	}

	@Override
	public void setView( Point3D center, double zoom ) {
		renderer.setViewpoint( CadPoints.toPoint2d( center ) );
		renderer.setZoom( CadPoints.toPoint2d( zoom, zoom ) );
	}

	@Override
	public void setView( Point3D center, double zoom, double rotate ) {
		renderer.setViewpoint( CadPoints.toPoint2d( center ) );
		renderer.setZoom( CadPoints.toPoint2d( zoom, zoom ) );
		renderer.setRotate( rotate );
	}

	@Override
	public void setViewport( Bounds viewport ) {

	}

	@Override
	public void setSelectTolerance( DesignValue aperture ) {
		selectTolerance().set( aperture );
	}

	@Override
	public DesignValue getSelectTolerance() {
		return selectTolerance.get();
	}

	@Override
	public ObjectProperty<DesignValue> selectTolerance() {
		return selectTolerance;
	}

	public ObjectProperty<DesignShape> selectAperture() {
		return selectAperture;
	}

	@Override
	public ObservableList<Shape> selectedShapes() {
		return FXCollections.observableArrayList();
	}

	@Override
	public Shape nearestShape2d( Collection<Shape> shapes, Point3D point ) {
		return null;
	}

	@Override
	public Point3D nearestCp( Collection<Shape> shapes, Point3D point ) {
		return null;
	}

	public DesignLayer getSelectedLayer() {
		return selectedLayer == null ? null : selectedLayer.get();
	}

	public void setSelectedLayer( DesignLayer layer ) {
		selectedLayerProperty().set( layer );
	}

	public ObjectProperty<DesignLayer> selectedLayerProperty() {
		if( selectedLayer == null ) selectedLayer = new SimpleObjectProperty<>();
		return selectedLayer;
	}

	public boolean isCurrentLayer( DesignLayer layer ) {
		return getCurrentLayer() == layer;
	}

	@Override
	public void setCurrentLayer( DesignLayer layer ) {
		currentLayerProperty().set( layer );
	}

	@Override
	public DesignLayer getCurrentLayer() {
		return currentLayer == null ? null : currentLayer.get();
	}

	@Override
	public ObjectProperty<DesignLayer> currentLayerProperty() {
		return currentLayer;
	}

	@Override
	public boolean isLayerVisible( DesignLayer layer ) {
		return renderer.isLayerVisible( layer );
	}

	@Override
	public void setLayerVisible( DesignLayer layer, boolean visible ) {
		if( visible ) {
			renderer.visibleLayers().add( layer );
		} else {
			renderer.visibleLayers().remove( layer );
		}
	}

	@Override
	public List<DesignLayer> getVisibleLayers() {
		return new ArrayList<>( renderer.visibleLayers() );
	}

	public ObservableSet<DesignLayer> visibleLayers() {
		return renderer.visibleLayers();
	}

	@Override
	public List<Shape> getVisibleShapes() {
		return null;
	}

	@Override
	public List<DesignShape> getVisibleGeometry() {
		return getVisibleLayers().stream().flatMap( layer -> layer.getShapes().stream() ).toList();
	}

	@Override
	public Paint getSelectedDrawPaint() {
		return null;
	}

	@Override
	public Paint getSelectedFillPaint() {
		return null;
	}

	@Override
	public boolean isReferenceLayerVisible() {
		return false;
	}

	@Override
	public void setReferenceLayerVisible( boolean visible ) {

	}

	@Override
	public void setCurrentView( DesignView view ) {
		currentViewProperty().set( view );
	}

	@Override
	public DesignView getCurrentView() {
		return currentView == null ? null : currentView.get();
	}

	@Override
	public ObjectProperty<DesignView> currentViewProperty() {
		if( currentView == null ) currentView = new SimpleObjectProperty<>();
		return currentView;
	}

	@Override
	public void zoom( Point3D anchor, double factor ) {
		Fx.run( () -> renderer.zoom( anchor, factor ) );
	}

	@Override
	public void pan( Point3D viewAnchor, Point3D dragAnchor, double x, double y ) {
		Fx.run( () -> renderer.pan( viewAnchor, dragAnchor, x, y ) );
	}

	@Override
	public Point3D mouseToWorld( Point3D point ) {
		return mouseToWorld( point.getX(), point.getY(), point.getZ() );
	}

	@Override
	public Point3D mouseToWorld( double x, double y, double z ) {
		return renderer == null ? Point3D.ZERO : renderer.parentToLocal( x, y, z );
	}

	@Override
	public Point3D mouseToWorkplane( Point3D point ) {
		return mouseToWorkplane( point.getX(), point.getY(), point.getZ() );
	}

	@Override
	public Point3D mouseToWorkplane( double x, double y, double z ) {
		Point3D worldPoint = mouseToWorld( x, y, z );
		return isGridSnapEnabled() ? gridSnap.snap( this, worldPoint ) : worldPoint;
	}

	@Override
	public Point3D worldToScreen( double x, double y, double z ) {
		return renderer == null ? Point3D.ZERO : renderer.localToParent( x, y, z );
	}

	@Override
	public Point3D worldToScreen( Point3D point ) {
		return renderer == null ? Point3D.ZERO : renderer.localToParent( point );
	}

	@Override
	public Bounds worldToScreen( Bounds bounds ) {
		return renderer == null ? Fx.EMPTY_BOUNDS : renderer.localToParent( bounds );
	}

	@Override
	public Point3D screenToWorld( double x, double y, double z ) {
		return renderer == null ? Point3D.ZERO : renderer.parentToLocal( x, y, z );
	}

	@Override
	public Point3D screenToWorld( Point3D point ) {
		return renderer == null ? Point3D.ZERO : renderer.parentToLocal( point );
	}

	@Override
	public Bounds screenToWorld( Bounds bounds ) {
		return renderer == null ? Fx.EMPTY_BOUNDS : renderer.parentToLocal( bounds );
	}

	@Override
	public boolean isGridVisible() {
		return renderer.isGridVisible();
	}

	@Override
	public void setGridVisible( boolean visible ) {
		renderer.setGridVisible( visible );
	}

	@Override
	public BooleanProperty gridVisible() {
		return renderer.gridVisible();
	}

	@Override
	public boolean isGridSnapEnabled() {
		return gridSnapEnabled == null ? DEFAULT_GRID_SNAP_ENABLED : gridSnapEnabled().get();
	}

	@Override
	public void setGridSnapEnabled( boolean enabled ) {
		gridSnapEnabled().set( enabled );
	}

	@Override
	public BooleanProperty gridSnapEnabled() {
		if( gridSnapEnabled == null ) gridSnapEnabled = new SimpleBooleanProperty( DEFAULT_GRID_SNAP_ENABLED );
		return gridSnapEnabled;
	}

	@Override
	public void updateSelectWindow( Point3D anchor, Point3D mouse ) {
		if( anchor == null || mouse == null ) return;

		// Calculate the bounds of the select aperture
		double x = Math.min( anchor.getX(), mouse.getX() );
		double y = Math.min( anchor.getY(), mouse.getY() );
		double w = Math.abs( anchor.getX() - mouse.getX() );
		double h = Math.abs( anchor.getY() - mouse.getY() );

		// Set the select aperture
		if( w == 0 || h == 0 ) {
			renderer.setSelectAperture( null );
		} else {
			DesignBox selectAperture = new DesignBox( x, y, w, h );
			selectAperture.setFillPaint( selectFillPaint.get() );
			selectAperture.setDrawPaint( selectDrawPaint.get() );
			selectAperture().set( selectAperture );
			renderer.setSelectAperture( selectAperture );
		}
	}

	@Override
	public List<Shape> screenPointFindOneAndWait( Point3D mouse ) {
		throw new UnsupportedOperationException( "This method has a bad interface for this class" );
	}

	@Override
	public List<Shape> screenPointFindAllAndWait( Point3D mouse ) {
		throw new UnsupportedOperationException( "This method has a bad interface for this class" );
	}

	@Override
	public List<Shape> screenPointSelectAndWait( Point3D mouse ) {
		throw new UnsupportedOperationException( "This method has a bad interface for this class" );
	}

	@Override
	public void screenPointSelect( Point3D mouse ) {
		// TODO Implement this
	}

	@Override
	public void screenPointSelect( Point3D mouse, boolean toggle ) {
		List<DesignShape> shapes = getSelectedGeometry();

		if( !toggle ) renderer.clearSelectedShapes();

		renderer.screenPointSelect( mouse, getSelectTolerance() ).stream().findFirst().ifPresent( shape -> {
			if( renderer.isShapeSelected( shape ) ) {
				renderer.selectedShapes().remove( shape );
			} else {
				renderer.selectedShapes().add( shape );
			}
		} );
	}

	@Override
	public void screenWindowSelect( Point3D a, Point3D b, boolean intersect, boolean toggle ) {
		if( !toggle ) renderer.clearSelectedShapes();

		List<DesignShape> shapes = renderer.screenWindowSelect( a, b, intersect );
		if( toggle ) {
			shapes.forEach( shape -> {
				if( renderer.isShapeSelected( shape ) ) {
					renderer.selectedShapes().remove( shape );
				} else {
					renderer.selectedShapes().add( shape );
				}
			} );
		} else {
			renderer.selectedShapes().addAll( shapes );
		}
	}

	@Override
	public void worldPointSelect( Point3D point ) {
		// TODO Implement this
	}

	@Override
	public void worldPointSelect( Point3D point, boolean toggle ) {
		// TODO Implement this
	}

	@Override
	public void clearSelected() {
		renderer.clearSelectedShapes();
	}

	@Override
	public List<DesignShape> findShapesWithMouse( Point3D mouse ) {
		return renderer.screenPointSelect( mouse, getSelectTolerance() );
	}

	@Override
	public List<DesignShape> findShapesWithPoint( Point3D point ) {
		return renderer.worldPointSelect( point, getSelectTolerance() );
	}

	@Override
	public List<DesignShape> getSelectedGeometry() {
		return new ArrayList<>( renderer.selectedShapes() );
	}

	@Override
	public DesignPortal getPriorPortal() {
		if( !portalStack.isEmpty() ) portalStack.pop();
		return portalStack.isEmpty() ? DesignPortal.DEFAULT : portalStack.pop();
	}

	private void capturePreviousPortal() {
		portalStack.push( new DesignPortal( getViewPoint(), getZoom(), getViewRotate() ) );
	}

	protected final void showCommandPrompt() {
		Fx.run( this::registerStatusBarItems );
		Fx.run( this::requestFocus );
	}

	private void registerStatusBarItems() {
		if( getWorkspace() == null ) return;
		Fx.run( () -> {
			StatusBar bar = getWorkspace().getStatusBar();
			bar.setLeftToolItems( getCommandPrompt() );
			bar.setRightToolItems( getCoordinateStatus() );
		} );
	}

	private void unregisterStatusBarItems() {
		if( getWorkspace() == null ) return;
		Fx.run( () -> {
			StatusBar bar = getWorkspace().getStatusBar();
			bar.removeLeftToolItems( getCommandPrompt() );
			bar.removeRightToolItems( getCoordinateStatus() );
		} );
	}

	private void registerCommandCapture() {
		// If there is already a command capture handler then remove it
		// (because it may belong to a different design)
		unregisterCommandCapture();

		// Add the design command capture handler. This captures all key events that
		// make it to the tool and forwards them to the command context which
		// will help determine what to do.
		addEventHandler( KeyEvent.ANY, getCommandContext() );
	}

	@SuppressWarnings( "unchecked" )
	private void unregisterCommandCapture() {
		Workpane workpane = getWorkpane();
		EventHandler<KeyEvent> handler = (EventHandler<KeyEvent>)workpane.getProperties().get( "design-tool-command-capture" );
		if( handler != null ) workpane.removeEventHandler( KeyEvent.ANY, handler );
	}

	private void registerActions() {
		pushAction( "print", printAction );
		pushAction( "properties", propertiesAction );
		pushAction( "delete", deleteAction );
		pushAction( "undo", undoAction );
		pushAction( "redo", redoAction );

		pushCommandAction( "draw-arc-2" );
		pushCommandAction( "draw-arc-3" );
		pushCommandAction( "draw-circle-2" );
		pushCommandAction( "draw-circle-3" );
		pushCommandAction( "draw-circle-diameter-2" );
		//pushCommandAction( "draw-curve-3" );
		pushCommandAction( "draw-curve-4" );
		pushCommandAction( "draw-ellipse-3" );
		pushCommandAction( "draw-ellipse-arc-5" );
		pushCommandAction( "draw-line-2" );
		pushCommandAction( "draw-line-perpendicular" );
		pushCommandAction( "draw-marker" );
		pushCommandAction( "draw-path" );

		pushCommandAction( "measure-angle" );
		pushCommandAction( "measure-distance" );
		pushCommandAction( "measure-length" );
		pushCommandAction( "measure-point" );
		pushCommandAction( "shape-information" );

		ProgramAction gridVisibleToggleAction = pushCommandAction( "grid-toggle", isGridVisible() ? "enabled" : "disabled" );
		gridVisible().addListener( gridVisibleToggleHandler = ( p, o, n ) -> gridVisibleToggleAction.setState( n ? "enabled" : "disabled" ) );
		ProgramAction snapGridToggleAction = pushCommandAction( "snap-grid-toggle", isGridSnapEnabled() ? "enabled" : "disabled" );
		gridSnapEnabled().addListener( snapGridToggleHandler = ( p, o, n ) -> snapGridToggleAction.setState( n ? "enabled" : "disabled" ) );

		String viewActions = "grid-toggle snap-grid-toggle";
		String layerActions = "layer[layer-create layer-sublayer | layer-delete]";
		String drawMarkerActions = "marker[draw-marker]";
		String drawLineActions = "line[draw-line-2 draw-line-perpendicular]";
		String drawCircleActions = "circle[draw-circle-2 draw-circle-diameter-2 draw-circle-3 | draw-arc-2 draw-arc-3]";
		String drawEllipseActions = "ellipse[draw-ellipse-3 draw-ellipse-arc-5]";
		String drawCurveActions = "curve[draw-curve-4 draw-path]";

		String measurementActions = "measure[shape-information measure-angle measure-distance measure-point measure-length]";

		@SuppressWarnings( "StringBufferReplaceableByString" ) StringBuilder menus = new StringBuilder( viewActions );
		menus.append( " " ).append( layerActions );
		menus.append( "|" ).append( drawMarkerActions );
		menus.append( " " ).append( drawLineActions );
		menus.append( " " ).append( drawCircleActions );
		menus.append( " " ).append( drawEllipseActions );
		menus.append( " " ).append( drawCurveActions );
		menus.append( "|" ).append( measurementActions );

		@SuppressWarnings( "StringBufferReplaceableByString" ) StringBuilder tools = new StringBuilder( viewActions );
		tools.append( " " ).append( drawMarkerActions );
		tools.append( " " ).append( drawLineActions );
		tools.append( " " ).append( drawCircleActions );
		tools.append( " " ).append( drawEllipseActions );
		tools.append( " " ).append( drawCurveActions );

		pushMenus( menus.toString() );
		pushTools( tools.toString() );
	}

	private void unregisterActions() {
		pullMenus();
		pullTools();

		if( gridVisibleToggleHandler != null ) gridVisible().removeListener( gridVisibleToggleHandler );
		pullCommandAction( "grid-toggle" );
		if( snapGridToggleHandler != null ) gridSnapEnabled().removeListener( snapGridToggleHandler );
		pullCommandAction( "snap-grid-toggle" );

		pullCommandAction( "draw-path" );
		pullCommandAction( "draw-marker" );
		pullCommandAction( "draw-line-perpendicular" );
		pullCommandAction( "draw-line-2" );
		pullCommandAction( "draw-ellipse-arc-5" );
		pullCommandAction( "draw-ellipse-3" );
		pullCommandAction( "draw-curve-4" );
		//pullCommandAction( "draw-curve-3" );
		pullCommandAction( "draw-circle-diameter-2" );
		pullCommandAction( "draw-circle-3" );
		pullCommandAction( "draw-circle-2" );
		pullCommandAction( "draw-arc-3" );
		pullCommandAction( "draw-arc-2" );

		pullCommandAction( "shape-information" );
		pullCommandAction( "measure-point" );
		pullCommandAction( "measure-length" );
		pullCommandAction( "measure-distance" );
		pullCommandAction( "measure-angle" );

		pullAction( "print", printAction );
		pullAction( "properties", propertiesAction );
		pullAction( "delete", deleteAction );
		pullAction( "undo", undoAction );
		pullAction( "redo", redoAction );
	}

	private ProgramAction pushCommandAction( String key ) {
		return pushCommandAction( key, null );
	}

	private ProgramAction pushCommandAction( String key, String initialActionState ) {
		ActionProxy proxy = getProgram().getActionLibrary().getAction( key );
		ProgramAction action = commandActions.computeIfAbsent( key, k -> new CommandAction( getProgram(), proxy.getCommand() ) );
		if( initialActionState != null ) action.setState( initialActionState );
		pushAction( key, action );
		return action;
	}

	private void pullCommandAction( String key ) {
		pullAction( key, commandActions.get( key ) );
	}

	private List<DesignLayer> getFilteredLayers( Predicate<? super DesignLayer> filter ) {
		return getDesign().getAllLayers().stream().filter( filter ).collect( Collectors.toList() );
	}

	private void doSetSelectedLayerById( String id ) {
		getDesign().findLayerById( id ).ifPresent( this::setSelectedLayer );
		log.atConfig().log( "Selected layer: %s", id );
	}

	private void doSetCurrentLayerById( String id ) {
		getDesign().findLayerById( id ).ifPresent( y -> {
			currentLayerProperty().set( y );
			showPropertiesPage( y );
		} );
	}

	private void doSetCurrentViewById( String id ) {
		getDesign().findViewById( id ).ifPresent( v -> {
			currentViewProperty().set( v );
			//renderer.setView( v.getLayers(), v.getOrigin(), v.getZoom(), v.getRotate() );
			//showPropertiesPage( v );
		} );
	}

	private void doSetCurrentPrintById( String id ) {
		// TODO Implement DesignTool.doSetCurrentPrintById()
	}

	private void showPropertiesPage( DesignDrawable drawable ) {
		if( drawable != null ) showPropertiesPage( new NodeSettings( drawable ), drawable.getClass() );
	}

	private void showPropertiesPage( Settings settings, Class<? extends DesignDrawable> type ) {
//		SettingsPage page = designPropertiesMap.getSettingsPage( type );
//		if( page != null ) {
//			page.setSettings( settings );
//
//			// Switch to a task thread to get the tool
//			getProgram().getTaskManager().submit( Task.of( () -> {
//				try {
//					// Open the tool but don't make it the active tool
//					getProgram().getAssetManager().openAsset( ShapePropertiesAssetType.URI, true, false ).get();
//
//					// Fire the event on the FX thread
//					Fx.run( () -> getWorkspace().getEventBus().dispatch( new ShapePropertiesToolEvent( this, ShapePropertiesToolEvent.SHOW, page ) ) );
//				} catch( Exception exception ) {
//					log.atWarn( exception ).log();
//				}
//			} ) );
//		} else {
//			log.atError().log( "Unable to find properties page for %s", type.getName() );
//		}
	}

	private void hidePropertiesPage() {
		getWorkspace().getEventBus().dispatch( new ShapePropertiesToolEvent( this, ShapePropertiesToolEvent.HIDE, null ) );
	}

	private class CommandAction extends ProgramAction {

		private final String shortcut;

		protected CommandAction( Xenon program, String shortcut ) {
			super( program );
			this.shortcut = shortcut;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public void handle( ActionEvent event ) {
			getCommandContext().command( shortcut );
		}

	}

	private class PrintAction extends ProgramAction {

		protected PrintAction( Xenon program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public void handle( ActionEvent event ) {
			getProgram().getTaskManager().submit( new DesignPrintTask( getProgram(), DesignToolV2.this, getAsset(), (DesignPrint)null ) );
			//getProgram().getTaskManager().submit( new DesignAwtPrintTask( getProgram(), FxRenderDesignTool.this, getAsset(), (DesignPrint)null ) );
		}

	}

	private class PropertiesAction extends ProgramAction {

		protected PropertiesAction( Xenon program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public void handle( ActionEvent event ) {
			// Get the settings pages for the asset type
			Asset asset = getAsset();
			SettingsPage designSettingsPage = asset.getType().getSettingsPages().get( "asset" );
			SettingsPage assetSettingsPage = asset.getType().getSettingsPages().get( "grid" );

			Settings designSettings = new NodeSettings( getAsset().getModel() );
			Settings assetSettings = getAssetSettings();

			// Set the settings for the pages
			designSettingsPage.setSettings( designSettings );
			assetSettingsPage.setSettings( assetSettings );

			// Switch to a task thread to get the tool
			getProgram().getTaskManager().submit( Task.of( () -> {
				try {
					// Show the properties tool
					getProgram().getAssetManager().openAsset( PropertiesType.URI ).get();

					// Fire the show request on the workspace event bus
					Workspace workspace = getProgram().getWorkspaceManager().getActiveWorkspace();
					Fx.run( () -> workspace.getEventBus().dispatch( new PropertiesToolEvent( PropertiesAction.this, PropertiesToolEvent.SHOW, designSettingsPage, assetSettingsPage ) ) );
				} catch( Exception exception ) {
					log.atError( exception ).log();
				}
			} ) );
		}

	}

	private class DeleteAction extends ProgramAction {

		protected DeleteAction( Xenon program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return !selectedShapes().isEmpty();
		}

		@Override
		public void handle( ActionEvent event ) {
			getCommandContext().command( CommandMap.getActionCommand( "delete" ).getCommand() );
		}

	}

	private class UndoAction extends ProgramAction {

		protected UndoAction( Xenon program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return getAsset().getUndoManager().isUndoAvailable();
		}

		@Override
		public void handle( ActionEvent event ) {
			getAsset().getUndoManager().undo();
		}

	}

	private class RedoAction extends ProgramAction {

		protected RedoAction( Xenon program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return getAsset().getUndoManager().isRedoAvailable();
		}

		@Override
		public void handle( ActionEvent event ) {
			getAsset().getUndoManager().redo();
		}

	}

}
