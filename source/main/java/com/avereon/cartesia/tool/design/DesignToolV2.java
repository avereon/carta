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
import com.avereon.data.IdNode;
import com.avereon.data.MultiNodeSettings;
import com.avereon.data.NodeSettings;
import com.avereon.product.Rb;
import com.avereon.settings.Settings;
import com.avereon.util.DelayedAction;
import com.avereon.util.TypeReference;
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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
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

	private final Stack<DesignPortal> portalStack;

	private final DesignPropertiesMap designPropertiesMap;

	// TOOL PROPERTIES
	// The renderer might also have some properties that should be exposed

	private final ObjectProperty<Reticle> reticle;

	private final ObjectProperty<DesignValue> selectTolerance;

	private final ObjectProperty<DesignLayer> currentLayer;

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

		commandActions = new ConcurrentHashMap<>();
		designPropertiesMap = new DesignPropertiesMap( product );

		layersGuide = new LayersGuide( product, this );
		//		viewsGuide = new ViewsGuide( product, this );
		//		printsGuide = new PrintsGuide( product, this );

		// Create and associate the workplane and renderer
		renderer = new DesignRenderer();
		renderer.setApertureDrawPaint( DEFAULT_APERTURE_DRAW );
		renderer.setApertureFillPaint( DEFAULT_APERTURE_FILL );
		renderer.setPreviewDrawPaint( DEFAULT_PREVIEW_DRAW );
		renderer.setPreviewFillPaint( DEFAULT_PREVIEW_FILL );
		renderer.setSelectedDrawPaint( DEFAULT_SELECTED_DRAW );
		renderer.setSelectedFillPaint( DEFAULT_SELECTED_FILL );

		// TODO Move this to tool settings like reticle and aperture
		renderer.getWorkplane().setGridStyle( GridStyle.DOT );

		reticle = new SimpleObjectProperty<>( DEFAULT_RETICLE );
		selectTolerance = new SimpleObjectProperty<>( DEFAULT_SELECT_TOLERANCE );

		currentLayer = new SimpleObjectProperty<>();
		currentView = new SimpleObjectProperty<>();

		// Actions
		printAction = new PrintAction( product.getProgram() );
		propertiesAction = new PropertiesAction( product.getProgram() );
		deleteAction = new DeleteAction( product.getProgram() );
		undoAction = new UndoAction( product.getProgram() );
		redoAction = new RedoAction( product.getProgram() );

		storePreviousViewAction = new DelayedAction( getProgram().getTaskManager().getExecutor(), this::capturePreviousPortal );
		storePreviousViewAction.setMinTriggerLimit( 1000 );
		storePreviousViewAction.setMaxTriggerLimit( 5000 );
		portalStack = new Stack<>();

		// Create the toast label
		toast = new Label( Rb.text( RbKey.LABEL, "loading" ) + "..." );
		StackPane.setAlignment( toast, Pos.CENTER );

		// Add the components to the parent
		getChildren().addAll( renderer, toast );

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

		// Create the design context
		getDesign().createDesignContext( getProduct() );

		// Set the renderer design
		renderer.setDesign( getDesign() );
		renderer.setDpi( Screen.getPrimary().getDpi() );

		// Set defaults
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
		// Workplane settings
		// The workplane values are stored in the asset settings
		// However, a set of default workplane values may need to be put in the
		// asset settings because when a tool is closed, the tool settings are deleted.
		configureWorkplane( getWorkplane(), getAssetSettings() );

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

		// Restore the list of enabled layers
		Set<String> enabledLayerIds = settings.get( ENABLED_LAYERS, new TypeReference<>() {}, Set.of() );
		getDesign().getAllLayers().forEach( l -> setLayerEnabled( l, enabledLayerIds.contains( l.getId() ) ) );

		// Restore the list of visible layers
		Set<String> visibleLayerIds = settings.get( VISIBLE_LAYERS, new TypeReference<>() {}, Set.of() );
		getDesign().getAllLayers().forEach( l -> setLayerVisible( l, visibleLayerIds.contains( l.getId() ) ) );

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

		// Add enabled layers listener
		enabledLayers().addListener( this::doStoreEnabledLayers );

		// Add visible layers listener
		visibleLayers().addListener( this::doStoreVisibleLayers );

		// Add current layer property listener
		currentLayerProperty().addListener( ( p, o, n ) -> settings.set( CURRENT_LAYER, n.getId() ) );

		// Add selected layer property listener
		selectedLayerProperty().addListener( ( p, o, n ) -> showPropertiesPage( n ) );

		// Add current view property listener
		currentViewProperty().addListener( ( p, o, n ) -> settings.set( CURRENT_VIEW, n.getId() ) );

		// Add grid visible property listener
		gridVisible().addListener( ( p, o, n ) -> settings.set( GRID_VISIBLE, String.valueOf( n ) ) );

		// Add grid visible property listener
		gridSnapEnabled().addListener( ( p, o, n ) -> settings.set( GRID_SNAP_ENABLED, String.valueOf( n ) ) );

		//		// Add reference points visible property listener
		//		designPane.referenceLayerVisible().addListener( ( p, o, n ) -> settings.set( REFERENCE_LAYER_VISIBLE, String.valueOf( n ) ) );

		// Update the design context when the mouse moves
		addEventFilter( MouseEvent.MOUSE_MOVED, e -> getDesignContext().setMouse( e ) );
		getDesignContext().getPreviewShapes().addListener( this::onPreviewShapesChanged );
		getDesignContext().getSelectedShapes().addListener( this::onSelectedShapesChanged );

		// Update the select aperture when the mouse moves
		addEventFilter( MouseEvent.MOUSE_MOVED, e -> {
			if( getCommandContext().getCommandStackDepth() == 0 ) {
				setSelectAperture( new Point3D( e.getX(), e.getY(), e.getZ() ), new Point3D( e.getX(), e.getY(), e.getZ() ) );
			}
		} );

		//addEventFilter( KeyEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( MouseEvent.ANY, e -> getCommandContext().handle( e ) );
		//addEventFilter( MouseDragEvent.ANY, e -> getCommandContext().handle( e ) );
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

	}

	@Override
	protected void allocate() throws ToolException {
		super.allocate();

		// Add asset switch listener to remove command prompt
		getProgram().register( AssetSwitchedEvent.SWITCHED, assetSwitchListener = e -> {
			if( isDisplayed() && e.getOldAsset() == getAsset() && e.getNewAsset() != getAsset() ) {
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

		if(renderer!= null ) renderer.setDesign( null );

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
		return getDesignContext().getDesignCommandContext().getCommandPrompt();
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
		setView( center, zoom, getViewRotate() );
	}

	@Override
	public void setView( Point3D center, double zoom, double rotate ) {
		renderer.setViewpoint( CadPoints.toPoint2d( center ) );
		renderer.setZoom( CadPoints.toPoint2d( zoom, zoom ) );
		renderer.setRotate( rotate );
	}

	@Override
	public void setScreenViewport( Bounds viewport ) {
		Point3D worldCenter = screenToWorld( new Point3D( viewport.getCenterX(), viewport.getCenterY(), viewport.getCenterZ() ) );

		Bounds toolBounds = getLayoutBounds();
		double xZoom = Math.abs( toolBounds.getWidth() / viewport.getWidth() );
		double yZoom = Math.abs( toolBounds.getHeight() / viewport.getHeight() );
		double zoom = Math.min( xZoom, yZoom ) * getZoom();

		setView( worldCenter, zoom );
	}

	@Override
	public void setWorldViewport( Bounds viewport ) {
		setScreenViewport( worldToScreen( viewport ) );
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
		return renderer.selectAperture();
	}

	@Override
	public ObservableList<Shape> selectedFxShapes() {
		// This is the old FX shape implementation, just return an empty list.
		return FXCollections.observableArrayList();
	}

	@Deprecated
	@Override
	public Point3D nearestCp( Collection<Shape> shapes, Point3D point ) {
		throw new UnsupportedOperationException( "This method is not supported" );
	}

	@Override
	public Point3D nearestReferencePoint( Collection<DesignShape> shapes, Point3D point ) {
		// Use screen coordinates to determine "nearest" since that is what the user sees

		// Convert the world point to screen coordinates
		Point3D mouse = worldToScreen( point );

		// Go through all the reference points, convert them to screen coordinates and find the nearest
		double distance;
		double minDistance = Double.MAX_VALUE;
		Point3D nearest = CadPoints.NONE;

		for( DesignShape shape : shapes ) {
			if( shape == null || shape.isPreview() ) continue;

			List<Point3D> referencePoints = shape.getReferencePoints();
			for( Point3D cp : referencePoints ) {
				distance = mouse.distance( worldToScreen( cp ) );
				if( distance < minDistance ) {
					nearest = cp;
					minDistance = distance;
				}
			}
		}

		return nearest;
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
		if( layer == null ) throw new NullPointerException( "Layer cannot be null" );
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
			if( !renderer.visibleLayers().contains( layer ) ) renderer.visibleLayers().add( layer );
		} else {
			renderer.visibleLayers().remove( layer );
		}
	}

	@Override
	public List<DesignLayer> getVisibleLayers() {
		return new ArrayList<>( renderer.visibleLayers() );
	}

	public ObservableList<DesignLayer> visibleLayers() {
		return renderer.visibleLayers();
	}

	public List<DesignLayer> getEnabledLayers() {
		return new ArrayList<>( renderer.enabledLayers() );
	}

	public ObservableList<DesignLayer> enabledLayers() {
		return renderer.enabledLayers();
	}

	public void setLayerEnabled( DesignLayer layer, boolean visible ) {
		if( visible ) {
			renderer.enabledLayers().add( layer );
		} else {
			renderer.enabledLayers().remove( layer );
		}
	}

	public DesignLayer getPreviewLayer() {
		return renderer.getPreviewLayer();
	}

	@Override
	public List<Shape> getVisibleFxShapes() {
		return List.of();
	}

	@Override
	public List<DesignShape> getVisibleShapes() {
		return renderer.getVisibleShapes();
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
	@Deprecated
	public void pan( Point3D viewAnchor, Point3D dragAnchor, Point3D point ) {
		if( viewAnchor == null ) throw new NullPointerException( "View anchor cannot be null" );
		if( dragAnchor == null ) throw new NullPointerException( "Drag anchor cannot be null" );
		if( point == null ) throw new NullPointerException( "Point cannot be null" );
		Fx.run( () -> renderer.pan( viewAnchor, dragAnchor, point ) );
	}

	public Point3D scaleScreenToWorld( Point3D point ) {
		double scaleX = renderer.getInternalScaleX();
		double scaleY = renderer.getInternalScaleY();
		return new Point3D( point.getX() / scaleX, point.getY() / scaleY, point.getZ() );
	}

	public Point3D scaleWorldToScreen( Point3D point ) {
		double scaleX = renderer.getInternalScaleX();
		double scaleY = renderer.getInternalScaleY();
		return new Point3D( point.getX() * scaleX, point.getY() * scaleY, point.getZ() );
	}

	@Override
	public Point3D screenToWorkplane( Point3D point ) {
		return screenToWorkplane( point.getX(), point.getY(), point.getZ() );
	}

	@Override
	public Point3D screenToWorkplane( double x, double y, double z ) {
		Point3D worldPoint = screenToWorld( x, y, z );
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

	/**
	 * Set the select aperture window. Points are specified in screen coordinates.
	 *
	 * @param anchor The anchor point
	 * @param mouse The mouse point
	 */
	@Override
	public void setSelectAperture( Point3D anchor, Point3D mouse ) {
		if( anchor == null || mouse == null ) {
			renderer.setSelectAperture( null );
			return;
		}

		// Set the select aperture
		DesignShape selectAperture;
		if( anchor.equals( mouse ) ) {
			//selectAperture = null;
			//renderer.setSelectAperture( null );
			double size = renderer.realToScreen( getSelectTolerance() );
			selectAperture = new DesignEllipse( mouse, size );
		} else {
			// Calculate the bounds of the select window
			double x = Math.min( anchor.getX(), mouse.getX() );
			double y = Math.min( anchor.getY(), mouse.getY() );
			double w = Math.abs( anchor.getX() - mouse.getX() );
			double h = Math.abs( anchor.getY() - mouse.getY() );
			selectAperture = new DesignBox( x, y, w, h );
		}

		renderer.setSelectAperture( selectAperture );
	}

	@Override
	public List<DesignShape> screenPointSyncFindOne( Point3D mouse ) {
		// This is a finding operation
		return screenPointFind( mouse ).stream().findFirst().stream().collect( Collectors.toList() );
	}

	@Override
	public List<DesignShape> worldPointSyncFindOne( Point3D point ) {
		// This is a finding operation
		return worldPointFind( point ).stream().findFirst().stream().collect( Collectors.toList() );
	}

	@Override
	public List<DesignShape> screenPointSyncFindAll( Point3D mouse ) {
		// This is a finding operation
		return screenPointFind( mouse );
	}

	@Override
	public List<DesignShape> worldPointSyncFindAll( Point3D point ) {
		// This is a finding operation
		return worldPointFind( point );
	}

	@Override
	public List<DesignShape> screenPointSyncSelect( Point3D mouse ) {
		// This is a selecting operation
		screenPointSelect( mouse );
		return new ArrayList<>( getSelectedShapes() );
	}

	@Override
	public List<DesignShape> worldPointSyncSelect( Point3D point ) {
		// This is a selecting operation
		worldPointSelect( point );
		return new ArrayList<>( getSelectedShapes() );
	}

	@Override
	public void screenPointSelect( Point3D mouse ) {
		screenPointSelect( mouse, false );
	}

	@Override
	public void screenPointSelect( Point3D mouse, boolean toggle ) {
		worldPointSelect( renderer.parentToLocal( mouse ), toggle );
	}

	@Override
	public void screenWindowSelect( Point3D a, Point3D b, boolean intersect, boolean toggle ) {
		worldWindowSelect( renderer.parentToLocal( a ), renderer.parentToLocal( b ), intersect, toggle );
	}

	@Override
	public void worldPointSelect( Point3D point ) {
		worldPointSelect( point, false );
	}

	private List<DesignShape> screenPointFind( Point3D mouse ) {
		return worldPointFind( renderer.parentToLocal( mouse ) );
	}

	private List<DesignShape> worldPointFind( Point3D point ) {
		return renderer.worldPointFind( point, getSelectTolerance() );
	}

	@Override
	public void worldPointSelect( Point3D point, boolean toggle ) {
		List<DesignShape> shapes = worldPointFind( point );

		if( shapes.isEmpty() ) {
			setSelectedShapes( shapes, toggle );
		} else {
			setSelectedShapes( List.of( shapes.getFirst() ), toggle );
		}
	}

	public void worldWindowSelect( Point3D a, Point3D b, boolean intersect, boolean toggle ) {
		setSelectedShapes( renderer.worldWindowFind( a, b, intersect ), toggle );
	}

	private void setSelectedShapes( List<DesignShape> shapes, boolean toggle ) {
		ObservableList<DesignShape> selectedShapes = getDesignContext().getSelectedShapes();
		if( toggle ) {
			shapes.forEach( shape -> {
				if( shape.isSelected() ) {
					selectedShapes.remove( shape );
				} else {
					selectedShapes.add( shape );
				}
			} );
		} else {
			selectedShapes.setAll( shapes );
		}
	}

	@Override
	public void clearSelectedShapes() {
		getDesignContext().getSelectedShapes().clear();
	}

	@Override
	public List<DesignShape> getSelectedShapes() {
		return new ArrayList<>( getDesignContext().getSelectedShapes() );
	}

	@Override
	public DesignPortal getPriorPortal() {
		// Remove the current portal
		if( !portalStack.isEmpty() ) portalStack.pop();

		// Return the prior portal
		return portalStack.isEmpty() ? DesignPortal.DEFAULT : portalStack.pop();
	}

	private void configureWorkplane( DesignWorkplane workplane, Settings settings ) {
		workplane.setCoordinateSystem( Grid.valueOf( settings.get( DesignWorkplane.COORDINATE_SYSTEM, DesignWorkplane.DEFAULT_COORDINATE_SYSTEM.name() ).toUpperCase() ) );
		workplane.setOrigin( settings.get( DesignWorkplane.WORKPANE_ORIGIN, DesignWorkplane.DEFAULT_GRID_ORIGIN ) );

		workplane.setGridAxisVisible( settings.get( DesignWorkplane.GRID_AXIS_VISIBLE, Boolean.class, DesignWorkplane.DEFAULT_GRID_AXIS_VISIBLE ) );
		workplane.setGridAxisPaint( Paints.parseWithNullOnException( settings.get( DesignWorkplane.GRID_AXIS_PAINT, DesignWorkplane.DEFAULT_GRID_AXIS_PAINT ) ) );
		workplane.setGridAxisWidth( settings.get( DesignWorkplane.GRID_AXIS_WIDTH, DesignWorkplane.DEFAULT_GRID_AXIS_WIDTH ) );

		workplane.setMajorGridVisible( settings.get( DesignWorkplane.GRID_MAJOR_VISIBLE, Boolean.class, DesignWorkplane.DEFAULT_GRID_MAJOR_VISIBLE ) );
		workplane.setMajorGridX( settings.get( DesignWorkplane.GRID_MAJOR_X, DesignWorkplane.DEFAULT_GRID_MAJOR_SIZE ) );
		workplane.setMajorGridY( settings.get( DesignWorkplane.GRID_MAJOR_Y, DesignWorkplane.DEFAULT_GRID_MAJOR_SIZE ) );
		workplane.setMajorGridZ( settings.get( DesignWorkplane.GRID_MAJOR_Z, DesignWorkplane.DEFAULT_GRID_MAJOR_SIZE ) );
		workplane.setMajorGridPaint( Paints.parseWithNullOnException( settings.get( DesignWorkplane.GRID_MAJOR_PAINT, DesignWorkplane.DEFAULT_GRID_MAJOR_PAINT ) ) );
		workplane.setMajorGridWidth( settings.get( DesignWorkplane.GRID_MAJOR_WIDTH, DesignWorkplane.DEFAULT_GRID_MAJOR_WIDTH ) );

		workplane.setMinorGridVisible( settings.get( DesignWorkplane.GRID_MINOR_VISIBLE, Boolean.class, DesignWorkplane.DEFAULT_GRID_MINOR_VISIBLE ) );
		workplane.setMinorGridX( settings.get( DesignWorkplane.GRID_MINOR_X, DesignWorkplane.DEFAULT_GRID_MINOR_SIZE ) );
		workplane.setMinorGridY( settings.get( DesignWorkplane.GRID_MINOR_Y, DesignWorkplane.DEFAULT_GRID_MINOR_SIZE ) );
		workplane.setMinorGridZ( settings.get( DesignWorkplane.GRID_MINOR_Z, DesignWorkplane.DEFAULT_GRID_MINOR_SIZE ) );
		workplane.setMinorGridPaint( Paints.parseWithNullOnException( settings.get( DesignWorkplane.GRID_MINOR_PAINT, DesignWorkplane.DEFAULT_GRID_MINOR_PAINT ) ) );
		workplane.setMinorGridWidth( settings.get( DesignWorkplane.GRID_MINOR_WIDTH, DesignWorkplane.DEFAULT_GRID_MINOR_WIDTH ) );

		workplane.setSnapGridX( settings.get( DesignWorkplane.GRID_SNAP_X, DesignWorkplane.DEFAULT_GRID_SNAP_SIZE ) );
		workplane.setSnapGridY( settings.get( DesignWorkplane.GRID_SNAP_Y, DesignWorkplane.DEFAULT_GRID_SNAP_SIZE ) );
		workplane.setSnapGridZ( settings.get( DesignWorkplane.GRID_SNAP_Z, DesignWorkplane.DEFAULT_GRID_SNAP_SIZE ) );

		settings.register( DesignWorkplane.COORDINATE_SYSTEM, e -> workplane.setCoordinateSystem( Grid.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		settings.register( DesignWorkplane.GRID_ORIGIN, e -> workplane.setOrigin( String.valueOf( e.getNewValue() ) ) );

		settings.register( DesignWorkplane.GRID_AXIS_VISIBLE, e -> workplane.setGridAxisVisible( Boolean.parseBoolean( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_AXIS_PAINT, e -> workplane.setGridAxisPaint( Paints.parse( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_AXIS_WIDTH, e -> workplane.setGridAxisWidth( String.valueOf( e.getNewValue() ) ) );

		settings.register( DesignWorkplane.GRID_MAJOR_VISIBLE, e -> workplane.setMajorGridVisible( Boolean.parseBoolean( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_MAJOR_X, e -> workplane.setMajorGridX( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_MAJOR_Y, e -> workplane.setMajorGridY( String.valueOf( e.getNewValue() ) ) );
		//settings.register( DesignWorkplane.GRID_MAJOR_Z, e -> workplane.setMajorGridZ( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_MAJOR_PAINT, e -> workplane.setMajorGridPaint( Paints.parse( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_MAJOR_WIDTH, e -> workplane.setMajorGridWidth( String.valueOf( e.getNewValue() ) ) );

		settings.register( DesignWorkplane.GRID_MINOR_VISIBLE, e -> workplane.setMinorGridVisible( Boolean.parseBoolean( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_MINOR_X, e -> workplane.setMinorGridX( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_MINOR_Y, e -> workplane.setMinorGridY( String.valueOf( e.getNewValue() ) ) );
		//settings.register( DesignWorkplane.GRID_MINOR_Z, e -> workplane.setMinorGridZ( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_MINOR_PAINT, e -> workplane.setMinorGridPaint( Paints.parse( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_MINOR_WIDTH, e -> workplane.setMinorGridWidth( String.valueOf( e.getNewValue() ) ) );

		settings.register( DesignWorkplane.GRID_SNAP_X, e -> workplane.setSnapGridX( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_SNAP_Y, e -> workplane.setSnapGridY( String.valueOf( e.getNewValue() ) ) );
		//settings.register( DesignWorkplane.GRID_SNAP_Z, e -> workplane.setSnapGridZ( String.valueOf( e.getNewValue() ) ) );

		// Rebuild the grid if any workplane values change
		//workplane.register( NodeEvent.VALUE_CHANGED, e -> rebuildGridAction.request() );
	}

	private void capturePreviousPortal() {
		portalStack.push( new DesignPortal( getViewPoint(), getZoom(), getViewRotate() ) );
	}

	public void showCommandPrompt() {
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

	private void doStoreEnabledLayers( ListChangeListener.Change<? extends DesignLayer> c ) {
		c.next();
		getSettings().set( ENABLED_LAYERS, c.getList().stream().map( IdNode::getId ).collect( Collectors.toSet() ) );
	}

	private void doStoreVisibleLayers( ListChangeListener.Change<? extends DesignLayer> c ) {
		c.next();
		getSettings().set( VISIBLE_LAYERS, c.getList().stream().map( IdNode::getId ).collect( Collectors.toSet() ) );
	}

	private void onPreviewShapesChanged( ListChangeListener.Change<? extends DesignShape> c ) {
		// Set the preview property of the shape
		while( c.next() ) {
			c.getRemoved().forEach( s -> s.setSelected( false ) );
			c.getAddedSubList().forEach( s -> s.setSelected( true ) );
		}

		// Request a render
		renderer.render();
	}

	private void onSelectedShapesChanged( ListChangeListener.Change<? extends DesignShape> c ) {
		while( c.next() ) {
			c.getRemoved().forEach( s -> s.setSelected( false ) );
			c.getAddedSubList().forEach( s -> s.setSelected( true ) );

			int size = c.getList().size();

			if( size == 0 ) {
				showPropertiesPage( getSelectedLayer() );
			} else if( size == 1 ) {
				c.getList().stream().findFirst().ifPresent( this::showPropertiesPage );
			} else {
				// Show a combined properties page
				showPropertiesPage( new MultiNodeSettings( c.getList() ), DesignShape.class );
			}
		}

		// Request a render
		renderer.render();

		deleteAction.updateEnabled();
	}

	private void showPropertiesPage( DesignDrawable drawable ) {
		if( drawable != null ) showPropertiesPage( new NodeSettings( drawable ), drawable.getClass() );
	}

	private void showPropertiesPage( Settings settings, Class<? extends DesignDrawable> type ) {
		SettingsPage page = designPropertiesMap.getSettingsPage( type );
		if( page != null ) {
			// Switch to a task thread to get the tool
			getProgram().getTaskManager().submit( Task.of( () -> {
				try {
					// Open the tool but don't make it the active tool
					getProgram().getAssetManager().openAsset( ShapePropertiesAssetType.URI, true, false ).get();

					// Fire the event on the FX thread
					Fx.run( () -> getWorkspace().getEventBus().dispatch( new ShapePropertiesToolEvent( this, ShapePropertiesToolEvent.SHOW, page, settings ) ) );
				} catch( Exception exception ) {
					log.atWarn( exception ).log();
				}
			} ) );
		} else {
			log.atError().log( "Unable to find properties page for %s", type.getName() );
		}
	}

	private void hidePropertiesPage() {
		getWorkspace().getEventBus().dispatch( new ShapePropertiesToolEvent( this, ShapePropertiesToolEvent.HIDE ) );
	}

	/**
	 * General class for commands linked to actions.
	 */
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
			return getDesignContext() != null && !getSelectedShapes().isEmpty();
		}

		@Override
		public void handle( ActionEvent event ) {
			getCommandContext().command( getMod().getCommandMap().getCommandByAction( "delete" ).getCommand() );
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
