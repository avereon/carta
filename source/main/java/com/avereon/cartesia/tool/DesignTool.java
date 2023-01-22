package com.avereon.cartesia.tool;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.*;
import com.avereon.cartesia.cursor.IconCursor;
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
import com.avereon.product.Rb;
import com.avereon.settings.Settings;
import com.avereon.transaction.Txn;
import com.avereon.util.DelayedAction;
import com.avereon.util.TypeReference;
import com.avereon.xenon.*;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetSwitchedEvent;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.asset.type.PropertiesType;
import com.avereon.xenon.notice.Notice;
import com.avereon.xenon.task.Task;
import com.avereon.xenon.tool.guide.GuideNode;
import com.avereon.xenon.tool.guide.GuidedTool;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.xenon.workpane.Workpane;
import com.avereon.xenon.workspace.StatusBar;
import com.avereon.xenon.workspace.Workspace;
import com.avereon.zarra.color.Paints;
import com.avereon.zarra.javafx.Fx;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.print.*;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Translate;
import javafx.stage.Screen;
import lombok.CustomLog;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@CustomLog
public abstract class DesignTool extends GuidedTool {

	public static final String RETICLE = "reticle";

	public static final String SELECT_APERTURE_SIZE = "select-aperture-size";

	public static final String SELECT_APERTURE_UNIT = "select-aperture-unit";

	public static final String REFERENCE_POINT_SIZE = "reference-point-size";

	public static final String REFERENCE_POINT_TYPE = "reference-point-type";

	public static final String REFERENCE_POINT_PAINT = "reference-point-paint";

	public static final boolean DEFAULT_GRID_VISIBLE = true;

	public static final boolean DEFAULT_GRID_SNAP_ENABLED = true;

	private static final String SETTINGS_VIEW_ZOOM = "view-zoom";

	private static final String SETTINGS_VIEW_POINT = "view-point";

	private static final String SETTINGS_VIEW_ROTATE = "view-rotate";

	private static final String CURRENT_LAYER = "current-layer";

	private static final String CURRENT_VIEW = "current-view";

	private static final String ENABLED_LAYERS = "enabled-layers";

	private static final String VISIBLE_LAYERS = "visible-layers";

	private static final String GRID_VISIBLE = "grid-visible";

	private static final String GRID_SNAP_ENABLED = "grid-snap";

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

	private final DesignWorkplane workplane;

	private final DelayedAction rebuildGridAction;

	private ReticleCursor reticle;

	private BooleanProperty gridSnapEnabled;

	private ChangeListener<Boolean> gridVisibleToggleHandler;

	private ChangeListener<Boolean> snapGridToggleHandler;

	private com.avereon.event.EventHandler<AssetSwitchedEvent> assetSwitchListener;

	public DesignTool( ProgramProduct product, Asset asset ) {
		super( product, asset );
		getStyleClass().add( "design-tool" );

		addStylesheet( CartesiaMod.STYLESHEET );

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

		this.workplane = new DesignWorkplane();

		this.rebuildGridAction = new DelayedAction( getProgram().getTaskManager().getExecutor(), this::doRebuildGrid );
		this.rebuildGridAction.setMaxTriggerLimit( 500 );
		this.rebuildGridAction.setMinTriggerLimit( 100 );

		this.selectedShapes = FXCollections.observableArrayList();
		this.selectedShapes.addListener( (ListChangeListener<? super Shape>)this::doSelectedShapesChanged );
		this.selectWindow = new SelectWindow();
		this.selectWindow.getStyleClass().add( "select" );
		this.selectPane = new Pane();
		this.selectPane.getChildren().addAll( selectWindow );

		getChildren().addAll( designPane, selectPane );

		designPane.prefWidthProperty().bind( widthProperty() );
		designPane.prefHeightProperty().bind( heightProperty() );

		// Settings and settings listeners should go in the ready() method
	}

	public final Design getDesign() {
		return getAssetModel();
	}

	public final DesignContext getDesignContext() {
		return getDesign().getDesignContext( getProduct() );
	}

	public final CommandContext getCommandContext() {
		return getDesignContext().getCommandContext();
	}

