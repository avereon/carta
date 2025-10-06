package com.avereon.cartesia.tool;

import com.avereon.cartesia.CartesiaMod;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.cursor.Reticle;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignPrint;
import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.design.BaseDesignRenderer;
import com.avereon.data.NodeSettings;
import com.avereon.product.Rb;
import com.avereon.settings.Settings;
import com.avereon.skill.WritableIdentity;
import com.avereon.util.DelayedAction;
import com.avereon.xenon.*;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.ResourceSwitchedEvent;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.asset.type.ProgramPropertiesType;
import com.avereon.xenon.task.Task;
import com.avereon.xenon.tool.guide.GuidedTool;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.xenon.workpane.Workpane;
import com.avereon.xenon.workspace.StatusBar;
import com.avereon.xenon.workspace.Workspace;
import com.avereon.zerra.event.FxEventHub;
import com.avereon.zerra.javafx.Fx;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Transform;
import javafx.stage.Screen;
import lombok.CustomLog;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The design tool is the base class for all design tools.
 */
@CustomLog
public abstract class BaseDesignTool extends GuidedTool implements DesignTool, EventTarget, WritableIdentity {

	protected static final String RETICLE = "reticle";

	protected static final String SELECT_APERTURE_SIZE = "select-aperture-size";

	protected static final String SELECT_APERTURE_UNIT = "select-aperture-unit";

	protected static final String REFERENCE_POINT_SIZE = "reference-point-size";

	protected static final String REFERENCE_POINT_TYPE = "reference-point-type";

	protected static final String REFERENCE_POINT_PAINT = "reference-point-paint";

	protected static final String SETTINGS_VIEW_ZOOM = "view-zoom";

	protected static final String SETTINGS_VIEW_POINT = "view-point";

	protected static final String SETTINGS_VIEW_ROTATE = "view-rotate";

	protected static final String CURRENT_LAYER = "current-layer";

	protected static final String CURRENT_VIEW = "current-view";

	protected static final String ENABLED_LAYERS = "enabled-layers";

	protected static final String VISIBLE_LAYERS = "visible-layers";

	protected static final String GRID_VISIBLE = "grid-visible";

	protected static final String GRID_SNAP_ENABLED = "grid-snap";

	protected static final String DESIGN_CONTEXT = "design-context";

	// TODO This is not connected to the grid pixel threshold yet
	protected static final double MINIMUM_GRID_PIXELS = 3.0;

	@Getter
	private final Node toast;

	@Getter
	private final BaseDesignRenderer renderer;

	// FX properties (what others should be here?)

	// Current:
	// selectAperture

	/**
	 * The current layer for adding geometry to the design.
	 */
	private final ObjectProperty<DesignLayer> currentLayer;

	/**
	 * The selected layer according to the tool guide.
	 */
	private final ObjectProperty<DesignLayer> selectedLayer;

	@Deprecated
	private final ObjectProperty<DesignView> currentView;
	// gridVisible
	// gridSnapEnabled

	// Proposed:
	// viewpoint
	// rotate
	// zoom

	/**
	 * The reticle is the more specialized equivalent of the crosshair cursor.
	 * Whenever the program uses the crosshair cursor, it should use the reticle
	 * cursor.
 	 */
	private final ObjectProperty<Reticle> reticle;

	// selectedShapes
	// visibleShapes
	// portal (viewport)

	// LAYERS
	// Reference points
	// Preview
	// Design layers
	// Grid

	private final Workplane workplane;

	private final Map<String, ProgramAction> commandActions;

	@Getter
	private final PrintAction printAction;

	@Getter
	private final PropertiesAction propertiesAction;

	@Getter
	private final DeleteAction deleteAction;

	@Getter
	private final UndoAction undoAction;

	@Getter
	private final RedoAction redoAction;

	@Getter
	private final DelayedAction storePreviousViewAction;

	private final Stack<DesignPortal> portalStack;

	private com.avereon.event.EventHandler<ResourceSwitchedEvent> assetSwitchListener;

