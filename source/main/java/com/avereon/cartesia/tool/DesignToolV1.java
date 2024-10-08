package com.avereon.cartesia.tool;

import com.avereon.cartesia.*;
import com.avereon.cartesia.cursor.Reticle;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.data.util.DesignPropertiesMap;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.snap.SnapGrid;
import com.avereon.cartesia.tool.view.DesignLayerPane;
import com.avereon.cartesia.tool.view.DesignPane;
import com.avereon.cartesia.tool.view.DesignShapeView;
import com.avereon.data.IdNode;
import com.avereon.data.MultiNodeSettings;
import com.avereon.data.NodeEvent;
import com.avereon.data.NodeSettings;
import com.avereon.settings.Settings;
import com.avereon.transaction.Txn;
import com.avereon.util.DelayedAction;
import com.avereon.util.TypeReference;
import com.avereon.util.UriUtil;
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
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;
import lombok.CustomLog;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// NEXT Remove DesignToolV1

@Deprecated
@CustomLog
public abstract class DesignToolV1 extends BaseDesignTool {

	private static final String REFERENCE_LAYER_VISIBLE = "";

	private static final Snap gridSnap = new SnapGrid();

	private static final double MINIMUM_GRID_PIXELS = 3.0;

	private final Map<String, ProgramAction> commandActions;

	private final DesignToolLayersGuide layersGuide;

	private final DesignToolViewsGuide viewsGuide;

	private final DesignToolPrintsGuide printsGuide;

	private final DesignPane designPane;

	private final Pane selectPane;

	private final SelectWindow selectWindow;

	private final ObjectProperty<DesignValue> selectAperture;

	private final ObservableList<Shape> selectedShapes;

	private final ObjectProperty<DesignLayer> currentLayer;

	private final ObjectProperty<DesignView> currentView;

	private final DesignPropertiesMap designPropertiesMap;

	private final PrintAction printAction;

	private final PropertiesAction propertiesAction;

	private final DeleteAction deleteAction;

	private final UndoAction undoAction;

	private final RedoAction redoAction;

	private final DelayedAction rebuildGridAction;

	private final DelayedAction storePreviousViewAction;

	private final DesignWorkplane workplane;

	private final Stack<DesignPortal> portalStack;

	private Reticle reticle;

	private BooleanProperty gridSnapEnabled;

	private ChangeListener<Boolean> gridVisibleToggleHandler;

	private ChangeListener<Boolean> snapGridToggleHandler;

	private com.avereon.event.EventHandler<AssetSwitchedEvent> assetSwitchListener;

	public DesignToolV1( XenonProgramProduct product, Asset asset ) {
		super( product, asset );
		addStylesheet( CartesiaMod.STYLESHEET );
		getStyleClass().add( "design-tool" );

		this.designPropertiesMap = new DesignPropertiesMap( product );
		this.commandActions = new ConcurrentHashMap<>();

		this.layersGuide = new DesignToolLayersGuide( product, this );
		this.viewsGuide = new DesignToolViewsGuide( product, this );
		this.printsGuide = new DesignToolPrintsGuide( product, this );
		getGuideContext().getGuides().addAll( layersGuide, viewsGuide, printsGuide );
		getGuideContext().setCurrentGuide( layersGuide );

		this.designPane = new DesignPane();
		this.selectAperture = new SimpleObjectProperty<>();
		this.currentLayer = new SimpleObjectProperty<>();
		this.currentView = new SimpleObjectProperty<>();

		this.printAction = new PrintAction( product.getProgram() );
		this.propertiesAction = new PropertiesAction( product.getProgram() );
		this.deleteAction = new DeleteAction( product.getProgram() );
		this.undoAction = new UndoAction( product.getProgram() );
		this.redoAction = new RedoAction( product.getProgram() );

		this.rebuildGridAction = new DelayedAction( getProgram().getTaskManager().getExecutor(), this::doRebuildGrid );
		this.rebuildGridAction.setMinTriggerLimit( 100 );
		this.rebuildGridAction.setMaxTriggerLimit( 500 );

		this.storePreviousViewAction = new DelayedAction( getProgram().getTaskManager().getExecutor(), this::capturePreviousPortal );
		this.storePreviousViewAction.setMinTriggerLimit( 1000 );
		this.storePreviousViewAction.setMaxTriggerLimit( 5000 );

		this.workplane = new DesignWorkplane();
		this.portalStack = new Stack<>();

		this.selectedShapes = FXCollections.observableArrayList();
		this.selectedShapes.addListener( (ListChangeListener<? super Shape>)this::doSelectedShapesChanged );
		this.selectWindow = new SelectWindow();
		this.selectWindow.getStyleClass().add( "select" );
		this.selectPane = new Pane();
		this.selectPane.getChildren().addAll( selectWindow );

		getChildren().addAll( designPane, selectPane );

		designPane.prefWidthProperty().bind( widthProperty() );
		designPane.prefHeightProperty().bind( heightProperty() );

		// NOTE Settings and settings listeners should go in the ready() method

		// TODO If the fragment of the URI is "print" more components need to be added
		String fragment = UriUtil.parseFragment( asset.getUri() );
		if( fragment != null && fragment.startsWith( "print" ) ) {
			// Should be in the format 'print=<uuid>'

			// Get the print identifier
			Map<String, String> parameters = UriUtil.parseQuery( fragment );
			String identifier = parameters.get( "print" );
		}
	}

