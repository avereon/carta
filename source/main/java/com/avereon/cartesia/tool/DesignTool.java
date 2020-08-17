package com.avereon.cartesia.tool;

import com.avereon.cartesia.CartesiaMod;
import com.avereon.cartesia.DesignUnit;
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
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;

import java.util.List;

public abstract class DesignTool extends ProgramTool {

	public static final String RETICLE = "reticle";

	public static final String SELECT_APERTURE_RADIUS = "select-aperture-radius";

	public static final String SELECT_APERTURE_UNIT = "select-aperture-unit";

	private static final System.Logger log = Log.get();

	private final CommandPrompt prompt;

	private final CoordinateStatus coordinates;

	private ReticleCursor reticle;

	private DesignPane designPane;

	private Point3D mousePoint;

	private Point2D dragAnchor;

	private Point2D panAnchor;

	private double selectApertureRadius;

	private DesignUnit selectApertureUnit;

	public DesignTool( ProgramProduct product, Asset asset ) {
		super( product, asset );

		addStylesheet( CartesiaMod.STYLESHEET );

		this.prompt = new CommandPrompt( this );
		this.coordinates = new CoordinateStatus( this );

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
		return designPane == null ? Point3D.ZERO : designPane.getPan();
	}

	public void setPan( Point3D point ) {
		if( designPane != null ) designPane.setPan( point );
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
		getChildren().add( designPane );

		// Keep the design pane centered when resizing
		widthProperty().addListener( ( p, o, n ) -> designPane.panOffset( 0.5 * (n.doubleValue() - o.doubleValue()), 0 ) );
		heightProperty().addListener( ( p, o, n ) -> designPane.panOffset( 0, 0.5 * (n.doubleValue() - o.doubleValue()) ) );

		// Update the status when the zoom changes
		designPane.zoomProperty().addListener( ( p, o, n ) -> getCoordinateStatus().updateZoom( n.doubleValue() ) );

		// Set the stored tool pan
		designPane.setPan( Point3D.ZERO );
		// Set the stored tool zoom
		designPane.setZoom( 1 );
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
		return designPane == null ? Point3D.ZERO : designPane.mouseToWorld().transform( x, y, z );
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
			panAnchor = new Point2D( designPane.getTranslateX(), designPane.getTranslateY() );
			dragAnchor = new Point2D( event.getX(), event.getY() );
		} else if( isSelectMode() ) {
			// A click with no modifier replaces the selection
			// A click with a CTRL modifier adds/removes to/from the selection
			boolean selected = mouseSelect( event.getX(), event.getY(), event.getZ(), isSelectModifyEvent( event ) );
			// TODO If nothing is selected this could be the start of a select window
			if( !selected ) dragAnchor = new Point2D( event.getX(), event.getY() );
		} else {
			getCommandPrompt().relay( mouseToWorld( event.getX(), event.getY(), event.getZ() ) );
		}
	}

	private void mouseDrag( MouseEvent event ) {
		if( isPanMouseEvent( event ) ) designPane.pan( panAnchor, dragAnchor, event.getX(), event.getY() );
		// TODO if( isSelectMode() ) designPane.showSelectWindow( dragAnchor, new Point3D(event.getX(), event.getY(), event.getZ() ));
	}

	private void mouseRelease( MouseEvent event ) {
		// TODO if( isSelectMode() ) designPane.hideSelectWindow( dragAnchor, new Point3D(event.getX(), event.getY(), event.getZ() ));
	}

	private void zoom( ScrollEvent event ) {
		if( Math.abs( event.getDeltaY() ) != 0.0 ) designPane.zoom( event.getX(), event.getY(), event.getDeltaY() > 0 );
	}

	private boolean mouseSelect( double x, double y, double z, boolean modify ) {
		if( !modify ) getDesign().clearSelected();

		List<Shape> selection = designPane.apertureSelect( x, y, z, selectApertureRadius, selectApertureUnit );
		if( selection.isEmpty() ) return false;

		CsaShape s = (CsaShape)selection.get( 0 ).getProperties().get( DesignPane.SHAPE_META_DATA );
		s.setSelected( !modify || !s.isSelected() );
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