	protected BaseDesignTool( XenonProgramProduct product, Asset asset, BaseDesignRenderer renderer ) {
		super( product, asset );
		addStylesheet( CartesiaMod.STYLESHEET );
		getStyleClass().add( "design-tool" );

		// Actions
		commandActions = new ConcurrentHashMap<>();

		printAction = new PrintAction( product.getProgram() );
		propertiesAction = new PropertiesAction( product.getProgram() );
		deleteAction = new DeleteAction( product.getProgram() );
		undoAction = new UndoAction( product.getProgram() );
		redoAction = new RedoAction( product.getProgram() );

		storePreviousViewAction = new DelayedAction( getProgram().getTaskManager().getExecutor(), this::capturePreviousPortal );
		storePreviousViewAction.setMinTriggerLimit( 1000 );
		storePreviousViewAction.setMaxTriggerLimit( 5000 );

		portalStack = new Stack<>();

		// Create the tool toast
		this.toast = new Label( Rb.text( RbKey.LABEL, "loading-asset", asset.getName() ) + " ..." );
		this.toast.getStyleClass().add( "tool-toast" );
		StackPane.setAlignment( this.toast, Pos.CENTER );

		// Configure the tool renderer
		// The renderer is configured to render to the primary screen by default,
		// but it can be configured to render to different media just as easily.

		// Example DPI values:
		// Sapphire: 162 @ 1x
		// Graphene: 153 @ 1x
		renderer.setDpiX( Screen.getPrimary().getDpi() );
		renderer.setDpiY( Screen.getPrimary().getDpi() );

		// gsettings set org.gnome.desktop.interface scaling-factor 2
		renderer.setOutputScaleX( Screen.getPrimary().getOutputScaleX() );
		renderer.setOutputScaleY( Screen.getPrimary().getOutputScaleY() );

		this.renderer = renderer;

		// Initially the toast is shown and the renderer is hidden
		this.toast.setVisible( true );
		this.renderer.setVisible( false );

		// Initialize the reticle
		reticle = new SimpleObjectProperty<>( DEFAULT_RETICLE );

		// Initialize the cursor to the default cursor
		// There is debate whether this should be the reticle
		setCursor( Cursor.DEFAULT );

		selectedLayer = new SimpleObjectProperty<>();
		currentLayer = new SimpleObjectProperty<>();
		currentView = new SimpleObjectProperty<>();

		this.workplane = new Workplane();
		renderer.setWorkplane( this.workplane );

		// Keep the renderer in the center of the tool
		widthProperty().addListener( ( _, _, _ ) -> updateWorkplaneBoundaries() );
		heightProperty().addListener( ( _, _, _ ) -> updateWorkplaneBoundaries() );
		renderer.viewCenterXProperty().addListener( ( _, _, _ ) -> updateWorkplaneBoundaries() );
		renderer.viewCenterYProperty().addListener( ( _, _, _ ) -> updateWorkplaneBoundaries() );
		renderer.viewCenterZProperty().addListener( ( _, _, _ ) -> updateWorkplaneBoundaries() );
		renderer.viewRotateProperty().addListener( ( _, _, _ ) -> updateWorkplaneBoundaries() );
		renderer.viewZoomXProperty().addListener( ( _, _, _ ) -> updateWorkplaneBoundaries() );
		renderer.viewZoomYProperty().addListener( ( _, _, _ ) -> updateWorkplaneBoundaries() );

		// Register the listener to update the cursor when the reticle changes, and the cursor is also a reticle cursor
		reticle.addListener( ( _, _, n ) -> {
			if( getCursor() instanceof ReticleCursor ) setCursor( n.getCursor( getProgram() ) );
		} );

		// Add the components to the parent
		getChildren().addAll( this.renderer, this.toast );
	}