	public final CoordinateSystem getCoordinateSystem() {
		return getWorkplane().getCoordinateSystem();
	}

	public final void setCoordinateSystem( CoordinateSystem system ) {
		getWorkplane().setCoordinateSystem( system );
	}

	public final DesignWorkplane getWorkplane() {
		return workplane;
	}

	public Point3D getViewPoint() {
		return designPane == null ? Point3D.ZERO : designPane.getViewPoint();
	}

	public void setViewPoint( Point3D point ) {
		getCommandContext().setPreviousViewpoint( getViewPoint() );
		if( designPane != null ) designPane.setViewPoint( point );
	}

	public double getViewRotate() {
		return designPane == null ? 0.0 : designPane.getViewRotate();
	}

	public void setViewRotate( double angle ) {
		getCommandContext().setPreviousRotate( getViewRotate() );
		if( designPane != null ) Fx.run( () -> designPane.setViewRotate( angle ) );
	}

	public double getZoom() {
		return designPane == null ? 1.0 : designPane.getZoom();
	}

	public void setZoom( double zoom ) {
		getCommandContext().setPreviousZoom( getZoom() );
		if( designPane != null ) designPane.setZoom( zoom );
	}

	public ReticleCursor getReticle() {
		return reticle;
	}

	public void setView( Point3D center, double zoom ) {
		if( designPane != null ) Fx.run( () -> designPane.setView( center, zoom ) );
	}

	public void setView( Point3D center, double zoom, double rotate ) {
		if( designPane != null ) Fx.run( () -> designPane.setView( center, zoom, rotate ) );
	}

	/**
	 * Set the camera viewport using a screen-based rectangular viewport. The
	 * appropriate zoom and center will be calculated.
	 *
	 * @param viewport The screen viewport
	 */
	public void setViewport( Bounds viewport ) {
		Point3D worldCenter = screenToWorld( new Point3D( viewport.getCenterX(), viewport.getCenterY(), viewport.getCenterZ() ) );

		Bounds toolBounds = getLayoutBounds();
		double xZoom = Math.abs( toolBounds.getWidth() / viewport.getWidth() );
		double yZoom = Math.abs( toolBounds.getHeight() / viewport.getHeight() );
		double zoom = Math.min( xZoom, yZoom ) * getZoom();

		setView( worldCenter, zoom );
	}

	public void setSelectAperture( DesignValue aperture ) {
		selectAperture.set( aperture );
	}

	public DesignValue getSelectAperture() {
		return selectAperture.get();
	}

	public ObjectProperty<DesignValue> selectApertureProperty() {
		return selectAperture;
	}

	public ObservableList<Shape> selectedShapes() {
		return selectedShapes;
	}

	public Shape nearestShape2d( Collection<Shape> shapes, Point3D point ) {
		// Go through all the shapes and find the nearest
		double distance;
		double minDistance = Double.MAX_VALUE;
		Shape nearest = null;

		for( Shape shape : shapes ) {
			DesignShape data = DesignShapeView.getDesignData( shape );
			if( data == null || data.isReference() ) continue;
			distance = data.distanceTo( point );
			if( distance < minDistance ) {
				nearest = shape;
				minDistance = distance;
			}
		}

		return nearest;
	}