	@Override
	public Point3D getViewpoint() {
		return designPane == null ? Point3D.ZERO : designPane.getViewPoint();
	}

	@Override
	public void setViewPoint( Point3D point ) {
		setView( point, getZoom() );
	}

	@Override
	public double getViewRotate() {
		return designPane == null ? 0.0 : designPane.getViewRotate();
	}

	@Override
	public void setViewRotate( double angle ) {
		setView( getViewpoint(), getZoom(), angle );
	}

	@Override
	public double getZoom() {
		return designPane == null ? 1.0 : designPane.getZoom();
	}

	@Override
	public void setZoom( double zoom ) {
		setView( getViewpoint(), zoom );
	}

	@Override
	public DoubleProperty zoomXProperty() {
		return null;
	}

	@Override
	public DoubleProperty zoomYProperty() {
		return null;
	}

	@Override
	public final ReticleCursor getReticleCursor() {
		return reticle.getCursor( getProgram() );
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
		// NOTE Running this on the FX thread can cause race conditions when used together with the single value set methods.
		if( designPane != null ) Fx.run( () -> designPane.setView( center, zoom, rotate ) );
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
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSelectTolerance( DesignValue aperture ) {
		selectAperture.set( aperture );
	}

	@Override
	public DesignValue getSelectTolerance() {
		return selectAperture.get();
	}

	@Override
	public ObjectProperty<DesignValue> selectTolerance() {
		return selectAperture;
	}

	@Override
	public ObservableList<Shape> selectedFxShapes() {
		return selectedShapes;
	}

	@Override
	public Point3D nearestCp( Collection<Shape> shapes, Point3D point ) {
		Point3D mouse = worldToScreen( point );

		// Go through all the reference points, convert them to screen coordinates and find the nearest
		double distance;
		double minDistance = Double.MAX_VALUE;
		Point3D nearest = CadPoints.NONE;

		for( Shape shape : shapes ) {
			DesignShape data = DesignShapeView.getDesignData( shape );
			if( data == null || data.isPreview() ) continue;
			List<ConstructionPoint> cps = DesignShapeView.getConstructionPoints( shape );
			for( ConstructionPoint cp : cps ) {
				distance = mouse.distance( worldToScreen( cp.getLayoutX(), cp.getLayoutY(), 0 ) );
				if( distance < minDistance ) {
					nearest = cp.getLocation();
					minDistance = distance;
				}
			}
		}

		return nearest;
	}

	@Override
	public Point3D nearestReferencePoint( Collection<DesignShape> shapes, Point3D point ) {
		return null;
	}

	@Override
	public void setCurrentLayer( DesignLayer layer ) {
		currentLayer.set( layer );
	}

	@Override
	public DesignLayer getCurrentLayer() {
		return currentLayer.get();
	}

	@Override
	public ObjectProperty<DesignLayer> currentLayerProperty() {
		return currentLayer;
	}

	private void setLayerEnabled( DesignLayer layer, boolean enabled ) {
		designPane.setLayerEnabled( layer, enabled );
	}

	@Override
	public boolean isLayerVisible( DesignLayer layer ) {
		return designPane.isLayerVisible( layer );
	}

	@Override
	public void setLayerVisible( DesignLayer layer, boolean visible ) {
		if( visible ) {
			// Show the layer and parent layers
			while( !layer.isRootLayer() ) {
				designPane.setLayerVisible( layer, visible );
				layer = layer.getLayer();
			}
		} else {
			// Just hide the layer
			designPane.setLayerVisible( layer, visible );
		}
	}

	List<String> getVisibleLayerIds() {
		return getFilteredLayers( DesignLayerPane::isVisible ).stream().map( IdNode::getId ).collect( Collectors.toList() );
	}

	@Override
	public List<DesignLayer> getVisibleLayers() {
		return getFilteredLayers( DesignLayerPane::isVisible );
	}

	private List<DesignLayer> getFilteredLayers( Predicate<? super DesignLayerPane> filter ) {
		return designPane.getLayers().stream().filter( filter ).map( y -> (DesignLayer)DesignShapeView.getDesignData( y ) ).collect( Collectors.toList() );
	}

	public DesignLayer getPreviewLayer() {
		return new DesignLayer();
	}

	public DesignLayer getReferenceLayer() {
		return new DesignLayer();
	}

	@Override
	@Deprecated
	public List<Shape> getVisibleFxShapes() {
		return designPane.getVisibleShapes();
	}

	@Override
	public List<DesignShape> getVisibleShapes() {
		return List.of();
	}

	@Override
	public Paint getSelectedDrawPaint() {
		return designPane.getSelectDrawPaint();
	}

	@Override
	public Paint getSelectedFillPaint() {
		return designPane.getSelectFillPaint();
	}

	@Override
	public boolean isReferenceLayerVisible() {
		return designPane.isReferenceLayerVisible();
	}

	@Override
	public void setReferenceLayerVisible( boolean visible ) {
		Fx.run( () -> designPane.setReferenceLayerVisible( visible ) );
	}

	@Override
	public void setCurrentView( DesignView view ) {
		currentView.set( view );
	}

	@Override
	public DesignView getCurrentView() {
		return currentView.get();
	}

	@Override
	public ObjectProperty<DesignView> currentViewProperty() {
		return currentView;
	}

	@Override
	public void zoom( Point3D anchor, double factor ) {
		Fx.run( () -> designPane.zoom( anchor, factor ) );
	}

	@Override
	public void pan( Point3D viewAnchor, Point3D dragAnchor, Point3D point ) {
		Fx.run( () -> designPane.mousePan( viewAnchor, dragAnchor, point ) );
	}

	public Point3D scaleScreenToWorld( Point3D point ) {
		return null;
	}

	public Point3D scaleWorldToScreen( Point3D point ) {
		return null;
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
	public Point3D snapToWorkplane( Point3D point ) {
		return point;
	}

	@Override
	public Point3D snapToWorkplane( double x, double y, double z ) {
		return snapToWorkplane( new Point3D( x, y, z ) );
	}

	@Override
	public Point3D worldToScreen( double x, double y, double z ) {
		return designPane == null ? Point3D.ZERO : designPane.localToParent( x, y, z );
	}

	@Override
	public Point3D worldToScreen( Point3D point ) {
		return designPane == null ? Point3D.ZERO : designPane.localToParent( point );
	}

	@Override
	public Bounds worldToScreen( Bounds bounds ) {
		// NOTE Flip the Y coordinate because we are converting from world to screen coordinates
		Point3D a = worldToScreen( bounds.getMinX(), bounds.getMaxY(), bounds.getMinZ() );
		Point3D b = worldToScreen( bounds.getMaxX(), bounds.getMinY(), bounds.getMaxZ() );
		return new BoundingBox( a.getX(), a.getY(), a.getZ(), b.getX() - a.getX(), b.getY() - a.getY(), b.getZ() - a.getZ() );
	}

	@Override
	public Point3D screenToWorld( double x, double y, double z ) {
		return designPane == null ? Point3D.ZERO : designPane.parentToLocal( x, y, z );
	}

	@Override
	public Point3D screenToWorld( Point3D point ) {
		return designPane == null ? Point3D.ZERO : designPane.parentToLocal( point );
	}

	@Override
	public Bounds screenToWorld( Bounds bounds ) {
		// NOTE Flip the Y coordinate because we are converting from screen to world coordinates
		Point3D a = screenToWorld( bounds.getMinX(), bounds.getMaxY(), bounds.getMinZ() );
		Point3D b = screenToWorld( bounds.getMaxX(), bounds.getMinY(), bounds.getMaxZ() );
		return new BoundingBox( a.getX(), a.getY(), a.getZ(), b.getX() - a.getX(), b.getY() - a.getY(), b.getZ() - a.getZ() );
	}

	@Override
	public boolean isGridVisible() {
		return getDesignPane().isGridVisible();
	}

	@Override
	public void setGridVisible( boolean visible ) {
		getDesignPane().setGridVisible( visible );
		if( visible ) rebuildGridAction.request();
	}

	@Override
	public BooleanProperty gridVisible() {
		return getDesignPane().gridVisible();
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
	protected void ready( OpenAssetRequest request ) throws ToolException {
		super.ready( request );

		setTitle( getAsset().getName() );
		setGraphic( getProgram().getIconLibrary().getIcon( getProduct().getCard().getArtifact() ) );

		getAsset().getUndoManager().undoAvailableProperty().addListener( ( v, o, n ) -> undoAction.updateEnabled() );
		getAsset().getUndoManager().redoAvailableProperty().addListener( ( v, o, n ) -> redoAction.updateEnabled() );
		getAsset().register( Asset.NAME, e -> setTitle( e.getNewValue() ) );
		getAsset().register( Asset.ICON, e -> setIcon( e.getNewValue() ) );

		Design design = getDesign();
		if( design != null ) {
			// Create the design context
			design.createDesignContext( getProduct() );

			// Link the command context to this tool
			getCommandContext().setTool( this );

			// Link the guides before loading the design
			layersGuide.link();
			viewsGuide.link();
			printsGuide.link();
		}

		Fx.run( () -> {
			designPane.setDpi( Screen.getPrimary().getDpi() );
			if( design != null ) designPane.setDesign( design );
		} );

		// Keep the design pane centered when resizing
		// These should be added before updating the pan and zoom
		widthProperty().addListener( ( p, o, n ) -> Fx.run( designPane::updateView ) );
		heightProperty().addListener( ( p, o, n ) -> Fx.run( designPane::updateView ) );

		// NOTE What listeners should be registered before configuring things???
		// There are two ways of handing the initialization of properties that change
		// 1. Initialize the properties from settings, then add listeners afterward.
		//    This is safer, but does not allow the listener logic to be triggered.
		// 2. Add listeners, then initialize the properties. This approach has the
		//    benefit of triggering the listeners, but, depending on the listener,
		//    that could be problematic if the listener gets caught in a loop.
		//    Another downside to this approach is that several listeners might call
		//    the same logic (possibly expensive logic) causing an unnecessary
		//    delay.

		// Workplane settings
		// The workplane values are stored in the asset settings
		// However, a set of default workplane values may need to be put in the
		// asset settings because when a tool is closed, the tool settings are deleted.
		configureWorkplane( getWorkplane(), getAssetSettings() );

		Settings productSettings = getProduct().getSettings();
		Settings settings = getSettings();
		String defaultSelectSize = "2";
		String defaultSelectUnit = DesignUnit.CENTIMETER.name().toLowerCase();
		String defaultReferencePointType = DesignMarker.Type.CIRCLE.name().toLowerCase();
		String defaultReferencePointSize = "10";
		String defaultReferencePointPaint = "#808080";
		String defaultReticle = Reticle.DUPLEX.name().toLowerCase();

		// Get tool settings
		double selectApertureSize = Double.parseDouble( productSettings.get( SELECT_APERTURE_SIZE, defaultSelectSize ) );
		DesignUnit selectApertureUnit = DesignUnit.valueOf( productSettings.get( SELECT_APERTURE_UNIT, defaultSelectUnit ).toUpperCase() );
		DesignMarker.Type referencePointType = DesignMarker.Type.valueOf( productSettings.get( REFERENCE_POINT_TYPE, defaultReferencePointType ).toUpperCase() );
		double referencePointSize = Double.parseDouble( productSettings.get( REFERENCE_POINT_SIZE, defaultReferencePointSize ) );
		Paint referencePointPaint = Paints.parse( productSettings.get( REFERENCE_POINT_PAINT, defaultReferencePointPaint ) );

		Point3D viewPoint = ParseUtil.parsePoint3D( settings.get( SETTINGS_VIEW_POINT, "0,0,0" ) );
		double viewZoom = Double.parseDouble( settings.get( SETTINGS_VIEW_ZOOM, "1.0" ) );
		double viewRotate = Double.parseDouble( settings.get( SETTINGS_VIEW_ROTATE, "0.0" ) );
		if( design != null ) setView( viewPoint, viewZoom, viewRotate );
		setReticle( Reticle.valueOf( productSettings.get( RETICLE, defaultReticle ).toUpperCase() ) );
		setSelectTolerance( new DesignValue( selectApertureSize, selectApertureUnit ) );
		designPane.setReferencePointType( referencePointType );
		designPane.setReferencePointSize( referencePointSize );
		designPane.setReferencePointPaint( referencePointPaint );

		if( design != null ) {
			design.findLayers( DesignLayer.ID, settings.get( CURRENT_LAYER, "" ) ).stream().findFirst().ifPresent( this::setCurrentLayer );
			design.findViews( DesignView.ID, settings.get( CURRENT_VIEW, "" ) ).stream().findFirst().ifPresent( this::setCurrentView );

			// Restore the list of enabled layers
			Set<String> enabledLayerIds = settings.get( ENABLED_LAYERS, new TypeReference<>() {}, Set.of() );
			design.getAllLayers().forEach( l -> designPane.setLayerEnabled( l, enabledLayerIds.contains( l.getId() ) ) );

			// Restore the list of visible layers
			Set<String> visibleLayerIds = settings.get( VISIBLE_LAYERS, new TypeReference<>() {}, Set.of() );
			design.getAllLayers().forEach( l -> designPane.setLayerVisible( l, visibleLayerIds.contains( l.getId() ) ) );
		}

		// Restore the grid visible flag
		setGridVisible( Boolean.parseBoolean( settings.get( GRID_VISIBLE, DEFAULT_GRID_VISIBLE ) ) );

		// Restore the grid snap enabled flag
		setGridSnapEnabled( Boolean.parseBoolean( settings.get( GRID_SNAP_ENABLED, DEFAULT_GRID_SNAP_ENABLED ) ) );

		// Restore the reference view visibility
		setReferenceLayerVisible( Boolean.parseBoolean( settings.get( REFERENCE_LAYER_VISIBLE, Boolean.TRUE.toString() ) ) );

		// Settings listeners
		productSettings.register( RETICLE, e -> setReticle( Reticle.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		productSettings.register( SELECT_APERTURE_SIZE, e -> setSelectTolerance( new DesignValue( Double.parseDouble( (String)e.getNewValue() ), getSelectTolerance().getUnit() ) ) );
		productSettings.register( SELECT_APERTURE_UNIT, e -> setSelectTolerance( new DesignValue( getSelectTolerance().getValue(), DesignUnit.valueOf( ((String)e.getNewValue()).toUpperCase() ) ) ) );
		productSettings.register( REFERENCE_POINT_TYPE, e -> designPane.setReferencePointType( DesignMarker.Type.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		productSettings.register( REFERENCE_POINT_SIZE, e -> designPane.setReferencePointSize( Double.parseDouble( (String)e.getNewValue() ) ) );
		productSettings.register( REFERENCE_POINT_PAINT, e -> designPane.setReferencePointPaint( Paints.parse( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );

		// Add layout bounds property listener
		layoutBoundsProperty().addListener( ( p, o, n ) -> doUpdateGridBounds() );

		// Add view point property listener
		designPane.viewPointProperty().addListener( ( p, o, n ) -> {
			storePreviousViewAction.request();
			settings.set( SETTINGS_VIEW_POINT, n.getX() + "," + n.getY() + "," + n.getZ() );
			doUpdateGridBounds();
		} );

		// Add view rotate property listener
		designPane.viewRotateProperty().addListener( ( p, o, n ) -> {
			storePreviousViewAction.request();
			settings.set( SETTINGS_VIEW_ROTATE, n.doubleValue() );
			doUpdateGridBounds();
		} );

		// Add view zoom property listener
		designPane.zoomProperty().addListener( ( p, o, n ) -> {
			storePreviousViewAction.request();
			getCoordinateStatus().updateZoom( n.doubleValue() );
			settings.set( SETTINGS_VIEW_ZOOM, n.doubleValue() );
			doUpdateGridBounds();
		} );

		// Add visible layers listener
		designPane.enabledLayersProperty().addListener( this::doStoreEnabledLayers );

		// Add visible layers listener
		designPane.visibleLayersProperty().addListener( this::doStoreVisibleLayers );

		// Add current layer property listener
		currentLayerProperty().addListener( ( p, o, n ) -> settings.set( CURRENT_LAYER, n.getId() ) );

		// Add current view property listener
		currentViewProperty().addListener( ( p, o, n ) -> settings.set( CURRENT_VIEW, n.getId() ) );

		// Add grid visible property listener
		gridVisible().addListener( ( p, o, n ) -> settings.set( GRID_VISIBLE, String.valueOf( n ) ) );

		// Add grid visible property listener
		gridSnapEnabled().addListener( ( p, o, n ) -> settings.set( GRID_SNAP_ENABLED, String.valueOf( n ) ) );

		// Add reference points visible property listener
		designPane.referenceLayerVisible().addListener( ( p, o, n ) -> settings.set( REFERENCE_LAYER_VISIBLE, String.valueOf( n ) ) );

		addEventFilter( MouseEvent.MOUSE_MOVED, e -> getDesignContext().setMouse( e ) );

		//addEventFilter( KeyEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( MouseEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( MouseDragEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ScrollEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ZoomEvent.ANY, e -> getCommandContext().handle( e ) );

		if( design != null ) getCoordinateStatus().updateZoom( getZoom() );
		designPane.updateView();
		doUpdateGridBounds();
	}

	@Override
	protected void guideNodesSelected( Set<GuideNode> oldNodes, Set<GuideNode> newNodes ) {
		if( getCurrentGuide() == layersGuide ) {
			newNodes.stream().findFirst().ifPresent( n -> doSetCurrentLayerById( n.getId() ) );
		} else if( getCurrentGuide() == viewsGuide ) {
			newNodes.stream().findFirst().ifPresent( n -> doSetCurrentViewById( n.getId() ) );
		} else if( getCurrentGuide() == printsGuide ) {
			newNodes.stream().findFirst().ifPresent( n -> doSetCurrentPrintById( n.getId() ) );
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
			// FIXME #113 Design tool activate does not show coordinate status
			if( isDisplayed() && e.getOldAsset() == this.getAsset() && e.getNewAsset() != this.getAsset() ) {
				unregisterStatusBarItems();
			}
		} );
	}

	@Override
	protected void activate() throws ToolException {
		super.activate();

		getCommandContext().setLastActiveDesignTool( this );

		registerStatusBarItems();
		registerCommandCapture();
		registerActions();

		getCommandContext().setTool( this );
		requestFocus();
	}

	@Override
	protected void conceal() throws ToolException {
		unregisterCommandCapture();
		unregisterActions();
		pullMenus();
		pullTools();
		super.conceal();
	}

	@Override
	protected void deallocate() throws ToolException {
		// Remove asset switch listener to unregister status bar items
		getProgram().unregister( AssetSwitchedEvent.SWITCHED, assetSwitchListener );

		super.deallocate();
	}

	final DesignPane getDesignPane() {
		return designPane;
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

	private void setReticle( Reticle reticle ) {
		this.reticle = reticle;
		if( getCursor() instanceof ReticleCursor ) setCursor( reticle.getCursor( getProgram() ) );
	}

	private CommandPrompt getCommandPrompt() {
		return getDesignContext().getDesignCommandContext().getCommandPrompt();
	}

	private CoordinateStatus getCoordinateStatus() {
		return getDesignContext().getCoordinateStatus();
	}

	private void doStoreEnabledLayers( SetChangeListener.Change<? extends DesignLayer> c ) {
		getSettings().set( ENABLED_LAYERS, c.getSet().stream().map( IdNode::getId ).collect( Collectors.toSet() ) );
	}

	private void doStoreVisibleLayers( SetChangeListener.Change<? extends DesignLayer> c ) {
		getSettings().set( VISIBLE_LAYERS, c.getSet().stream().map( IdNode::getId ).collect( Collectors.toSet() ) );
	}

	@Override
	public void setSelectAperture( Point3D anchor, Point3D mouse ) {
		if( anchor == null ) return;
		double x = Math.min( anchor.getX(), mouse.getX() );
		double y = Math.min( anchor.getY(), mouse.getY() );
		double w = Math.abs( anchor.getX() - mouse.getX() );
		double h = Math.abs( anchor.getY() - mouse.getY() );
		if( w == 0 || h == 0 ) {
			Fx.run( selectWindow::hide );
		} else {
			Fx.run( () -> selectWindow.resizeRelocate( x, y, w, h ) );
		}
	}

	@Override
	public List<DesignShape> screenPointSyncFindOne( Point3D mouse ) {
		throw new UnsupportedOperationException( "This class does not return DesignShapes" );
	}

	@Override
	public List<DesignShape> worldPointSyncFindOne( Point3D mouse ) {
		throw new UnsupportedOperationException( "This class does not return DesignShapes" );
	}

	@Override
	public List<DesignShape> screenPointSyncFindAll( Point3D mouse ) {
		throw new UnsupportedOperationException( "This class does not return DesignShapes" );
	}

	@Override
	public List<DesignShape> worldPointSyncFindAll( Point3D mouse ) {
		throw new UnsupportedOperationException( "This class does not return DesignShapes" );
	}

	@Override
	public List<DesignShape> screenPointSyncSelect( Point3D mouse ) {
		throw new UnsupportedOperationException( "This class does not return DesignShapes" );
	}

	@Override
	public List<DesignShape> worldPointSyncSelect( Point3D mouse ) {
		throw new UnsupportedOperationException( "This class does not return DesignShapes" );
	}

	@Override
	public void screenPointSelect( Point3D mouse ) {
		screenPointSelect( mouse, false );
	}

	@Override
	public void screenPointSelect( Point3D mouse, boolean toggle ) {
		if( !toggle ) selectedFxShapes().clear();

		designPane.screenPointSelect( mouse, getSelectTolerance() ).stream().findFirst().ifPresent( shape -> {
			if( toggle && DesignShapeView.getDesignData( shape ).isSelected() ) {
				selectedFxShapes().remove( shape );
			} else {
				selectedFxShapes().add( shape );
			}
		} );
	}

	@Override
	public void screenWindowSelect( Point3D a, Point3D b, boolean intersect, boolean toggle ) {
		Fx.run( () -> {
			selectedFxShapes().clear();
			selectedFxShapes().addAll( designPane.screenWindowSelect( a, b, intersect ) );
		} );
	}

	@Override
	public void worldPointSelect( Point3D point ) {
		worldPointSelect( point, false );
	}

	@Override
	public void worldPointSelect( Point3D point, boolean toggle ) {
		Fx.run( () -> {
			if( !toggle ) selectedFxShapes().clear();

			List<Shape> selection = designPane.worldPointSelect( point, getSelectTolerance() );
			selection.stream().findFirst().ifPresent( shape -> {
				if( toggle && DesignShapeView.getDesignData( shape ).isSelected() ) {
					selectedFxShapes().remove( shape );
				} else {
					selectedFxShapes().add( shape );
				}
			} );
		} );
	}

	@Override
	public void worldWindowSelect( Point3D a, Point3D b, boolean intersect, boolean toggle ) {
		Fx.run( () -> {
			selectedFxShapes().clear();
			selectedFxShapes().addAll( designPane.worldWindowSelect( a, b, intersect ) );
		} );
	}

	@Override
	public void clearSelectedShapes() {
		selectedFxShapes().clear();
	}

	@Override
	public List<DesignShape> getSelectedShapes() {
		return selectedFxShapes().stream().map( DesignShapeView::getDesignData ).collect( Collectors.toList() );
	}

	private void configureWorkplane( DesignWorkplane workplane, Settings settings ) {
		workplane.setCoordinateSystem( Grid.valueOf( settings.get( DesignWorkplane.COORDINATE_SYSTEM, DesignWorkplane.DEFAULT_COORDINATE_SYSTEM.name() ).toUpperCase() ) );
		workplane.setOrigin( settings.get( "workpane-origin", DesignWorkplane.DEFAULT_GRID_ORIGIN ) );

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
		workplane.register( NodeEvent.VALUE_CHANGED, e -> rebuildGridAction.request() );
	}

	private void doUpdateGridBounds() {
		DesignWorkplane workplane = getWorkplane();
		Bounds majorGridBounds = new BoundingBox( 0, 0, workplane.calcMajorGridX(), workplane.calcMajorGridY() );
		Bounds minorGridBounds = new BoundingBox( 0, 0, workplane.calcMinorGridX(), workplane.calcMinorGridY() );

		Fx.run( () -> {
			Bounds majorBounds = designPane.localToParent( majorGridBounds );
			Bounds minorBounds = designPane.localToParent( minorGridBounds );
			Bounds bounds = designPane.parentToLocal( getLayoutBounds() );

			boolean showMajorGridForSettings = getAssetSettings().get( DesignWorkplane.GRID_MAJOR_VISIBLE, Boolean.class, true );
			boolean showMinorGridForSettings = getAssetSettings().get( DesignWorkplane.GRID_MINOR_VISIBLE, Boolean.class, true );
			boolean showMajorGridForBounds = majorBounds.getWidth() > MINIMUM_GRID_PIXELS && majorBounds.getHeight() > MINIMUM_GRID_PIXELS;
			boolean showMinorGridForBounds = minorBounds.getWidth() > MINIMUM_GRID_PIXELS && minorBounds.getHeight() > MINIMUM_GRID_PIXELS;

			// Updating some workplane values should cause the grid to be rebuilt
			Txn.run( () -> {
				workplane.setBounds( bounds );
				workplane.setMajorGridShowing( showMajorGridForSettings && showMajorGridForBounds );
				workplane.setMinorGridShowing( showMinorGridForSettings && showMinorGridForBounds );
			} );
		} );
	}

	/**
	 * Should only be called by triggering the {@link #rebuildGridAction}.
	 */
	private void doRebuildGrid() {
		if( !isGridVisible() ) return;

		getProgram().getTaskManager().submit( Task.of( "Rebuild grid", () -> {
			try {
				List<Shape> grid = getCoordinateSystem().createFxGeometryGrid( getWorkplane() );
				Fx.run( () -> designPane.setGrid( grid ) );
			} catch( Exception exception ) {
				log.atError().withCause( exception ).log( "Error creating grid" );
			}
		} ) );
	}

	private void doSetCurrentLayerById( String id ) {
		getDesign().findLayers( DesignLayer.ID, id ).stream().findFirst().ifPresent( y -> {
			currentLayerProperty().set( y );
			showPropertiesPage( y );
		} );
	}

	private void doSetCurrentViewById( String id ) {
		getDesign().findViews( DesignView.ID, id ).stream().findFirst().ifPresent( v -> {
			currentViewProperty().set( v );
			getDesignPane().setView( v.getLayers(), v.getOrigin(), v.getZoom(), v.getRotate() );
			//showPropertiesPage( v );
		} );
	}

	private void doSetCurrentPrintById( String id ) {
		// TODO Implement DesignTool.doSetCurrentPrintById()
	}

	private void doSelectedShapesChanged( ListChangeListener.Change<? extends Shape> c ) {
		while( c.next() ) {
			c.getRemoved().stream().map( BaseDesignTool::getDesignData ).forEach( s -> s.setSelected( false ) );
			c.getAddedSubList().stream().map( BaseDesignTool::getDesignData ).forEach( s -> s.setSelected( true ) );

			int size = c.getList().size();

			if( size == 0 ) {
				showPropertiesPage( getCurrentLayer() );
			} else if( size == 1 ) {
				c.getList().stream().findFirst().map( BaseDesignTool::getDesignData ).ifPresent( this::showPropertiesPage );
			} else {
				// Show a combined properties page
				Set<DesignDrawable> designData = c.getList().parallelStream().map( BaseDesignTool::getDesignData ).collect( Collectors.toSet() );
				showPropertiesPage( new MultiNodeSettings( designData ), DesignShape.class );
			}
		}
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
					Fx.run( () -> getWorkspace().getEventBus().dispatch( new ShapePropertiesToolEvent( DesignToolV1.this, ShapePropertiesToolEvent.SHOW, page, settings ) ) );
				} catch( Exception exception ) {
					log.atWarn( exception ).log();
				}
			} ) );
		} else {
			log.atError().log( "Unable to find properties page for %s", type.getName() );
		}
	}

	private void hidePropertiesPage() {
		getWorkspace().getEventBus().dispatch( new ShapePropertiesToolEvent( DesignToolV1.this, ShapePropertiesToolEvent.HIDE ) );
	}

	@Override
	public DesignPortal getPriorPortal() {
		if( !portalStack.isEmpty() ) portalStack.pop();
		return portalStack.isEmpty() ? DesignPortal.DEFAULT : portalStack.pop();
	}

	private void capturePreviousPortal() {
		portalStack.push( new DesignPortal( getViewpoint(), getZoom(), getViewRotate() ) );
	}

	private static class SelectWindow extends Rectangle {

		@Override
		public boolean isResizable() {
			return true;
		}

		@Override
		public void resizeRelocate( double x, double y, double w, double h ) {
			setX( x );
			setY( y );
			setWidth( w );
			setHeight( h );
			setVisible( true );
		}

		public void hide() {
			setX( 0 );
			setY( 0 );
			setWidth( 0 );
			setHeight( 0 );
			setVisible( false );
		}

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
			getProgram().getTaskManager().submit( new DesignPrintTask( getProgram(), DesignToolV1.this, getAsset(), null ) );
			//getProgram().getTaskManager().submit( new DesignAwtPrintTask( getProgram(), FxShapeDesignTool.this, getAsset(), null ) );
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
			return !selectedFxShapes().isEmpty();
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