	protected void ready( OpenAssetRequest request ) throws ToolException {
		super.ready( request );

		setTitle( getAsset().getName() );
		setGraphic( getProgram().getIconLibrary().getIcon( getProduct().getCard().getArtifact() ) );

		getAsset().register( Asset.NAME, e -> setTitle( e.getNewValue() ) );
		getAsset().register( Asset.ICON, e -> setIcon( e.getNewValue() ) );

		getAsset().getUndoManager().undoAvailableProperty().addListener( ( v, o, n ) -> undoAction.updateEnabled() );
		getAsset().getUndoManager().redoAvailableProperty().addListener( ( v, o, n ) -> redoAction.updateEnabled() );

		// Set the design model
		Design design = request.getAsset().getModel();
		getRenderer().setDesign( design );

		// Set the workplane settings TODO replace with settings eventually
		getWorkplane().setGridStyle( GridStyle.CROSS );
		getWorkplane().setMinorGridX( "0.2" );
		getWorkplane().setMinorGridY( "0.2" );

		// Show the grid TODO replace with settings eventually
		getRenderer().setGridVisible( true );

		// Show the first layer TODO replace with settings eventually
		if( !design.getLayers().getLayers().isEmpty() ) {
			getRenderer().setLayerVisible( design.getLayers().getLayers().getFirst(), true );
		}

		// Update the design context when the mouse moves
		addEventFilter( MouseEvent.MOUSE_MOVED, e -> getDesignContext().setMouse( e ) );

		// Link the command context to the user interfaces
		//addEventFilter( KeyEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( MouseEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ScrollEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ZoomEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( GestureEvent.ANY, e -> getCommandContext().handle( e ) );

		// Swap the toast for the renderer
		getToast().setVisible( false );
		getRenderer().setVisible( true );
	}

	@Override
	protected void allocate() throws ToolException {
		super.allocate();

		// Add asset switch listener to remove command prompt
		getProgram().register(
			ResourceSwitchedEvent.SWITCHED, assetSwitchListener = e -> {
				if( isDisplayed() && e.getOldAsset() == getAsset() && e.getNewAsset() != getAsset() ) {
					unregisterStatusBarItems();
				}
			}
		);
	}

	@Override
	protected void activate() throws ToolException {
		super.activate();
		if( !getAsset().isLoaded() ) return;

		DesignCommandContext commandContext = getCommandContext();
		if( commandContext != null ) commandContext.setLastUserTool( this );

		registerStatusBarItems();
		registerCommandCapture();
		registerActions();

		requestFocus();
	}

	@Override
	protected void conceal() throws ToolException {
		unregisterActions();
		unregisterCommandCapture();

		super.conceal();
	}

	@Override
	protected void deallocate() throws ToolException {
		// Remove asset switch listener to unregister status bar items
		getProgram().unregister( ResourceSwitchedEvent.SWITCHED, assetSwitchListener );

		if( renderer != null ) renderer.setDesign( null );

		super.deallocate();
	}

	@Override
	public final CartesiaMod getMod() {
		return (CartesiaMod)getProduct();
	}

	@Override
	public final Design getDesign() {
		return getAssetModel();
	}

	@Override
	public final DesignContext getDesignContext() {
		Design design = getDesign();
		if( design == null ) return null;

		// Lazy instantiate the context
		DesignContext context = design.getValue( DESIGN_CONTEXT );
		if( context == null ) {
			context = new DesignContext( design, new DesignCommandContext( getProduct() ) );
			design.setValue( DESIGN_CONTEXT, context );
		}

		return context;
	}

	@Override
	public final DesignCommandContext getCommandContext() {
		DesignContext context = getDesignContext();
		if( context == null ) return null;
		return context.getDesignCommandContext();
	}

	@Override
	public final Workplane getWorkplane() {
		return workplane;
	}

	@Override
	public final Grid getGridSystem() {
		return getWorkplane().getGridSystem();
	}

	@Override
	public final void setGridSystem( Grid system ) {
		getWorkplane().setGridSystem( system );
	}

	@Override
	public Point3D getViewCenter() {
		return getRenderer().getViewCenter();
	}

	@Override
	public void setViewCenter( Point3D viewCenter ) {
		getRenderer().setViewCenter( viewCenter );
	}

	@Override
	public double getViewRotate() {
		return getRenderer().getViewRotate();
	}

	@Override
	public void setViewRotate( double rotate ) {
		getRenderer().setViewRotate( rotate );
	}

