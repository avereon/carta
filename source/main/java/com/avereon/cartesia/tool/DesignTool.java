package com.avereon.cartesia.tool;

import com.avereon.cartesia.CartesiaMod;
import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.data.CsaShape;
import com.avereon.cartesia.data.Design;
import com.avereon.util.Log;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.xenon.workspace.Workspace;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;

import java.util.List;

public abstract class DesignTool extends ProgramTool {

	public static final String RETICLE = "reticle";

	public static final String SELECT_APERTURE_RADIUS = "select-aperture-radius";

	public static final String SELECT_APERTURE_UNIT = "select-aperture-unit";

	private static final String SETTINGS_ZOOM = "zoom";

	private static final String SETTINGS_PAN = "pan";

	private static final System.Logger log = Log.get();

	private final CommandPrompt prompt;

	private final CoordinateStatus coordinates;

	private ReticleCursor reticle;

	private DesignPane designPane;

	private final Pane selectPane;

	private final SelectWindow selectWindow;

	private Point3D mousePoint;

	private Point3D dragAnchor;

	private Point3D viewAnchor;

	private double selectApertureRadius;

	private DesignUnit selectApertureUnit;

	public DesignTool( ProgramProduct product, Asset asset ) {
		super( product, asset );
		getStyleClass().add( "design-tool" );

		addStylesheet( CartesiaMod.STYLESHEET );

		this.prompt = new CommandPrompt( this );
		this.coordinates = new CoordinateStatus( this );

		this.selectWindow = new SelectWindow();
		this.selectWindow.getStyleClass().add( "select" );
		this.selectPane = new Pane();
		this.selectPane.getChildren().addAll( selectWindow );

		// Initial values from settings
		setReticle( ReticleCursor.valueOf( product.getSettings().get( RETICLE, ReticleCursor.DUPLEX.getClass().getSimpleName() ).toUpperCase() ) );
		double selectApertureRadius = Double.parseDouble( product.getSettings().get( SELECT_APERTURE_RADIUS, "1.0" ) );
		DesignUnit selectApertureUnit = DesignUnit.valueOf( product.getSettings().get( SELECT_APERTURE_UNIT, DesignUnit.MILLIMETER.name() ).toUpperCase() );
		setSelectAperture( selectApertureRadius, selectApertureUnit );

		// Settings listeners
		product.getSettings().register( RETICLE, e -> setReticle( ReticleCursor.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		product.getSettings().register( SELECT_APERTURE_RADIUS, e -> setSelectAperture( Double.parseDouble( (String)e.getNewValue() ), selectApertureUnit ) );
		product
			.getSettings()
			.register( SELECT_APERTURE_UNIT, e -> setSelectAperture( selectApertureRadius, DesignUnit.valueOf( ((String)e.getNewValue()).toUpperCase() ) ) );

		addEventFilter( KeyEvent.ANY, this::key );
		addEventFilter( MouseEvent.MOUSE_MOVED, this::mouseMove );
		addEventFilter( MouseEvent.MOUSE_PRESSED, this::mousePress );
		addEventFilter( MouseEvent.MOUSE_DRAGGED, this::mouseDrag );
		addEventFilter( MouseEvent.MOUSE_RELEASED, this::mouseRelease );
		addEventFilter( ScrollEvent.SCROLL, this::zoom );
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
		}
	}

	public Design getDesign() {
		return getAssetModel();
	}

	public CommandPrompt getCommandPrompt() {
		return prompt;
	}

	public Point3D getMousePoint() {
		return mousePoint;
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

	public void setSelectAperture( double radius, DesignUnit unit ) {
		selectApertureRadius = radius;
		selectApertureUnit = unit;
	}

	@Override
	protected void ready( OpenAssetRequest request ) throws ToolException {
		super.ready( request );

		setTitle( getAsset().getName() );
		setGraphic( getProgram().getIconLibrary().getIcon( getProduct().getCard().getArtifact() ) );

		getAsset().register( Asset.NAME, e -> setTitle( e.getNewValue() ) );
		getAsset().register( Asset.ICON, e -> setIcon( e.getNewValue() ) );

		designPane = new DesignPane( request.getAsset().getModel() );
		designPane.setDpi( Screen.getPrimary().getDpi() );

		getChildren().addAll( designPane, selectPane );

		// Keep the design pane centered when resizing
		widthProperty().addListener( ( p, o, n ) -> designPane.recenter() );
		heightProperty().addListener( ( p, o, n ) -> designPane.recenter() );

		// Get tool settings
		setPan( ParseUtil.parsePoint3D( getSettings().get( SETTINGS_PAN, "0,0,0" ) ) );
		setZoom( Double.parseDouble( getSettings().get( SETTINGS_ZOOM, "1.0" ) ) );

		// Add pan property listener
		designPane.viewPointProperty().addListener( ( p, o, n ) -> {
			getSettings().set( SETTINGS_PAN, n.getX() + "," + n.getY() + "," + n.getZ() );
		} );

		// Add zoom property listener
		designPane.zoomProperty().addListener( ( p, o, n ) -> {
			getCoordinateStatus().updateZoom( n.doubleValue() );
			getSettings().set( SETTINGS_ZOOM, n.doubleValue() );
		} );

		designPane.recenter();
	}

	@Override
	protected void activate() throws ToolException {
		super.activate();
		// Not sure I want to reset when activated
		//getCommandPrompt().reset();
		Workspace workspace = getWorkspace();
		if( workspace != null ) {
			workspace.getStatusBar().addLeft( getCommandPrompt() );
			workspace.getStatusBar().addRight( getCoordinateStatus() );
		}
		requestFocus();
	}

	@Override
	protected void deactivate() throws ToolException {
		super.conceal();
		Workspace workspace = getWorkspace();
		if( workspace != null ) {
			workspace.getStatusBar().removeRight( getCoordinateStatus() );
			workspace.getStatusBar().removeLeft( getCommandPrompt() );
		}
	}

	protected Point3D mouseToWorld( double x, double y, double z ) {
		return designPane == null ? Point3D.ZERO : designPane.mouseToWorld( x, y, z );
	}

	public ReticleCursor getReticle() {
		return reticle;
	}

	private void setReticle( ReticleCursor reticle ) {
		this.reticle = reticle;
		if( getCursor() instanceof ReticleCursor ) setCursor( reticle );
	}

	private CoordinateStatus getCoordinateStatus() {
		return coordinates;
	}

	private void key( KeyEvent event ) {
		getCommandPrompt().relay( event );
	}

	private void mouseMove( MouseEvent event ) {
		mousePoint = mouseToWorld( event.getX(), event.getY(), event.getZ() );
		getCoordinateStatus().updatePosition( mousePoint.getX(), mousePoint.getY(), mousePoint.getZ() );
	}

	private void mousePress( MouseEvent event ) {
		// Drag anchor is used by select, pan (and others)
		if( isPanMouseEvent( event ) ) {
			viewAnchor = designPane.getViewPoint();
			dragAnchor = new Point3D( event.getX(), event.getY(), 0 );
		} else if( isSelectMode() ) {
			// A click with no modifier replaces the selection
			// A click with a CTRL modifier adds/removes to/from the selection
			selectWindow.resizeRelocate( 0, 0, 0, 0 );
			boolean selected = mouseSelect( event.getX(), event.getY(), event.getZ(), isSelectModifyEvent( event ) );
			// TODO If nothing is selected this could be the start of a select window
			if( !selected ) dragAnchor = new Point3D( event.getX(), event.getY(), 0 );
		} else {
			getCommandPrompt().relay( mouseToWorld( event.getX(), event.getY(), event.getZ() ) );
		}
	}

	private void mouseDrag( MouseEvent event ) {
		if( isPanMouseEvent( event ) ) designPane.mousePan( viewAnchor, dragAnchor, event.getX(), event.getY() );
		if( isSelectMode() ) updateSelectWindow( dragAnchor, new Point3D( event.getX(), event.getY(), event.getZ() ) );
	}

	private void updateSelectWindow( Point3D a, Point3D b ) {
		double x = Math.min( a.getX(), b.getX() );
		double y = Math.min( a.getY(), b.getY() );
		double w = Math.abs( a.getX() - b.getX() );
		double h = Math.abs( a.getY() - b.getY() );
		Fx.run( () -> selectWindow.resizeRelocate( x, y, w, h ) );
	}

	private void mouseRelease( MouseEvent event ) {
		Point3D mouse = new Point3D( event.getX(), event.getY(), event.getZ() );
		if( isSelectMode() && selectWindow.getWidth() > 0 && selectWindow.getHeight() > 0 ) {
			windowSelect( dragAnchor, mouse, !event.isControlDown() );
		}
	}

	private void zoom( ScrollEvent event ) {
		if( Math.abs( event.getDeltaY() ) != 0.0 ) designPane.mouseZoom( event.getX(), event.getY(), event.getDeltaY() > 0 );
	}

	private boolean mouseSelect( double x, double y, double z, boolean modify ) {
		if( !modify ) getDesign().clearSelected();

		List<Shape> selection = designPane.apertureSelect( x, y, z, selectApertureRadius, selectApertureUnit );
		if( selection.isEmpty() ) return false;

		CsaShape s = (CsaShape)selection.get( 0 ).getProperties().get( DesignPane.SHAPE_META_DATA );
		s.setSelected( !modify || !s.isSelected() );
		return true;
	}

	private boolean windowSelect( Point3D a, Point3D b, boolean contains ) {
		// a might be null

		selectWindow.resizeRelocate( 0, 0, 0, 0 );

		List<Shape> selection = designPane.windowSelect( a, b, contains );
		if( selection.isEmpty() ) return false;

		selection.stream().map( s -> (CsaShape)s.getProperties().get( DesignPane.SHAPE_META_DATA ) ).forEach( s -> s.setSelected( true ) );
		return true;
	}

	private boolean isSelectModifyEvent( MouseEvent event ) {
		return event.isControlDown() && event.isPrimaryButtonDown();
	}

	private boolean isPanMouseEvent( MouseEvent event ) {
		return event.isShiftDown() && event.isPrimaryButtonDown();
	}

	private boolean isSelectMode() {
		return getDesign().getCommandProcessor().isSelecting();
	}

}