	public Point3D nearestCp( Collection<Shape> shapes, Point3D point ) {
		Point3D mouse = worldToScreen( point );

		// Go through all the reference points, convert them to screen coordinates and find the nearest
		double distance;
		double minDistance = Double.MAX_VALUE;
		Point3D nearest = CadPoints.NONE;

		for( Shape shape : shapes ) {
			DesignShape data = DesignShapeView.getDesignData( shape );
			if( data == null || data.isReference() ) continue;
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

	public void setCurrentLayer( DesignLayer layer ) {
		currentLayer.set( layer );
	}

	public DesignLayer getCurrentLayer() {
		return currentLayer.get();
	}

	public ObjectProperty<DesignLayer> currentLayerProperty() {
		return currentLayer;
	}

	private void setLayerEnabled( DesignLayer layer, boolean enabled ) {
		designPane.setLayerEnabled( layer, enabled );
	}

	public boolean isLayerVisible( DesignLayer layer ) {
		return designPane.isLayerVisible( layer );
	}

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

	public List<DesignLayer> getVisibleLayers() {
		return getFilteredLayers( DesignLayerPane::isVisible );
	}

	private List<DesignLayer> getFilteredLayers( Predicate<? super DesignLayerPane> filter ) {
		return designPane.getLayers().stream().filter( filter ).map( y -> (DesignLayer)DesignShapeView.getDesignData( y ) ).collect( Collectors.toList() );
	}

	// FIXME This really should return design shapes and not FX shapes
	public List<Shape> getVisibleShapes() {
		return designPane.getVisibleShapes();
	}

	public Paint getSelectedDrawPaint() {
		return designPane.getSelectDrawPaint();
	}

	public Paint getSelectedFillPaint() {
		return designPane.getSelectFillPaint();
	}

	public boolean isReferenceLayerVisible() {
		return designPane.isReferenceLayerVisible();
	}

	public void setReferenceLayerVisible( boolean visible ) {
		Fx.run( () -> designPane.setReferenceLayerVisible( visible ) );
	}

	public void setCurrentView( DesignView view ) {
		currentView.set( view );
	}

	public DesignView getCurrentView() {
		return currentView.get();
	}

	public ObjectProperty<DesignView> currentViewProperty() {
		return currentView;
	}

	/**
	 * Change the zoom value by a factor.
	 *
	 * @param anchor The zoom anchor
	 * @param factor The zoom factor
	 */
	public void zoom( Point3D anchor, double factor ) {
		Fx.run( () -> designPane.zoom( anchor, factor ) );
	}

	/**
	 * Pan the view by mouse coordinates.
	 *
	 * @param viewAnchor The view point location before being dragged (world)
	 * @param dragAnchor The drag anchor (screen)
	 * @param x The new X coordinate (screen)
	 * @param y The new Y coordinate (screen)
	 */
	public void pan( Point3D viewAnchor, Point3D dragAnchor, double x, double y ) {
		Fx.run( () -> designPane.mousePan( viewAnchor, dragAnchor, x, y ) );
	}

	public Point3D mouseToWorld( Point3D point ) {
		return mouseToWorld( point.getX(), point.getY(), point.getZ() );
	}

	public Point3D mouseToWorld( double x, double y, double z ) {
		return designPane == null ? Point3D.ZERO : designPane.parentToLocal( x, y, z );
	}

	public Point3D mouseToWorkplane( Point3D point ) {
		return mouseToWorkplane( point.getX(), point.getY(), point.getZ() );
	}

	public Point3D mouseToWorkplane( double x, double y, double z ) {
		Point3D worldPoint = mouseToWorld( x, y, z );
		return isGridSnapEnabled() ? gridSnap.snap( this, worldPoint ) : worldPoint;
	}

	public Point3D worldToScreen( double x, double y, double z ) {
		return designPane == null ? Point3D.ZERO : designPane.localToParent( x, y, z );
	}

	public Point3D worldToScreen( Point3D point ) {
		return designPane == null ? Point3D.ZERO : designPane.localToParent( point );
	}

	public Bounds worldToScreen( Bounds bounds ) {
		// NOTE Flip the Y coordinate because we are converting from world to screen coordinates
		Point3D a = worldToScreen( bounds.getMinX(), bounds.getMaxY(), bounds.getMinZ() );
		Point3D b = worldToScreen( bounds.getMaxX(), bounds.getMinY(), bounds.getMaxZ() );
		return new BoundingBox( a.getX(), a.getY(), a.getZ(), b.getX() - a.getX(), b.getY() - a.getY(), b.getZ() - a.getZ() );
	}

	public Point3D screenToWorld( double x, double y, double z ) {
		return designPane == null ? Point3D.ZERO : designPane.parentToLocal( x, y, z );
	}

	public Point3D screenToWorld( Point3D point ) {
		return designPane == null ? Point3D.ZERO : designPane.parentToLocal( point );
	}

	public Bounds screenToWorld( Bounds bounds ) {
		// NOTE Flip the Y coordinate because we are converting from screen to world coordinates
		Point3D a = screenToWorld( bounds.getMinX(), bounds.getMaxY(), bounds.getMinZ() );
		Point3D b = screenToWorld( bounds.getMaxX(), bounds.getMinY(), bounds.getMaxZ() );
		return new BoundingBox( a.getX(), a.getY(), a.getZ(), b.getX() - a.getX(), b.getY() - a.getY(), b.getZ() - a.getZ() );
	}

	public boolean isGridVisible() {
		return getDesignPane().isGridVisible();
	}

	public void setGridVisible( boolean visible ) {
		getDesignPane().setGridVisible( visible );
		if( visible ) rebuildGridAction.update();
	}

	public BooleanProperty gridVisible() {
		return getDesignPane().gridVisible();
	}

	public boolean isGridSnapEnabled() {
		return gridSnapEnabled == null ? DEFAULT_GRID_SNAP_ENABLED : gridSnapEnabled().get();
	}

	public void setGridSnapEnabled( boolean enabled ) {
		gridSnapEnabled().set( enabled );
	}

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

		Design design = request.getAsset().getModel();
		design.getDesignContext( getProduct() ).getCommandContext().setTool( this );

		// Link the guides before loading the design
		layersGuide.link();
		viewsGuide.link();
		printsGuide.link();

		Fx.run( () -> {
			designPane.setDpi( Screen.getPrimary().getDpi() );
			designPane.setDesign( design );
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
		configureWorkplane();

		Settings productSettings = getProduct().getSettings();
		Settings settings = getSettings();
		String defaultSelectSize = "2";
		String defaultSelectUnit = DesignUnit.CENTIMETER.name().toLowerCase();
		String defaultReferencePointType = DesignMarker.Type.CIRCLE.name().toLowerCase();
		String defaultReferencePointSize = "10";
		String defaultReferencePointPaint = "#808080";
		String defaultReticle = ReticleCursor.DUPLEX.name().toLowerCase();

		// Get tool settings
		double selectApertureSize = Double.parseDouble( productSettings.get( SELECT_APERTURE_SIZE, defaultSelectSize ) );
		DesignUnit selectApertureUnit = DesignUnit.valueOf( productSettings.get( SELECT_APERTURE_UNIT, defaultSelectUnit ).toUpperCase() );
		DesignMarker.Type referencePointType = DesignMarker.Type.valueOf( productSettings.get( REFERENCE_POINT_TYPE, defaultReferencePointType ).toUpperCase() );
		double referencePointSize = Double.parseDouble( productSettings.get( REFERENCE_POINT_SIZE, defaultReferencePointSize ) );
		Paint referencePointPaint = Paints.parse( productSettings.get( REFERENCE_POINT_PAINT, defaultReferencePointPaint ) );

		setViewPoint( ParseUtil.parsePoint3D( settings.get( SETTINGS_VIEW_POINT, "0,0,0" ) ) );
		setViewRotate( Double.parseDouble( settings.get( SETTINGS_VIEW_ROTATE, "0.0" ) ) );
		setZoom( Double.parseDouble( settings.get( SETTINGS_VIEW_ZOOM, "1.0" ) ) );
		setReticle( ReticleCursor.valueOf( productSettings.get( RETICLE, defaultReticle ).toUpperCase() ) );
		setSelectAperture( new DesignValue( selectApertureSize, selectApertureUnit ) );
		designPane.setReferencePointType( referencePointType );
		designPane.setReferencePointSize( referencePointSize );
		designPane.setReferencePointPaint( referencePointPaint );

		design.findLayers( DesignLayer.ID, settings.get( CURRENT_LAYER, "" ) ).stream().findFirst().ifPresent( this::setCurrentLayer );
		design.findViews( DesignView.ID, settings.get( CURRENT_VIEW, "" ) ).stream().findFirst().ifPresent( this::setCurrentView );

		// Restore the list of enabled layers
		Set<String> enabledLayerIds = settings.get( ENABLED_LAYERS, new TypeReference<>() {}, Set.of() );
		design.getAllLayers().forEach( l -> designPane.setLayerEnabled( l, enabledLayerIds.contains( l.getId() ) ) );

		// Restore the list of visible layers
		Set<String> visibleLayerIds = settings.get( VISIBLE_LAYERS, new TypeReference<>() {}, Set.of() );
		design.getAllLayers().forEach( l -> designPane.setLayerVisible( l, visibleLayerIds.contains( l.getId() ) ) );

		// Restore the grid visible flag
		setGridVisible( Boolean.parseBoolean( settings.get( GRID_VISIBLE, DEFAULT_GRID_VISIBLE ) ) );

		// Restore the grid snap enabled flag
		setGridSnapEnabled( Boolean.parseBoolean( settings.get( GRID_SNAP_ENABLED, DEFAULT_GRID_SNAP_ENABLED ) ) );

		// Restore the reference view visibility
		setReferenceLayerVisible( Boolean.parseBoolean( settings.get( REFERENCE_LAYER_VISIBLE, Boolean.TRUE.toString() ) ) );

		// Settings listeners
		productSettings.register( RETICLE, e -> setReticle( ReticleCursor.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		productSettings.register( SELECT_APERTURE_SIZE, e -> setSelectAperture( new DesignValue( Double.parseDouble( (String)e.getNewValue() ), getSelectAperture().getUnit() ) ) );
		productSettings.register( SELECT_APERTURE_UNIT, e -> setSelectAperture( new DesignValue( getSelectAperture().getValue(), DesignUnit.valueOf( ((String)e.getNewValue()).toUpperCase() ) ) ) );
		productSettings.register( REFERENCE_POINT_TYPE, e -> designPane.setReferencePointType( DesignMarker.Type.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		productSettings.register( REFERENCE_POINT_SIZE, e -> designPane.setReferencePointSize( Double.parseDouble( (String)e.getNewValue() ) ) );
		productSettings.register( REFERENCE_POINT_PAINT, e -> designPane.setReferencePointPaint( Paints.parse( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );

		// Add layout bounds property listener
		layoutBoundsProperty().addListener( ( p, o, n ) -> doUpdateGridBounds() );

		// Add view point property listener
		designPane.viewPointProperty().addListener( ( p, o, n ) -> {
			settings.set( SETTINGS_VIEW_POINT, n.getX() + "," + n.getY() + "," + n.getZ() );
			doUpdateGridBounds();
		} );

		// Add view rotate property listener
		designPane.viewRotateProperty().addListener( ( p, o, n ) -> {
			settings.set( SETTINGS_VIEW_ROTATE, n.doubleValue() );
			doUpdateGridBounds();
		} );

		// Add view zoom property listener
		designPane.zoomProperty().addListener( ( p, o, n ) -> {
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

		getCoordinateStatus().updateZoom( getZoom() );
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

	final void showCommandPrompt() {
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
		// make it to the workpane and forwards them to the command context which
		// will help determine what to do.
		Workpane workpane = getWorkpane();
		workpane.getProperties().put( "design-tool-command-capture", getCommandContext() );
		workpane.addEventHandler( KeyEvent.ANY, getCommandContext() );
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
		String drawMarkerActions = "marker[draw-marker]";
		String drawLineActions = "line[draw-line-2 draw-line-perpendicular]";
		String drawCircleActions = "circle[draw-circle-2 draw-circle-diameter-2 draw-circle-3 | draw-arc-2 draw-arc-3]";
		String drawEllipseActions = "ellipse[draw-ellipse-3 draw-ellipse-arc-5]";
		String drawCurveActions = "curve[draw-curve-4 draw-path]";

		String measurementActions = "measure[shape-information measure-angle measure-distance measure-point measure-length]";

		@SuppressWarnings( "StringBufferReplaceableByString" ) StringBuilder menus = new StringBuilder( viewActions );
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

	private void setReticle( ReticleCursor reticle ) {
		this.reticle = reticle;
		if( getCursor() instanceof IconCursor ) setCursor( reticle.getCursorIcon( getProgram() ) );
	}

	private CommandPrompt getCommandPrompt() {
		return getDesignContext().getCommandPrompt();
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

	public void updateSelectWindow( Point3D anchor, Point3D mouse ) {
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

	public List<Shape> screenPointFindOneAndWait( Point3D mouse ) {
		return designPane.screenPointSelect( mouse, getSelectAperture() ).stream().findFirst().stream().collect( Collectors.toList() );
	}

	public List<Shape> screenPointFindAllAndWait( Point3D mouse ) {
		return new ArrayList<>( designPane.screenPointSelect( mouse, getSelectAperture() ) );
	}

	public List<Shape> screenPointSelectAndWait( Point3D mouse ) {
		selectedShapes.clear();
		selectedShapes.addAll( designPane.screenPointSelect( mouse, getSelectAperture() ).stream().findFirst().stream().toList() );
		return selectedShapes();
	}

	public void screenPointSelect( Point3D mouse ) {
		screenPointSelect( mouse, false );
	}

	public void screenPointSelect( Point3D mouse, boolean toggle ) {
		if( !toggle ) selectedShapes().clear();

		designPane.screenPointSelect( mouse, getSelectAperture() ).stream().findFirst().ifPresent( shape -> {
			if( toggle && getDesignData( shape ).isSelected() ) {
				selectedShapes().remove( shape );
			} else {
				selectedShapes().add( shape );
			}
		} );
	}

	public void mouseWindowSelect( Point3D a, Point3D b, boolean contains ) {
		Fx.run( () -> {
			selectedShapes().clear();
			selectedShapes().addAll( designPane.screenWindowSelect( a, b, contains ) );
		} );
	}

	public void worldPointSelect( Point3D point ) {
		worldPointSelect( point, false );
	}

	public void worldPointSelect( Point3D point, boolean toggle ) {
		Fx.run( () -> {
			if( !toggle ) selectedShapes().clear();

			List<Shape> selection = designPane.worldPointSelect( point, getSelectAperture() );
			selection.stream().findFirst().ifPresent( shape -> {
				if( toggle && getDesignData( shape ).isSelected() ) {
					selectedShapes().remove( shape );
				} else {
					selectedShapes().add( shape );
				}
			} );
		} );
	}

	public void clearSelected() {
		selectedShapes().clear();
	}

	public List<DesignShape> findShapesWithMouse( Point3D mouse ) {
		return designPane.screenPointSelect( mouse, getSelectAperture() ).stream().map( DesignShapeView::getDesignData ).collect( Collectors.toList() );
	}

	public List<DesignShape> findShapesWithPoint( Point3D point ) {
		return designPane.worldPointSelect( point, getSelectAperture() ).stream().map( DesignShapeView::getDesignData ).collect( Collectors.toList() );
	}

	public List<DesignShape> getSelectedGeometry() {
		return selectedShapes().stream().map( DesignShapeView::getDesignData ).collect( Collectors.toList() );
	}

	private void configureWorkplane() {
		// The workplane values are stored in the asset settings
		// However, a set of default workplane values may need to be put in the
		// asset settings because when a tool is closed, the tool settings are deleted.
		DesignWorkplane workplane = getWorkplane();
		Settings settings = getAssetSettings();

		workplane.setCoordinateSystem( CoordinateSystem.valueOf( settings.get( DesignWorkplane.COORDINATE_SYSTEM, DesignWorkplane.DEFAULT_COORDINATE_SYSTEM.name() ).toUpperCase() ) );
		workplane.setOrigin( settings.get( "workpane-origin", DesignWorkplane.DEFAULT_GRID_ORIGIN ) );

		workplane.setGridAxisVisible( settings.get( DesignWorkplane.GRID_AXIS_VISIBLE, Boolean.class, DesignWorkplane.DEFAULT_GRID_AXIS_VISIBILE ) );
		workplane.setGridAxisPaint( Paints.parseWithNullOnException( settings.get( DesignWorkplane.GRID_AXIS_PAINT, DesignWorkplane.DEFAULT_GRID_AXIS_PAINT ) ) );
		workplane.setGridAxisWidth( settings.get( DesignWorkplane.GRID_AXIS_WIDTH, DesignWorkplane.DEFAULT_GRID_AXIS_WIDTH ) );

		workplane.setMajorGridVisible( settings.get( DesignWorkplane.GRID_MAJOR_VISIBLE, Boolean.class, DesignWorkplane.DEFAULT_GRID_MAJOR_VISIBILE ) );
		workplane.setMajorGridX( settings.get( DesignWorkplane.GRID_MAJOR_X, DesignWorkplane.DEFAULT_GRID_MAJOR_SIZE ) );
		workplane.setMajorGridY( settings.get( DesignWorkplane.GRID_MAJOR_Y, DesignWorkplane.DEFAULT_GRID_MAJOR_SIZE ) );
		workplane.setMajorGridZ( settings.get( DesignWorkplane.GRID_MAJOR_Z, DesignWorkplane.DEFAULT_GRID_MAJOR_SIZE ) );
		workplane.setMajorGridPaint( Paints.parseWithNullOnException( settings.get( DesignWorkplane.GRID_MAJOR_PAINT, DesignWorkplane.DEFAULT_GRID_MAJOR_PAINT ) ) );
		workplane.setMajorGridWidth( settings.get( DesignWorkplane.GRID_MAJOR_WIDTH, DesignWorkplane.DEFAULT_GRID_MAJOR_WIDTH ) );

		workplane.setMinorGridVisible( settings.get( DesignWorkplane.GRID_MINOR_VISIBLE, Boolean.class, DesignWorkplane.DEFAULT_GRID_MINOR_VISIBILE ) );
		workplane.setMinorGridX( settings.get( DesignWorkplane.GRID_MINOR_X, DesignWorkplane.DEFAULT_GRID_MINOR_SIZE ) );
		workplane.setMinorGridY( settings.get( DesignWorkplane.GRID_MINOR_Y, DesignWorkplane.DEFAULT_GRID_MINOR_SIZE ) );
		workplane.setMinorGridZ( settings.get( DesignWorkplane.GRID_MINOR_Z, DesignWorkplane.DEFAULT_GRID_MINOR_SIZE ) );
		workplane.setMinorGridPaint( Paints.parseWithNullOnException( settings.get( DesignWorkplane.GRID_MINOR_PAINT, DesignWorkplane.DEFAULT_GRID_MINOR_PAINT ) ) );
		workplane.setMinorGridWidth( settings.get( DesignWorkplane.GRID_MINOR_WIDTH, DesignWorkplane.DEFAULT_GRID_MINOR_WIDTH ) );

		workplane.setSnapGridX( settings.get( DesignWorkplane.GRID_SNAP_X, DesignWorkplane.DEFAULT_GRID_SNAP_SIZE ) );
		workplane.setSnapGridY( settings.get( DesignWorkplane.GRID_SNAP_Y, DesignWorkplane.DEFAULT_GRID_SNAP_SIZE ) );
		workplane.setSnapGridZ( settings.get( DesignWorkplane.GRID_SNAP_Z, DesignWorkplane.DEFAULT_GRID_SNAP_SIZE ) );

		settings.register( DesignWorkplane.COORDINATE_SYSTEM, e -> workplane.setCoordinateSystem( CoordinateSystem.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
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
		workplane.register( NodeEvent.VALUE_CHANGED, e -> rebuildGridAction.update() );
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
				List<Shape> grid = getCoordinateSystem().getGridLines( getWorkplane() );
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
			c.getRemoved().stream().map( DesignTool::getDesignData ).forEach( s -> s.setSelected( false ) );
			c.getAddedSubList().stream().map( DesignTool::getDesignData ).forEach( s -> s.setSelected( true ) );

			int size = c.getList().size();

			if( size == 0 ) {
				showPropertiesPage( getCurrentLayer() );
			} else if( size == 1 ) {
				c.getList().stream().findFirst().map( DesignTool::getDesignData ).ifPresent( this::showPropertiesPage );
			} else {
				// Show a combined properties page
				Set<DesignDrawable> designData = c.getList().parallelStream().map( DesignTool::getDesignData ).collect( Collectors.toSet() );
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
			page.setSettings( settings );

			// Switch to a task thread to get the tool
			getProgram().getTaskManager().submit( Task.of( () -> {
				try {
					// Open the tool but don't make it the active tool
					getProgram().getAssetManager().openAsset( ShapePropertiesAssetType.URI, true, false ).get();

					// Fire the event on the FX thread
					Fx.run( () -> getWorkspace().getEventBus().dispatch( new ShapePropertiesToolEvent( DesignTool.this, ShapePropertiesToolEvent.SHOW, page ) ) );
				} catch( Exception exception ) {
					log.atWarn( exception ).log();
				}
			} ) );
		} else {
			log.atError().log( "Unable to find properties page for %s", type.getName() );
		}
	}

	private void hidePropertiesPage() {
		getWorkspace().getEventBus().dispatch( new ShapePropertiesToolEvent( DesignTool.this, ShapePropertiesToolEvent.HIDE, null ) );
	}

	static DesignLayer getDesignData( DesignLayerPane l ) {
		return (DesignLayer)DesignShapeView.getDesignData( l );
	}

	public static DesignShape getDesignData( Shape s ) {
		return DesignShapeView.getDesignData( s );
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

		protected CommandAction( Program program, String shortcut ) {
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

		protected PrintAction( Program program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public void handle( ActionEvent event ) {
			String label = Rb.textOr( RbKey.LABEL, "print", "Print" );
			String taskName = label + " " + getAsset().getName();

			getProgram().getTaskManager().submit( Task.of( taskName, () -> {
				final PrinterJob job = PrinterJob.createPrinterJob();
				final Printer printer = job.getPrinter();
				job.getJobSettings().setPageLayout( printer.createPageLayout( Paper.NA_LETTER, PageOrientation.PORTRAIT, 0, 0, 0, 0 ) );
				final PageLayout layout = job.getJobSettings().getPageLayout();

				// NOTE This can be used to give feedback to the user. It can be bound to a text field
				job.jobStatusProperty().asString();

				// WIDTH and HEIGHT in printer points (1/72)
				double printableWidth = layout.getPrintableWidth();
				double printableHeight = layout.getPrintableHeight();

				// NOTE This is a rather Swing looking dialog, maybe handle print properties separately
				//				boolean print = job.showPageSetupDialog( getScene().getWindow() );
				//				boolean print = job.showPrintDialog( getScene().getWindow() );
				//				if( !print ) return;

				// NOTE The print API uses 72 DPI regardless of the printer
				final DesignPane designPane = new DesignPane();
				designPane.setDpi( 72 );
				designPane.setReferenceLayerVisible( false );
				designPane.setDesign( getDesign() );
				designPane.setView( getVisibleLayers(), getViewPoint(), getZoom(), getViewRotate() );

				// Create an encapsulating pane to represent the paper
				final Pane paperPane = new Pane( designPane );

				// Move the center of the paper pane to the center of the printable area
				paperPane.getTransforms().add( new Translate( 0.5 * printableWidth, 0.5 * printableHeight ) );

				// NOTE DesignPane uses the FX thread for a lot of its work
				// Need to wait for it to complete
				Fx.waitFor( 1000 );

				boolean successful = job.printPage( paperPane ) && job.endJob();
				if( !successful ) getProgram().getNoticeManager().addNotice( new Notice( taskName, job.getJobStatus() ) );
			} ) );
		}

	}

	private class PropertiesAction extends ProgramAction {

		protected PropertiesAction( Program program ) {
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

					// Fire the event on the FX thread
					Workspace workspace = getProgram().getWorkspaceManager().getActiveWorkspace();
					Fx.run( () -> workspace.getEventBus().dispatch( new PropertiesToolEvent( PropertiesAction.this, PropertiesToolEvent.SHOW, designSettingsPage, assetSettingsPage ) ) );
				} catch( Exception exception ) {
					log.atError( exception ).log();
				}
			} ) );
		}

	}

	private class DeleteAction extends ProgramAction {

		protected DeleteAction( Program program ) {
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

		protected UndoAction( Program program ) {
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

		protected RedoAction( Program program ) {
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