	@Override
	public double getViewZoom() {
		return getRenderer().getViewZoomX();
	}

	@Override
	public void setViewZoom( double viewZoom ) {
		getRenderer().setViewZoom( viewZoom, viewZoom );
	}

	@Override
	public void setView( DesignPortal portal ) {
		setView( portal.center(), portal.zoom(), portal.rotate() );
	}

	@Override
	public void setView( Point3D viewpoint, double zoom ) {
		setView( viewpoint, zoom, getViewRotate() );
	}

	@Override
	public void setView( Point3D viewpoint, double zoom, double rotate ) {
		setViewCenter( viewpoint );
		setViewZoom( zoom );
		setViewRotate( rotate );
	}

	@Override
	public void setView( DesignView view ) {
		setViewCenter( view.getOrigin() );
		setViewZoom( view.getZoom() );
		setViewRotate( view.getRotate() );
		setVisibleLayers( view.getLayers() );
	}

	@Override
	public DesignView createView() {
		DesignView view = new DesignView();
		view.setOrigin( getViewCenter() );
		view.setRotate( getViewRotate() );
		view.setZoom( getViewZoom() );
		view.setLayers( new ArrayList<>( getVisibleLayers() ) );
		return view;
	}

	@Override
	public double getDpi() {
		return getRenderer().getDpiX();
	}

	@Override
	public void setDpi( double dpi ) {
		getRenderer().setDpi( dpi, dpi );
	}

	public DoubleProperty viewDpiProperty() {
		return getRenderer().dpiXProperty();
	}

	@Override
	@Deprecated
	public DesignView getCurrentView() {
		return currentView.get();
	}

	@Override
	@Deprecated
	public void setCurrentView( DesignView view ) {
		currentView.set( Objects.requireNonNull( view ) );
	}

	@Override
	@Deprecated
	public ObjectProperty<DesignView> currentViewProperty() {
		return currentView;
	}

	@Override
	public final ReticleCursor getReticleCursor() {
		return getReticle().getCursor( getProgram() );
	}

	@Override
	public Reticle getReticle() {
		return reticle.get();
	}

	@Override
	public void setReticle( Reticle reticle ) {
		this.reticle.set( reticle );
	}

	public ObjectProperty<Reticle> reticle() {
		return reticle;
	}

	@Override
	@Deprecated
	public void setScreenViewport( Bounds viewport ) {
		setWorldViewport( screenToWorld( viewport ) );
	}

	@Override
	public void setWorldViewport( Bounds viewport ) {
		Bounds toolBounds = getBoundsInLocal();
		if( toolBounds.getWidth() == 0 || toolBounds.getHeight() == 0 ) return;

		Bounds worldBounds = screenToWorld( toolBounds );
		double xZoom = Math.abs( worldBounds.getWidth() / viewport.getWidth() );
		double yZoom = Math.abs( worldBounds.getHeight() / viewport.getHeight() );
		double zoom = Math.min( xZoom, yZoom ) * getViewZoom();

		Point3D worldCenter = new Point3D( viewport.getCenterX(), viewport.getCenterY(), viewport.getCenterZ() );
		setView( worldCenter, zoom );
	}

	@Override
	public Transform getWorldToScreenTransform() {
		return getRenderer().getWorldToScreenTransform();
	}

	@Override
	public Point2D worldToScreen( double x, double y ) {
		return getRenderer().worldToScreen( x, y );
	}

	@Override
	public Point2D worldToScreen( Point2D point ) {
		return getRenderer().worldToScreen( point );
	}

	@Override
	public Point3D worldToScreen( double x, double y, double z ) {
		return getRenderer().worldToScreen( x, y, z );
	}

	@Override
	public Point3D worldToScreen( Point3D point ) {
		return getRenderer().worldToScreen( point );
	}

	@Override
	public Bounds worldToScreen( Bounds bounds ) {
		return getRenderer().worldToScreen( bounds );
	}

	@Override
	public Transform getScreenToWorldTransform() {
		return getRenderer().getScreenToWorldTransform();
	}

