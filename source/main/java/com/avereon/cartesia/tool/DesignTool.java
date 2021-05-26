package com.avereon.cartesia.tool;

import com.avereon.cartesia.*;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.snap.SnapGrid;
import com.avereon.cartesia.tool.guide.DesignToolLayersGuide;
import com.avereon.cartesia.tool.guide.DesignToolPrintsGuide;
import com.avereon.cartesia.tool.guide.DesignToolViewsGuide;
import com.avereon.cartesia.tool.view.DesignPane;
import com.avereon.cartesia.tool.view.DesignPaneLayer;
import com.avereon.cartesia.tool.view.DesignShapeView;
import com.avereon.data.IdNode;
import com.avereon.data.MultiNodeSettings;
import com.avereon.data.NodeEvent;
import com.avereon.data.NodeSettings;
import com.avereon.settings.Settings;
import com.avereon.util.Log;
import com.avereon.util.TypeReference;
import com.avereon.xenon.*;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.task.Task;
import com.avereon.xenon.tool.guide.GuideNode;
import com.avereon.xenon.tool.guide.GuidedTool;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.zerra.javafx.Fx;
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
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class DesignTool extends GuidedTool {

	public static final String RETICLE = "reticle";

	public static final String SELECT_APERTURE_RADIUS = "select-aperture-radius";

	public static final String SELECT_APERTURE_UNIT = "select-aperture-unit";

	public static final boolean DEFAULT_GRID_VISIBLE = true;

	public static final boolean DEFAULT_GRID_SNAP_ENABLED = true;

	private static final String SETTINGS_VIEW_ZOOM = "view-zoom";

	private static final String SETTINGS_VIEW_POINT = "view-point";

	private static final String SETTINGS_VIEW_ROTATE = "view-rotate";

	private static final String CURRENT_LAYER = "layer";

	private static final String VISIBLE_LAYERS = "visible-layers";

	private static final String GRID_VISIBLE = "grid-visible";

	private static final String GRID_SNAP_ENABLED = "grid-snap";

	private static final String REFERENCE_LAYER_VISIBLE = "";

	private static final System.Logger log = Log.get();

	private static final Snap gridSnap = new SnapGrid();

	private final Map<String, Action> commandActions;

	private final DesignToolLayersGuide layersGuide;

	private final DesignToolViewsGuide viewsGuide;

	private final DesignToolPrintsGuide printsGuide;

	private final DesignPane designPane;

	private final Pane selectPane;

	private final SelectWindow selectWindow;

	private final ObjectProperty<DesignValue> selectTolerance;

	private final ObservableList<Shape> selectedShapes;

	private final ObjectProperty<DesignLayer> currentLayer;

	private final DesignPropertiesMap designPropertiesMap;

	private final DeleteAction deleteAction;

	private final UndoAction undoAction;

	private final RedoAction redoAction;

	private ReticleCursor reticle;

	private BooleanProperty gridSnapEnabled;

	private ChangeListener<Boolean> gridVisibleToggleHandler;

	private ChangeListener<Boolean> snapGridToggleHandler;

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

		this.designPane = new DesignPane();
		this.selectTolerance = new SimpleObjectProperty<>();
		this.currentLayer = new SimpleObjectProperty<>();

		this.deleteAction = new DeleteAction( product.getProgram() );
		this.undoAction = new UndoAction( product.getProgram() );
		this.redoAction = new RedoAction( product.getProgram() );

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

	@Deprecated
	public CommandPrompt getCommandPrompt() {
		return getDesignContext().getCommandPrompt();
	}

	public Point3D getViewPoint() {
		return designPane == null ? Point3D.ZERO : designPane.getViewPoint();
	}

	public void setViewPoint( Point3D point ) {
		if( designPane != null ) designPane.setViewPoint( point );
	}

	public double getViewRotate() {
		return designPane == null ? 0.0 : designPane.getViewRotate();
	}

	public void setViewRotate( double angle ) {
		if( designPane != null ) Fx.run( () -> designPane.setViewRotate( angle ) );
	}

	public double getZoom() {
		return designPane == null ? 1.0 : designPane.getZoom();
	}

	public void setZoom( double zoom ) {
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

	public void setSelectTolerance( DesignValue tolerance ) {
		selectTolerance.set( tolerance );
	}

	public DesignValue getSelectTolerance() {
		return selectTolerance.get();
	}

	public ObjectProperty<DesignValue> selectToleranceProperty() {
		return selectTolerance;
	}

	public ObservableList<Shape> selectedShapes() {
		return selectedShapes;
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

	public boolean isLayerVisible( DesignLayer layer ) {
		return designPane.isLayerVisible( layer );
	}

	public void setLayerVisible( DesignLayer layer, boolean visible ) {
		if( visible ) {
			// Show the specified layer and parent layers
			while( !layer.isRootLayer() ) {
				designPane.setLayerVisible( layer, visible );
				layer = layer.getLayer();
			}
		} else {
			// Only hide the specified layer
			designPane.setLayerVisible( layer, visible );
		}
	}

	List<String> getVisibleLayerIds() {
		return getFilteredLayers( DesignPaneLayer::isVisible ).stream().map( IdNode::getId ).collect( Collectors.toList() );
	}

	public List<DesignLayer> getVisibleLayers() {
		return getFilteredLayers( DesignPaneLayer::isVisible );
	}

	private List<DesignLayer> getFilteredLayers( Predicate<? super DesignPaneLayer> filter ) {
		return designPane.getLayers().stream().filter( filter ).map( y -> (DesignLayer)DesignShapeView.getDesignData( y ) ).collect( Collectors.toList() );
	}

	public List<Shape> getVisibleShapes() {
		return designPane.getVisibleShapes();
	}

	public List<Shape> getSelectedShapes() {
		return new ArrayList<>( selectedShapes );
	}

	public boolean isReferenceLayerVisible() {
		return designPane.isReferenceLayerVisible();
	}

	public void setReferenceLayerVisible( boolean visible ) {
		Fx.run( () -> designPane.setReferenceLayerVisible( visible ) );
	}

	/**
	 * Change the zoom value by a factor.
	 *
	 * @param anchor
	 * @param factor
	 */
	public void zoom( Point3D anchor, double factor ) {
		Fx.run( () -> designPane.zoom( anchor, factor ) );
	}

	/**
	 * Pan the view by mouse coordinates.
	 *
	 * @param viewAnchor
	 * @param dragAnchor
	 * @param x
	 * @param y
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

		// Link the guides before loading the design
		layersGuide.link( designPane );
		//viewsGuide.init( design );
		//printsGuide.init( design );

		Fx.run( () -> {
			designPane.setDesign( design );
			designPane.setDpi( Screen.getPrimary().getDpi() );
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

		// Get tool settings
		double selectApertureRadius = Double.parseDouble( getSettings().get( SELECT_APERTURE_RADIUS, "1.0" ) );
		DesignUnit selectApertureUnit = DesignUnit.valueOf( getSettings().get( SELECT_APERTURE_UNIT, DesignUnit.MILLIMETER.name() ).toUpperCase() );
		setSelectTolerance( new DesignValue( selectApertureRadius, selectApertureUnit ) );
		setViewPoint( ParseUtil.parsePoint3D( getSettings().get( SETTINGS_VIEW_POINT, "0,0,0" ) ) );
		setViewRotate( Double.parseDouble( getSettings().get( SETTINGS_VIEW_ROTATE, "0.0" ) ) );
		setZoom( Double.parseDouble( getSettings().get( SETTINGS_VIEW_ZOOM, "1.0" ) ) );
		setReticle( ReticleCursor.valueOf( getSettings().get( RETICLE, ReticleCursor.DUPLEX.getClass().getSimpleName() ).toUpperCase() ) );
		design.findLayers( DesignLayer.ID, getSettings().get( CURRENT_LAYER ) ).stream().findFirst().ifPresent( this::setCurrentLayer );

		// Restore the list of visible layers
		Set<String> visibleLayerIds = getSettings().get( VISIBLE_LAYERS, new TypeReference<>() {}, Set.of() );
		design.getAllLayers().forEach( l -> setLayerVisible( l, visibleLayerIds.contains( l.getId() ) ) );

		// Restore the grid visible flag
		setGridVisible( Boolean.parseBoolean( getSettings().get( GRID_VISIBLE, DEFAULT_GRID_VISIBLE ) ) );

		// Restore the grid snap enabled flag
		setGridSnapEnabled( Boolean.parseBoolean( getSettings().get( GRID_SNAP_ENABLED, DEFAULT_GRID_SNAP_ENABLED ) ) );

		// Restore the reference layer visibility
		setReferenceLayerVisible( Boolean.parseBoolean( getSettings().get( REFERENCE_LAYER_VISIBLE, Boolean.TRUE.toString() ) ) );

		// Settings listeners
		getSettings().register( RETICLE, e -> setReticle( ReticleCursor.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		getSettings().register( SELECT_APERTURE_RADIUS, e -> setSelectTolerance( new DesignValue( Double.parseDouble( (String)e.getNewValue() ), selectApertureUnit ) ) );
		getSettings().register( SELECT_APERTURE_UNIT, e -> setSelectTolerance( new DesignValue( selectApertureRadius, DesignUnit.valueOf( ((String)e.getNewValue()).toUpperCase() ) ) ) );

		// Add layout bounds property listener
		layoutBoundsProperty().addListener( ( p, o, n ) -> validateGrid() );

		// Add view point property listener
		designPane.viewPointProperty().addListener( ( p, o, n ) -> {
			getSettings().set( SETTINGS_VIEW_POINT, n.getX() + "," + n.getY() + "," + n.getZ() );
			validateGrid();
		} );

		// Add view rotate property listener
		designPane.viewRotateProperty().addListener( ( p, o, n ) -> {
			getSettings().set( SETTINGS_VIEW_ROTATE, n.doubleValue() );
			validateGrid();
		} );

		// Add view zoom property listener
		designPane.zoomProperty().addListener( ( p, o, n ) -> {
			getCoordinateStatus().updateZoom( n.doubleValue() );
			getSettings().set( SETTINGS_VIEW_ZOOM, n.doubleValue() );
			validateGrid();
		} );

		// Add visible layers listener
		designPane.visibleLayersProperty().addListener( this::doStoreVisibleLayers );

		// Add current layer property listener
		currentLayerProperty().addListener( ( p, o, n ) -> getSettings().set( CURRENT_LAYER, n.getId() ) );

		// Add grid visible property listener
		gridVisible().addListener( ( p, o, n ) -> getSettings().set( GRID_VISIBLE, String.valueOf( n ) ) );

		// Add grid visible property listener
		gridSnapEnabled().addListener( ( p, o, n ) -> getSettings().set( GRID_SNAP_ENABLED, String.valueOf( n ) ) );

		// Add reference points visible property listener
		designPane.referenceLayerVisible().addListener( ( p, o, n ) -> getSettings().set( REFERENCE_LAYER_VISIBLE, String.valueOf( n ) ) );

		addEventFilter( MouseEvent.MOUSE_MOVED, e -> getDesignContext().setMouse( e ) );
		addEventFilter( MouseEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( MouseDragEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ScrollEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ZoomEvent.ANY, e -> getCommandContext().handle( e ) );

		// FIXME This can cause NPEs when not in an active workspace
		if( isActive() ) activate();
		getCoordinateStatus().updateZoom( getZoom() );
		designPane.updateView();
		validateGrid();
	}

	private void doStoreVisibleLayers( SetChangeListener.Change<? extends DesignLayer> c ) {
		getSettings().set( VISIBLE_LAYERS, c.getSet().stream().map( IdNode::getId ).collect( Collectors.toSet() ) );
	}

	@Override
	protected void guideNodesSelected( Set<GuideNode> oldNodes, Set<GuideNode> newNodes ) {
		newNodes.stream().findFirst().ifPresent( n -> doSetCurrentLayerById( n.getId() ) );
	}

	@Override
	protected void guideFocusChanged( boolean focused, Set<GuideNode> nodes ) {
		showPropertiesPage( getCurrentLayer() );
	}

	@Override
	protected void activate() throws ToolException {
		super.activate();
		if( isReady() ) {
			getDesignContext().getCommandContext().setLastActiveDesignTool( this );
			registerStatusBarItems();
			updateCommandCapture();
			registerActions();
		}
		requestFocus();
	}

	@Override
	protected void deactivate() throws ToolException {
		super.deactivate();
		if( isReady() ) unregisterStatusBarItems();
	}

	@Override
	protected void conceal() throws ToolException {
		super.conceal();
		if( isReady() ) unregisterActions();
	}

	private DesignPane getDesignPane() {
		return designPane;
	}

	private void registerStatusBarItems() {
		getWorkspace().getStatusBar().addLeftItems( getCommandPrompt() );
		getWorkspace().getStatusBar().addRightItems( getCoordinateStatus() );
	}

	private void unregisterStatusBarItems() {
		getWorkspace().getStatusBar().removeRightItems( getCoordinateStatus() );
		getWorkspace().getStatusBar().removeLeftItems( getCommandPrompt() );
	}

	@SuppressWarnings( "unchecked" )
	private void updateCommandCapture() {
		// If there is already a command capture handler then remove it (because it may belong to a different design)
		EventHandler<KeyEvent> handler = (EventHandler<KeyEvent>)getScene().getProperties().get( "design-tool-command-capture" );
		if( handler != null ) getScene().removeEventHandler( KeyEvent.ANY, handler );

		// Add this design command capture handler
		getScene().getProperties().put( "design-tool-command-capture", getCommandPrompt() );
		getScene().addEventHandler( KeyEvent.ANY, getCommandPrompt() );
	}

	private void registerActions() {
		pushAction( "delete", deleteAction );
		pushAction( "undo", undoAction );
		pushAction( "redo", redoAction );

		pushTools( "grid-toggle snap-grid-toggle draw[draw-line-2 draw-line-perpendicular | draw-circle-2 draw-circle-3 | draw-arc-2 draw-arc-3 | draw-ellipse-3 draw-ellipse-arc-5 | draw-curve-4 | draw-marker | draw-path]" );

		Action gridVisibleToggleAction = pushCommandAction( "grid-toggle", isGridVisible() ? "enabled" : "disabled" );
		gridVisible().addListener( gridVisibleToggleHandler = ( p, o, n ) -> gridVisibleToggleAction.setState( n ? "enabled" : "disabled" ) );
		Action snapGridToggleAction = pushCommandAction( "snap-grid-toggle", isGridSnapEnabled() ? "enabled" : "disabled" );
		gridSnapEnabled().addListener( snapGridToggleHandler=(p,o,n) -> snapGridToggleAction.setState( n ? "enabled" : "disabled" ) );
	}

	private void unregisterActions() {
		gridVisible().removeListener( gridVisibleToggleHandler );
		pullCommandAction( "grid-toggle" );
		gridSnapEnabled().removeListener( snapGridToggleHandler );
		pullCommandAction( "snap-grid-toggle" );

		pullTools();

		pullAction( "delete", deleteAction );
		pullAction( "undo", undoAction );
		pullAction( "redo", redoAction );
	}

	private Action pushCommandAction( String key, String initialActionState ) {
		ActionProxy proxy = getProgram().getActionLibrary().getAction( key );
		Action action = commandActions.computeIfAbsent( key, k -> new CommandAction( getProgram(), proxy.getCommand() ) );
		action.setState( initialActionState );
		pushAction( key, action );
		return action;
	}

	private void pullCommandAction( String key ) {
		pullAction( key, commandActions.get( key ) );
	}

	private void setReticle( ReticleCursor reticle ) {
		this.reticle = reticle;
		if( getCursor() instanceof ReticleCursor ) setCursor( reticle );
	}

	private CoordinateStatus getCoordinateStatus() {
		return getDesignContext().getCoordinateStatus();
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

	public List<Shape> screenPointFindAndWait( Point3D mouse ) {
		final List<Shape> selection = new ArrayList<>();
		Fx.run( () -> designPane.screenPointSelect( mouse, getSelectTolerance() ).stream().findFirst().ifPresent( selection::add ) );
		try {
			Fx.waitForWithInterrupt( 1000 );
		} catch( InterruptedException exception ) {
			log.log( Log.WARN, "Interrupted waiting for FX thread", exception );
		}
		return selection;
	}

	public List<Shape> screenPointSelectAndWait( Point3D mouse ) {
		Fx.run( () -> designPane.screenPointSelect( mouse, getSelectTolerance() ).stream().findFirst().ifPresent( selectedShapes()::add ) );
		try {
			Fx.waitForWithInterrupt( 1000 );
		} catch( InterruptedException exception ) {
			log.log( Log.WARN, "Interrupted waiting for FX thread", exception );
		}
		return selectedShapes();
	}

	public void screenPointSelect( Point3D mouse ) {
		screenPointSelect( mouse, false );
	}

	public void screenPointSelect( Point3D mouse, boolean toggle ) {
		Fx.run( () -> {
			if( !toggle ) selectedShapes().clear();

			List<Shape> selection = designPane.screenPointSelect( mouse, getSelectTolerance() );
			selection.stream().findFirst().ifPresent( shape -> {
				if( toggle && getDesignData( shape ).isSelected() ) {
					selectedShapes().remove( shape );
				} else {
					selectedShapes().add( shape );
				}
			} );
		} );
	}

	public void mouseWindowSelect( Point3D a, Point3D b, boolean contains ) {
		Fx.run( () -> {
			selectedShapes().clear();
			List<Shape> selection = designPane.screenWindowSelect( a, b, contains );
			selectedShapes().addAll( selection );
		} );
	}

	public void worldPointSelect( Point3D point ) {
		worldPointSelect( point, false );
	}

	public void worldPointSelect( Point3D point, boolean toggle ) {
		Fx.run( () -> {
			if( !toggle ) selectedShapes().clear();

			List<Shape> selection = designPane.worldPointSelect( point, getSelectTolerance() );
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
		getSelectedGeometry().clear();
	}

	public List<DesignShape> findShapesWithMouse( Point3D mouse ) {
		return designPane.screenPointSelect( mouse, getSelectTolerance() ).stream().map( DesignShapeView::getDesignData ).collect( Collectors.toList() );
	}

	public List<DesignShape> findShapesWithPoint( Point3D point ) {
		return designPane.worldPointSelect( point, getSelectTolerance() ).stream().map( DesignShapeView::getDesignData ).collect( Collectors.toList() );
	}

	public List<DesignShape> getSelectedGeometry() {
		return selectedShapes().stream().map( DesignShapeView::getDesignData ).collect( Collectors.toList() );
	}

	private void configureWorkplane() {
		Settings settings = getAsset().getSettings();
		DesignWorkplane workplane = getDesignContext().getWorkplane();

		workplane.setOrigin( getAsset().getSettings().get( "workpane-origin", DesignWorkplane.DEFAULT_ORIGIN ) );
		workplane.setMajorGridX( getAsset().getSettings().get( "workpane-major-grid-x", DesignWorkplane.DEFAULT_MAJOR_GRID_SIZE ) );
		workplane.setMajorGridY( getAsset().getSettings().get( "workpane-major-grid-y", DesignWorkplane.DEFAULT_MAJOR_GRID_SIZE ) );
		workplane.setMajorGridZ( getAsset().getSettings().get( "workpane-major-grid-z", DesignWorkplane.DEFAULT_MAJOR_GRID_SIZE ) );
		workplane.setMinorGridX( getAsset().getSettings().get( "workpane-minor-grid-x", DesignWorkplane.DEFAULT_MINOR_GRID_SIZE ) );
		workplane.setMinorGridY( getAsset().getSettings().get( "workpane-minor-grid-y", DesignWorkplane.DEFAULT_MINOR_GRID_SIZE ) );
		workplane.setMinorGridZ( getAsset().getSettings().get( "workpane-minor-grid-z", DesignWorkplane.DEFAULT_MINOR_GRID_SIZE ) );
		workplane.setSnapGridX( getAsset().getSettings().get( "workpane-snap-grid-x", DesignWorkplane.DEFAULT_SNAP_GRID_SIZE ) );
		workplane.setSnapGridY( getAsset().getSettings().get( "workpane-snap-grid-y", DesignWorkplane.DEFAULT_SNAP_GRID_SIZE ) );
		workplane.setSnapGridZ( getAsset().getSettings().get( "workpane-snap-grid-z", DesignWorkplane.DEFAULT_SNAP_GRID_SIZE ) );

		workplane.register( DesignWorkplane.ORIGIN, e -> settings.set( "workpane-origin", e.getNewValue() ) );
		workplane.register( DesignWorkplane.MAJOR_GRID_X, e -> settings.set( "workpane-major-grid-x", e.getNewValue() ) );
		workplane.register( DesignWorkplane.MAJOR_GRID_Y, e -> settings.set( "workpane-major-grid-y", e.getNewValue() ) );
		workplane.register( DesignWorkplane.MAJOR_GRID_Z, e -> settings.set( "workpane-major-grid-z", e.getNewValue() ) );
		workplane.register( DesignWorkplane.MINOR_GRID_X, e -> settings.set( "workpane-major-grid-x", e.getNewValue() ) );
		workplane.register( DesignWorkplane.MINOR_GRID_Y, e -> settings.set( "workpane-major-grid-y", e.getNewValue() ) );
		workplane.register( DesignWorkplane.MINOR_GRID_Z, e -> settings.set( "workpane-major-grid-z", e.getNewValue() ) );
		workplane.register( DesignWorkplane.SNAP_GRID_X, e -> settings.set( "workpane-major-grid-x", e.getNewValue() ) );
		workplane.register( DesignWorkplane.SNAP_GRID_Y, e -> settings.set( "workpane-major-grid-y", e.getNewValue() ) );
		workplane.register( DesignWorkplane.SNAP_GRID_Z, e -> settings.set( "workpane-major-grid-z", e.getNewValue() ) );

		workplane.register( NodeEvent.VALUE_CHANGED, e -> rebuildGrid() );
		rebuildGrid();
	}

	private void validateGrid() {
		Fx.run( () -> {
			Bounds bounds = designPane.parentToLocal( getLayoutBounds() );
			DesignWorkplane workplane = getDesignContext().getWorkplane();
			// TODO What if the bounds are significantly smaller thant the workplane?
			// TODO What if the bounds are so large that the grid is effectively filled in?
			if( workplane.getBounds().contains( bounds ) ) return;
			workplane.setBounds( designPane.parentToLocal( getLayoutBounds() ) );
		} );
		rebuildGrid();
	}

	private void rebuildGrid() {
		// FIXME This takes too much work
		// NOTE Maybe the grid can be removed during pan operations???

		getProgram().getTaskManager().submit( Task.of( "Rebuild grid", () -> {
			try {
				List<Shape> grid = getDesignContext().getCoordinateSystem().getGridLines( getDesignContext().getWorkplane() );
				Fx.run( () -> designPane.setGrid( grid ) );
			} catch( Exception exception ) {
				log.log( Log.ERROR, "Error creating grid", exception );
			}
		} ) );
	}

	private void doSetCurrentLayerById( String id ) {
		getDesign().findLayers( DesignLayer.ID, id ).stream().findFirst().ifPresent( y -> {
			currentLayerProperty().set( y );
			showPropertiesPage( y );
		} );
	}

	private void doSelectedShapesChanged( ListChangeListener.Change<? extends Shape> c ) {
		while( c.next() ) {
			c.getRemoved().stream().map( DesignTool::getDesignData ).forEach( s -> s.setSelected( false ) );
			c.getAddedSubList().stream().map( DesignTool::getDesignData ).forEach( s -> s.setSelected( true ) );

			if( c.getList().size() > 1 ) {
				// Show a combined properties page
				Set<DesignDrawable> designData = c.getList().parallelStream().map( DesignTool::getDesignData ).collect( Collectors.toSet() );
				showPropertiesPage( new MultiNodeSettings( designData ), DesignShape.class );
			} else if( c.getList().size() == 1 ) {
				c.getList().stream().findFirst().map( DesignTool::getDesignData ).ifPresent( this::showPropertiesPage );
			} else {
				hidePropertiesPage();
			}
		}
		deleteAction.updateEnabled();
	}

	private void showPropertiesPage( DesignDrawable drawable ) {
		showPropertiesPage( new NodeSettings( drawable ), drawable.getClass() );
	}

	private void showPropertiesPage( Settings settings, Class<? extends DesignDrawable> type ) {
		SettingsPage page = designPropertiesMap.getSettingsPage( type );
		if( page != null ) {
			page.setSettings( settings );
			PropertiesToolEvent event = new PropertiesToolEvent( DesignTool.this, PropertiesToolEvent.SHOW, page );
			getWorkspace().getEventBus().dispatch( event );
		} else {
			log.log( Log.ERROR, "Unable to find properties page for " + type.getName() );
		}
	}

	private void hidePropertiesPage() {
		getWorkspace().getEventBus().dispatch( new PropertiesToolEvent( DesignTool.this, PropertiesToolEvent.HIDE, null ) );
	}

	public static DesignLayer getDesignData( DesignPaneLayer l ) {
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

	private class DeleteAction extends Action {

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

	private class UndoAction extends Action {

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

	private class RedoAction extends Action {

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

	private class CommandAction extends Action {

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

}
