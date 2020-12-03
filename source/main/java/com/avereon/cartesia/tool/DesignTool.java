package com.avereon.cartesia.tool;

import com.avereon.cartesia.*;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.snap.SnapGrid;
import com.avereon.data.IdNode;
import com.avereon.data.MultiNodeSettings;
import com.avereon.data.NodeEvent;
import com.avereon.data.NodeSettings;
import com.avereon.settings.Settings;
import com.avereon.util.Log;
import com.avereon.util.TypeReference;
import com.avereon.xenon.Action;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.PropertiesToolEvent;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.tool.guide.GuideNode;
import com.avereon.xenon.tool.guide.GuidedTool;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.zerra.javafx.Fx;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;

import java.util.Collection;
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

	private static final String SETTINGS_ZOOM = "zoom";

	private static final String SETTINGS_PAN = "pan";

	private static final String CURRENT_LAYER = "layer";

	private static final String VISIBLE_LAYERS = "visible-layers";

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
		this.selectedShapes.addListener( (ListChangeListener<? super Shape>)this::doSelectShapes );
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

	public Point3D getPan() {
		return designPane == null ? Point3D.ZERO : designPane.getViewPoint();
	}

	public void setPan( Point3D point ) {
		if( designPane != null ) designPane.setViewPoint( point );
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
		designPane.setLayerVisible( layer, visible );
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

	public Point3D getViewPoint() {
		return designPane.getViewPoint();
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
		Workplane workplane = getDesignContext().getWorkplane();
		boolean gridSnapEnabled = workplane.isGridSnapEnabled();
		return gridSnapEnabled ? gridSnap.snap( this, worldPoint ) : worldPoint;
	}

	public Point3D worldToMouse( double x, double y, double z ) {
		return designPane == null ? Point3D.ZERO : designPane.localToParent( x, y, z );
	}

	public Point3D worldToMouse( Point3D point ) {
		return designPane == null ? Point3D.ZERO : designPane.localToParent( point );
	}

	public boolean isGridVisible() {
		return getDesignContext().getWorkplane().isGridVisible();
	}

	public void setGridVisible( boolean visible ) {
		getDesignContext().getWorkplane().setGridVisible( visible );
	}

	public boolean isGridSnapEnabled() {
		return getDesignContext().getWorkplane().isGridSnapEnabled();
	}

	public void setGridSnapEnabled( boolean enabled ) {
		getDesignContext().getWorkplane().setGridSnapEnabled( enabled );
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
		designPane.loadDesign( design );
		designPane.setDpi( Screen.getPrimary().getDpi() );
		layersGuide.link( designPane );
		//viewsGuide.init( design );
		//printsGuide.init( design );

		// Keep the design pane centered when resizing
		// These should be added before updating the pan and zoom
		widthProperty().addListener( ( p, o, n ) -> designPane.recenter() );
		heightProperty().addListener( ( p, o, n ) -> designPane.recenter() );

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
		getDesignContext().getWorkplane().register( Workplane.GRID_VISIBLE, e -> {
			// This one is particularly interesting because the design pane is not
			// technically the authoritative source of the grid visible property, and
			// it's not the design pane that sets up the listener, the design tool
			// does. That makes this one unique...for now.
			// I guess in the FX world this would be a bind, but that isn't available
			// yet with data nodes.
			designPane.setGridVisible( e.getNewValue() );
		} );

		// Workplane settings
		configureWorkplane();

		// Get tool settings
		double selectApertureRadius = Double.parseDouble( getSettings().get( SELECT_APERTURE_RADIUS, "1.0" ) );
		DesignUnit selectApertureUnit = DesignUnit.valueOf( getSettings().get( SELECT_APERTURE_UNIT, DesignUnit.MILLIMETER.name() ).toUpperCase() );
		setSelectTolerance( new DesignValue( selectApertureRadius, selectApertureUnit ) );
		setPan( ParseUtil.parsePoint3D( getSettings().get( SETTINGS_PAN, "0,0,0" ) ) );
		setZoom( Double.parseDouble( getSettings().get( SETTINGS_ZOOM, "1.0" ) ) );
		setReticle( ReticleCursor.valueOf( getSettings().get( RETICLE, ReticleCursor.DUPLEX.getClass().getSimpleName() ).toUpperCase() ) );
		design.findLayers( DesignLayer.ID, getSettings().get( CURRENT_LAYER ) ).stream().findFirst().ifPresent( this::setCurrentLayer );

		// Restore the list of visible layers
		Set<String> visibleLayerIds = getSettings().get( VISIBLE_LAYERS, new TypeReference<>() {}, Set.of() );
		design.getAllLayers().forEach( l -> setLayerVisible( l, visibleLayerIds.contains( l.getId() ) ) );

		// Settings listeners
		getSettings().register( RETICLE, e -> setReticle( ReticleCursor.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		getSettings().register( SELECT_APERTURE_RADIUS, e -> setSelectTolerance( new DesignValue( Double.parseDouble( (String)e.getNewValue() ), selectApertureUnit ) ) );
		getSettings().register( SELECT_APERTURE_UNIT, e -> setSelectTolerance( new DesignValue( selectApertureRadius, DesignUnit.valueOf( ((String)e.getNewValue()).toUpperCase() ) ) ) );

		// Add layout bounds property listener
		layoutBoundsProperty().addListener( ( p, o, n ) -> {
			validateGrid();
		} );

		// Add pan property listener
		designPane.viewPointProperty().addListener( ( p, o, n ) -> {
			getSettings().set( SETTINGS_PAN, n.getX() + "," + n.getY() + "," + n.getZ() );
			validateGrid();
		} );

		// Add zoom property listener
		designPane.zoomProperty().addListener( ( p, o, n ) -> {
			getCoordinateStatus().updateZoom( n.doubleValue() );
			getSettings().set( SETTINGS_ZOOM, n.doubleValue() );
			validateGrid();
		} );

		// Add visible layers listener
		designPane.visibleLayersProperty().addListener( this::doUpdateVisibleLayers );

		// Add current layer property listener
		currentLayerProperty().addListener( ( p, o, n ) -> {
			getSettings().set( CURRENT_LAYER, n.getId() );
		} );

		addEventFilter( MouseEvent.MOUSE_MOVED, e -> getDesignContext().setMouse( e ) );
		addEventFilter( MouseEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( MouseDragEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ScrollEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ZoomEvent.ANY, e -> getCommandContext().handle( e ) );

		if( isActive() ) activate();
		getCoordinateStatus().updateZoom( getZoom() );
		designPane.recenter();
		validateGrid();
	}

	private void doUpdateVisibleLayers( SetChangeListener.Change<? extends DesignLayer> c ) {
		getSettings().set( VISIBLE_LAYERS, c.getSet().stream().map( IdNode::getId ).collect( Collectors.toSet() ) );
	}

	@Override
	protected void guideNodesSelected( Set<GuideNode> oldNodes, Set<GuideNode> newNodes ) {
		newNodes.stream().findFirst().ifPresent( n -> doSetCurrentLayerById( n.getId() ) );
	}

	@Override
	protected void activate() throws ToolException {
		super.activate();
		if( isReady() ) {
			registerActions();
			getDesignContext().getCommandContext().setLastActiveDesignTool( this );
			registerStatusBarItems();
		}
		requestFocus();
	}

	@Override
	protected void conceal() throws ToolException {
		super.conceal();
		unregisterActions();
		if( isReady() && isLastTool() ) unregisterStatusBarItems();
	}

	public DesignPane getDesignPane() {
		return designPane;
	}

	private void registerStatusBarItems() {
		CommandPrompt prompt = getCommandPrompt();
		getScene().addEventHandler( KeyEvent.ANY, prompt );
		getWorkspace().getStatusBar().addLeftItems( prompt );
		getWorkspace().getStatusBar().addRightItems( getCoordinateStatus() );
	}

	private void unregisterStatusBarItems() {
		CommandPrompt prompt = getCommandPrompt();
		getScene().removeEventHandler( KeyEvent.ANY, prompt );
		getWorkspace().getStatusBar().removeLeftItems( prompt );
		getWorkspace().getStatusBar().removeRightItems( getCoordinateStatus() );
	}

	private void registerActions() {
		pushToolActions( "grid-toggle", "snap-grid-toggle" );

		pushAction( "delete", deleteAction );
		pushAction( "undo", undoAction );
		pushAction( "redo", redoAction );

		// FIXME Is there a more concise way of doing this? There will be others
		Action snapGridToggleAction = pushCommandAction( "snap-grid-toggle", isGridSnapEnabled() ? "enabled" : "disabled" );
		getDesignContext().getWorkplane().register( Workplane.GRID_SNAP, e -> {
			snapGridToggleAction.setState( e.getNewValue() ? "enabled" : "disabled" );
		} );
		Action gridVisibleToggleAction = pushCommandAction( "grid-toggle", isGridVisible() ? "enabled" : "disabled" );
		getDesignContext().getWorkplane().register( Workplane.GRID_VISIBLE, e -> {
			gridVisibleToggleAction.setState( e.getNewValue() ? "enabled" : "disabled" );
		} );
	}

	private void unregisterActions() {
		pullCommandAction( "snap-grid-toggle" );
		pullCommandAction( "grid-toggle" );

		pullAction( "delete", deleteAction );
		pullAction( "undo", undoAction );
		pullAction( "redo", redoAction );

		pullToolActions();
	}

	private Action pushCommandAction( String key ) {
		return pushCommandAction( key, null );
	}

	private Action pushCommandAction( String key, String initialActionState ) {
		String shortcut = getProduct().rb().textOr( BundleKey.ACTION, key + CommandMap.COMMAND_SUFFIX, "" ).toLowerCase();
		Action action = commandActions.computeIfAbsent( key, k -> new CommandAction( getProgram(), shortcut ) );
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

	public void mouseSelect( double x, double y, double z, boolean toggle ) {
		Fx.run( () -> {
			if( !toggle ) selectedShapes().clear();

			List<Shape> selection = designPane.apertureSelect( x, y, z, getSelectTolerance() );
			selection.stream().findFirst().ifPresent( shape -> {
				if( toggle && getDesignData( shape ).isSelected() ) {
					selectedShapes().remove( shape );
				} else {
					selectedShapes().add( shape );
				}
			} );
		} );
	}

	public void windowSelect( Point3D a, Point3D b, boolean contains ) {
		Fx.run( () -> {
			selectedShapes().clear();
			List<Shape> selection = designPane.windowSelect( a, b, contains );
			selectedShapes().addAll( selection );
		} );
	}

	private void configureWorkplane() {
		Settings settings = getAsset().getSettings();
		Workplane workplane = getDesignContext().getWorkplane();

		workplane.setOrigin( getAsset().getSettings().get( "workpane-origin", Workplane.DEFAULT_ORIGIN ) );
		workplane.setMajorGridX( getAsset().getSettings().get( "workpane-major-grid-x", Workplane.DEFAULT_MAJOR_GRID_SIZE ) );
		workplane.setMajorGridY( getAsset().getSettings().get( "workpane-major-grid-y", Workplane.DEFAULT_MAJOR_GRID_SIZE ) );
		workplane.setMajorGridZ( getAsset().getSettings().get( "workpane-major-grid-z", Workplane.DEFAULT_MAJOR_GRID_SIZE ) );
		workplane.setMinorGridX( getAsset().getSettings().get( "workpane-minor-grid-x", Workplane.DEFAULT_MINOR_GRID_SIZE ) );
		workplane.setMinorGridY( getAsset().getSettings().get( "workpane-minor-grid-y", Workplane.DEFAULT_MINOR_GRID_SIZE ) );
		workplane.setMinorGridZ( getAsset().getSettings().get( "workpane-minor-grid-z", Workplane.DEFAULT_MINOR_GRID_SIZE ) );
		workplane.setSnapGridX( getAsset().getSettings().get( "workpane-snap-grid-x", Workplane.DEFAULT_SNAP_GRID_SIZE ) );
		workplane.setSnapGridY( getAsset().getSettings().get( "workpane-snap-grid-y", Workplane.DEFAULT_SNAP_GRID_SIZE ) );
		workplane.setSnapGridZ( getAsset().getSettings().get( "workpane-snap-grid-z", Workplane.DEFAULT_SNAP_GRID_SIZE ) );
		setGridVisible( getAsset().getSettings().get( Workplane.GRID_VISIBLE, Boolean.class, Workplane.DEFAULT_GRID_VISIBLE ) );
		setGridSnapEnabled( getAsset().getSettings().get( Workplane.GRID_SNAP, Boolean.class, Workplane.DEFAULT_GRID_SNAP_ENABLED ) );

		workplane.register( Workplane.ORIGIN, e -> settings.set( "workpane-origin", e.getNewValue() ) );
		workplane.register( Workplane.MAJOR_GRID_X, e -> settings.set( "workpane-major-grid-x", e.getNewValue() ) );
		workplane.register( Workplane.MAJOR_GRID_Y, e -> settings.set( "workpane-major-grid-y", e.getNewValue() ) );
		workplane.register( Workplane.MAJOR_GRID_Z, e -> settings.set( "workpane-major-grid-z", e.getNewValue() ) );
		workplane.register( Workplane.MINOR_GRID_X, e -> settings.set( "workpane-major-grid-x", e.getNewValue() ) );
		workplane.register( Workplane.MINOR_GRID_Y, e -> settings.set( "workpane-major-grid-y", e.getNewValue() ) );
		workplane.register( Workplane.MINOR_GRID_Z, e -> settings.set( "workpane-major-grid-z", e.getNewValue() ) );
		workplane.register( Workplane.SNAP_GRID_X, e -> settings.set( "workpane-major-grid-x", e.getNewValue() ) );
		workplane.register( Workplane.SNAP_GRID_Y, e -> settings.set( "workpane-major-grid-y", e.getNewValue() ) );
		workplane.register( Workplane.SNAP_GRID_Z, e -> settings.set( "workpane-major-grid-z", e.getNewValue() ) );
		workplane.register( Workplane.GRID_VISIBLE, e -> settings.set( Workplane.GRID_VISIBLE, e.getNewValue() ) );
		workplane.register( Workplane.GRID_SNAP, e -> settings.set( Workplane.GRID_SNAP, e.getNewValue() ) );

		workplane.register( Workplane.GRID_VISIBLE, e -> this.setGridVisible( e.getNewValue() ) );
		workplane.register( Workplane.GRID_SNAP, e -> this.setGridSnapEnabled( e.getNewValue() ) );

		workplane.register( NodeEvent.VALUE_CHANGED, e -> rebuildGrid() );
		Fx.run( this::rebuildGrid );
	}

	private void validateGrid() {
		Fx.run( () -> {
			Bounds bounds = designPane.parentToLocal( getLayoutBounds() );
			Workplane workplane = getDesignContext().getWorkplane();
			if( workplane.getBounds().contains( bounds ) ) return;
			workplane.setBounds( designPane.parentToLocal( getLayoutBounds() ) );
			rebuildGrid();
		} );
	}

	private void rebuildGrid() {
		try {
			CoordinateSystem system = CoordinateSystem.ORTHO;
			designPane.setGrid( system.getGridLines( getDesignContext().getWorkplane() ) );
		} catch( Exception exception ) {
			log.log( Log.ERROR, "Error creating grid", exception );
		}
	}

	private void doSelectShapes( ListChangeListener.Change<? extends Shape> c ) {
		while( c.next() ) {
			c.getRemoved().stream().map( DesignTool::getDesignData ).forEach( s -> s.setSelected( false ) );
			c.getAddedSubList().stream().map( DesignTool::getDesignData ).forEach( s -> s.setSelected( true ) );

			System.out.println( "select shape count=" + c.getList().size() );

			if( c.getList().size() > 1 ) {
				// Show a combined properties page
				Set<DesignDrawable> designData = c.getList().parallelStream().map( DesignTool::getDesignData ).collect( Collectors.toSet() );
				showPropertiesPage( new MultiNodeSettings( designData ), DesignShape.class );
			} else if( c.getList().size() == 1 ) {
				c.getList().stream().findFirst().map( DesignTool::getDesignData ).ifPresent( this::showPropertiesPage );
			} else {
				getWorkspace().getEventBus().dispatch( new PropertiesToolEvent( DesignTool.this, PropertiesToolEvent.HIDE, null ) );
			}
		}
		deleteAction.updateEnabled();
	}

	private void showPropertiesPage( DesignDrawable drawable ) {
		showPropertiesPage( new NodeSettings( drawable ), drawable.getClass() );
	}

	private void showPropertiesPage( Settings settings, Class<? extends DesignDrawable> type ) {
		SettingsPage page = designPropertiesMap.getSettingsPage( type );
		page.setSettings( settings );
		PropertiesToolEvent event = new PropertiesToolEvent( DesignTool.this, PropertiesToolEvent.SHOW, page );
		getWorkspace().getEventBus().dispatch( event );
	}

	private void doDeleteShapes( Collection<DesignShape> shapes ) {
		runTask( () -> shapes.forEach( s -> s.getParentLayer().removeShape( s ) ) );
		selectedShapes.clear();
	}

	private void doSetCurrentLayerById( String id ) {
		getDesign().findLayers( DesignLayer.ID, id ).stream().findFirst().ifPresent( y -> {
			currentLayerProperty().set( y );
			showPropertiesPage( y );
		} );
	}

	static DesignLayer getDesignData( DesignPaneLayer l ) {
		return (DesignLayer)DesignShapeView.getDesignData( l );
	}

	static DesignShape getDesignData( Shape s ) {
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
			doDeleteShapes( selectedShapes().stream().map( DesignTool::getDesignData ).collect( Collectors.toSet() ) );
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