	@Override
	public Point2D screenToWorld( double x, double y ) {
		return getRenderer().screenToWorld( x, y );
	}

	@Override
	public Point2D screenToWorld( Point2D point ) {
		return getRenderer().screenToWorld( point );
	}

	@Override
	public Point3D screenToWorld( double x, double y, double z ) {
		return getRenderer().screenToWorld( x, y, z );
	}

	@Override
	public Point3D screenToWorld( Point3D point ) {
		return getRenderer().screenToWorld( point );
	}

	@Override
	public Bounds screenToWorld( Bounds bounds ) {
		return getRenderer().screenToWorld( bounds );
	}

	@Override
	public boolean isLayerVisible( DesignLayer layer ) {
		return getRenderer().isLayerVisible( layer );
	}

	@Override
	public void setLayerVisible( DesignLayer layer, boolean visible ) {
		getRenderer().setLayerVisible( layer, visible );
	}

	@Override
	public List<DesignLayer> getVisibleLayers() {
		return getRenderer().getVisibleLayers();
	}

	@Override
	public void setVisibleLayers( Collection<DesignLayer> layers ) {
		getRenderer().setVisibleLayers( layers );
	}

	@Override
	public DesignLayer getCurrentLayer() {
		return currentLayer.get();
	}

	@Override
	public void setCurrentLayer( DesignLayer layer ) {
		currentLayer.set( Objects.requireNonNull( layer ) );
	}

	@Override
	public ObjectProperty<DesignLayer> currentLayerProperty() {
		return currentLayer;
	}

	@Override
	public DesignLayer getSelectedLayer() {
		return selectedLayer.get();
	}

	@Override
	public void setSelectedLayer( DesignLayer layer ) {
		selectedLayer.set( Objects.requireNonNull( layer ) );
	}

