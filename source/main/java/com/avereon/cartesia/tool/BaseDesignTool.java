package com.avereon.cartesia.tool;

import com.avereon.cartesia.CartesiaMod;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.cursor.Reticle;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.design.DesignRenderer;
import com.avereon.product.Rb;
import com.avereon.skill.WritableIdentity;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.tool.guide.GuidedTool;
import com.avereon.xenon.workpane.ToolException;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventTarget;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import lombok.CustomLog;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
	private final DesignRenderer renderer;

	// FX properties (what others should be here?)

	// NEXT This viewCenter property and the renderer viewCenter property are not the same and are not linked
	private final ObjectProperty<Point3D> viewCenter;

	private final DoubleProperty viewRotate;

	private final DoubleProperty viewZoom;

	// Current:
	// selectAperture
	private final ObjectProperty<DesignLayer> selectedLayer;

	private final ObjectProperty<DesignLayer> currentLayer;

	@Deprecated
	private final ObjectProperty<DesignView> currentView;
	// gridVisible
	// gridSnapEnabled

	// Proposed:
	// viewpoint
	// rotate
	// zoom
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

	protected BaseDesignTool( XenonProgramProduct product, Asset asset, DesignRenderer renderer ) {
		super( product, asset );
		addStylesheet( CartesiaMod.STYLESHEET );
		getStyleClass().add( "design-tool" );

		// Create the tool toast
		this.toast = new Label( Rb.text( RbKey.LABEL, "loading-asset", asset.getName() ) + " ..." );
		this.toast.getStyleClass().add( "tool-toast" );
		StackPane.setAlignment( this.toast, Pos.CENTER );

		// Configure the tool renderer
		// The renderer is configured to render to the primary screen by default,
		// but it can be configured to render to different media just as easily by
		// changing the DPI setting.
		// Should be:
		// Sapphire: 162 @ 1x
		// Graphene: 153 @ 1x
		renderer.setDpiX( Screen.getPrimary().getDpi() );
		renderer.setDpiY( Screen.getPrimary().getDpi() );

		renderer.setOutputScaleX( Screen.getPrimary().getOutputScaleX() );
		renderer.setOutputScaleY( Screen.getPrimary().getOutputScaleY() );
		this.renderer = renderer;

		// Initially the toast is shown and the renderer is hidden
		this.toast.setVisible( true );
		this.renderer.setVisible( false );

		viewCenter = new SimpleObjectProperty<>( DEFAULT_CENTER );
		viewRotate = new SimpleDoubleProperty( DEFAULT_ROTATE );
		viewZoom = new SimpleDoubleProperty( DEFAULT_ZOOM.getX() );

		reticle = new SimpleObjectProperty<>( DEFAULT_RETICLE );
		setCursor( getReticleCursor() );

		selectedLayer = new SimpleObjectProperty<>();
		currentLayer = new SimpleObjectProperty<>();
		currentView = new SimpleObjectProperty<>();

		this.workplane = new Workplane();
		renderer.setWorkplane( this.workplane );

		// Keep the renderer in the center of the tool
		widthProperty().addListener( ( _, _, _ ) -> updateWorkplaneBoundaries() );
		heightProperty().addListener( ( _, _, _ ) -> updateWorkplaneBoundaries() );

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

		// Link the command context to the user interfaces
		addEventFilter( KeyEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( MouseEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ScrollEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ZoomEvent.ANY, e -> getCommandContext().handle( e ) );

		// Swap the toast for the renderer
		getToast().setVisible( false );
		getRenderer().setVisible( true );
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
		return viewCenter.get();
	}

	@Override
	public void setViewCenter( Point3D viewCenter ) {
		this.viewCenter.set( Objects.requireNonNull( viewCenter ) );
	}

	public ObjectProperty<Point3D> viewCenterProperty() {
		return viewCenter;
	}

	@Override
	public double getViewRotate() {
		return viewRotate.get();
	}

	@Override
	public void setViewRotate( double rotate ) {
		this.viewRotate.set( rotate );
	}

	public DoubleProperty viewRotateProperty() {
		return viewRotate;
	}

	@Override
	public double getViewZoom() {
		return viewZoom.get();
	}

	@Override
	public void setViewZoom( double viewZoom ) {
		this.viewZoom.set( viewZoom );
	}

	public DoubleProperty viewZoomProperty() {
		return viewZoom;
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
		getRenderer().setDpi( dpi );
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

	// NEXT Insert implementations here

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

	@Override
	public DesignLayer getCurrentLayer() {
		return currentLayer.get();
	}

	@Override
	public void setCurrentLayer( DesignLayer layer ) {
		currentLayer.set( Objects.requireNonNull( layer ) );
	}

	@Override
	@Deprecated
	public ObjectProperty<DesignLayer> currentLayerProperty() {
		return currentLayer;
	}

	@Override
	public DesignRenderer getScreenDesignRenderer() {
		return renderer;
	}

	private void updateWorkplaneBoundaries() {
		// Update the workplane boundaries based on the viewport

		// Determine the viewport boundaries in world coordinates
		// Note that the viewport can be panned, zoomed and rotated
		// A world rectangle could be determined from the viewport

		double width = getRenderer().getWidth();
		double height = getRenderer().getHeight();
		Bounds source = new BoundingBox( 0, 0, width, height );
		//Bounds source = getRenderer().getBoundsInLocal();
		//		System.out.println();
		//		System.out.println( "source bounds: " + source );

		// NEXT Continue work to manage the workplane bounds
		Bounds workplaneBounds = getRenderer().screenToWorld( source );
		//		System.out.println( "target bounds: " + workplaneBounds );
		workplane.setBounds( workplaneBounds );
	}

}