	@Override
	public ObjectProperty<DesignLayer> selectedLayerProperty() {
		return selectedLayer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void zoom( Point3D anchor, double factor ) {
		Fx.onFxOrCurrent( () -> getRenderer().zoom( anchor, factor ) );
	}

	// NEXT Insert common design tool implementations here
	// Many implementations found in DesignToolV2 can be moved here

	@Override
	public BaseDesignRenderer getScreenDesignRenderer() {
		return renderer;
	}

	protected CommandPrompt getCommandPrompt() {
		return getDesignContext().getDesignCommandContext().getCommandPrompt();
	}

	protected CoordinateStatus getCoordinateStatus() {
		return getDesignContext().getCoordinateStatus();
	}

	protected void registerStatusBarItems() {
		Fx.run( () -> {
			Workspace workspace = getWorkspace();
			if( workspace == null ) return;

			StatusBar bar = workspace.getStatusBar();
			bar.setLeftToolItems( getCommandPrompt() );
			bar.setRightToolItems( getCoordinateStatus() );
		} );
	}

	protected void unregisterStatusBarItems() {
		Fx.run( () -> {
			Workspace workspace = getWorkspace();
			if( workspace == null ) return;

			StatusBar bar = workspace.getStatusBar();
			bar.removeLeftToolItems( getCommandPrompt() );
			bar.removeRightToolItems( getCoordinateStatus() );
		} );
	}

	protected void registerCommandCapture() {
		// If there is already a command capture handler then remove it
		// (because it may belong to a different design)
		unregisterCommandCapture();

		// Add the design command capture handler. This captures all key events that
		// make it to the tool and forwards them to the command context which
		// will help determine what to do.
		addEventHandler( KeyEvent.ANY, getCommandContext() );
	}

	@SuppressWarnings( "unchecked" )
	protected void unregisterCommandCapture() {
		Workpane workpane = getWorkpane();
		EventHandler<KeyEvent> handler = (EventHandler<KeyEvent>)workpane.getProperties().get( "design-tool-command-capture" );
		if( handler != null ) workpane.removeEventHandler( KeyEvent.ANY, handler );
	}

	protected void registerActions() {
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

		//ProgramAction gridVisibleToggleAction = pushCommandAction( "grid-toggle", isGridVisible() ? "enabled" : "disabled" );
		//gridVisible().addListener( gridVisibleToggleHandler = ( p, o, n ) -> gridVisibleToggleAction.setState( n ? "enabled" : "disabled" ) );
		//ProgramAction snapGridToggleAction = pushCommandAction( "snap-grid-toggle", isGridSnapEnabled() ? "enabled" : "disabled" );
		//gridSnapEnabled().addListener( snapGridToggleHandler = ( p, o, n ) -> snapGridToggleAction.setState( n ? "enabled" : "disabled" ) );

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

	protected void unregisterActions() {
		pullMenus();
		pullTools();

		//if( gridVisibleToggleHandler != null ) gridVisible().removeListener( gridVisibleToggleHandler );
		//pullCommandAction( "grid-toggle" );
		//if( snapGridToggleHandler != null ) gridSnapEnabled().removeListener( snapGridToggleHandler );
		//pullCommandAction( "snap-grid-toggle" );

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

	private void capturePreviousPortal() {
		portalStack.push( new DesignPortal( getViewCenter(), getViewZoom(), getViewRotate() ) );
	}

	private ProgramAction pushCommandAction( String key ) {
		return pushCommandAction( key, null );
	}

	private ProgramAction pushCommandAction( String key, String initialActionState ) {
		ActionProxy proxy = getProgram().getActionLibrary().getAction( key );
		ProgramAction action = commandActions.computeIfAbsent( key, k -> new CommandAction( this, getProgram(), proxy.getCommand() ) );
		if( initialActionState != null ) action.setState( initialActionState );
		pushAction( key, action );
		return action;
	}

	private void pullCommandAction( String key ) {
		pullAction( key, commandActions.get( key ) );
	}

	private void updateWorkplaneBoundaries() {
		// Update the workplane boundaries based on the viewport

		// Determine the viewport boundaries in world coordinates
		// Note that the viewport can be panned, zoomed and rotated
		// A world rectangle could be determined from the viewport

		workplane.setBounds( getRenderer().screenToWorld( getRenderer().getLayoutBounds() ) );
	}

	protected class PrintAction extends ProgramAction {

		protected PrintAction( Xenon program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public void handle( ActionEvent event ) {
			getProgram().getTaskManager().submit( new DesignPrintTask( getProgram(), BaseDesignTool.this, getAsset(), (DesignPrint)null ) );
			//getProgram().getTaskManager().submit( new DesignAwtPrintTask( getProgram(), FxRenderDesignTool.this, getAsset(), (DesignPrint)null ) );
		}

	}

	// FIXME Is this a duplicate of com.avereon.xenon.action.PropertiesAction?
	protected class PropertiesAction extends ProgramAction {

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
			SettingsPage assetSettingsPage = asset.getType().getSettingsPages().get( "grid" );
			SettingsPage designSettingsPage = asset.getType().getSettingsPages().get( "asset" );

			Settings assetSettings = getAssetSettings();
			Settings designSettings = new NodeSettings( getAsset().getModel() );

			// Set the settings for the pages
			assetSettingsPage.setSettings( assetSettings );
			designSettingsPage.setSettings( designSettings );

			// Switch to a task thread to get the tool
			getProgram().getTaskManager().submit( Task.of( () -> {
				try {
					// Show the properties tool
					getProgram().getAssetManager().openAsset( ProgramPropertiesType.URI, getWorkpane() ).get();

					// Fire the show request on the workspace event bus
					PropertiesToolEvent toolEvent = new PropertiesToolEvent( PropertiesAction.this, PropertiesToolEvent.SHOW, designSettingsPage, assetSettingsPage );
					Workspace workspace = getProgram().getWorkspaceManager().getActiveWorkspace();
					FxEventHub workspaceEventBus = workspace.getEventBus();
					Fx.run( () -> workspaceEventBus.dispatch( toolEvent ) );
				} catch( Exception exception ) {
					log.atError( exception ).log();
				}
			} ) );
		}

	}

	protected class DeleteAction extends ProgramAction {

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

	protected class UndoAction extends ProgramAction {

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

	protected class RedoAction extends ProgramAction {

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
