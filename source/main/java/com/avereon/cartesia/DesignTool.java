package com.avereon.cartesia;

import com.avereon.cartesia.cursor.StandardCursor;
import com.avereon.util.Log;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.xenon.workspace.Workspace;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public abstract class DesignTool extends ProgramTool {

	private static final System.Logger log = Log.get();

	static final double ZOOM_FACTOR = Math.pow( 2, 1.0 / 4.0 );

	private static final double DEFAULT_ZOOM_X = 1;

	private static final double DEFAULT_ZOOM_Y = 1;

	private static final double DEFAULT_ZOOM_Z = 1;

	private final CommandPrompt prompt;

	private final CoordinateStatus coordinates;

	// TODO Might need to move all these properties to a DesignPane class for easier testing
	private final Pane geometry;

	private DoubleProperty zoomXProperty;

	private DoubleProperty zoomYProperty;

	private DoubleProperty zoomZProperty;

	private Point3D dragAnchor;

	private Point3D translateAnchor;

	public DesignTool( ProgramProduct product, Asset asset ) {
		super( product, asset );

		addStylesheet( CartesiaMod.STYLESHEET );

		this.prompt = new CommandPrompt( this );
		this.coordinates = new CoordinateStatus( this );

		// This pane will "steal" the key events
		geometry = new Pane();
		geometry.setManaged( false );
		getChildren().add( geometry );

		geometry.getChildren().add( new Line( -10, -10, 10, 10 ) );
		geometry.getChildren().add( new Line( 10, -10, -10, 10 ) );

		// Initial values
		geometry.setScaleY( -1 );

		// Initial values from settings
		setCursor( StandardCursor.valueOf( product.getSettings().get( "reticle", StandardCursor.DUPLEX.name() ).toUpperCase() ) );

		// Settings listeners
		product.getSettings().register( "reticle", e -> setCursor( StandardCursor.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );

		addEventFilter( KeyEvent.ANY, getCommandPrompt()::update );
		addEventFilter( MouseEvent.MOUSE_MOVED, getCoordinateStatus()::update );
		addEventFilter( MouseEvent.MOUSE_PRESSED, e -> {
			dragAnchor = new Point3D( e.getX(), e.getY(), e.getZ() );
			translateAnchor = new Point3D( geometry.getTranslateX(), geometry.getTranslateY(), 0.0 );
		} );
		addEventFilter( MouseEvent.MOUSE_DRAGGED, this::translate );
		addEventFilter( ScrollEvent.SCROLL, this::zoom );
	}

	public DesignUnit getDesignUnit() {
		return DesignUnit.CENTIMETER;
	}

	/**
	 * Defines the factor by which coordinates are zoomed about the center of the
	 * design along the X axis of this {@code Design}. This is used to zoom the
	 * design either manually or by using an animation.
	 *
	 * @return the zoomX for this {@code Design}
	 * @defaultValue 1.0
	 */
	public final DoubleProperty zoomXProperty() {
		if( zoomXProperty == null ) zoomXProperty = new SimpleDoubleProperty();
		return zoomXProperty;
	}

	public final void setZoomX( double value ) {
		zoomXProperty().set( value );
	}

	public final double getZoomX() {
		return (zoomXProperty == null) ? DEFAULT_ZOOM_X : zoomXProperty.get();
	}

	/**
	 * Defines the factor by which coordinates are zoomed about the center of the
	 * design along the Y axis of this {@code Design}. This is used to zoom the
	 * design either manually or by using an animation.
	 *
	 * @return the zoomY for this {@code Design}
	 * @defaultValue 1.0
	 */
	public final DoubleProperty zoomYProperty() {
		if( zoomYProperty == null ) zoomYProperty = new SimpleDoubleProperty();
		return zoomYProperty;
	}

	public final void setZoomY( double value ) {
		zoomYProperty().set( value );
	}

	public final double getZoomY() {
		return (zoomYProperty == null) ? DEFAULT_ZOOM_Y : zoomYProperty.get();
	}

	/**
	 * Defines the factor by which coordinates are zoomed about the center of the
	 * design along the Z axis of this {@code Design}. This is used to zoom the
	 * design either manually or by using an animation.
	 *
	 * @return the zoomZ for this {@code Design}
	 * @defaultValue 1.0
	 */
	public final DoubleProperty zoomZProperty() {
		if( zoomZProperty == null ) zoomZProperty = new SimpleDoubleProperty();
		return zoomZProperty;
	}

	public final void setZoomZ( double value ) {
		zoomZProperty().set( value );
	}

	public final double getZoomZ() {
		return (zoomZProperty == null) ? DEFAULT_ZOOM_Z : zoomZProperty.get();
	}

	protected abstract Point3D mouseToWorld( MouseEvent event );

	@Override
	protected void activate() throws ToolException {
		super.activate();
		Workspace workspace = getWorkspace();
		if( workspace != null ) {
			workspace.getStatusBar().addLeft( getCommandPrompt() );
			workspace.getStatusBar().addRight( getCoordinateStatus() );
		}
		getCommandPrompt().clear();
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

	private void setCursor( StandardCursor cursor ) {
		setCursor( cursor.get() );
	}

	private CommandPrompt getCommandPrompt() {
		return prompt;
	}

	private CoordinateStatus getCoordinateStatus() {
		return coordinates;
	}

	private void translate( MouseEvent event ) {
		if( event.isPrimaryButtonDown() && event.isShiftDown() ) {
			geometry.setTranslateX( translateAnchor.getX() + ((event.getX() - dragAnchor.getX())) );
			geometry.setTranslateY( translateAnchor.getY() + ((event.getY() - dragAnchor.getY())) );
		}
	}

	private void zoom( ScrollEvent event ) {
		zoom( geometry, event.getDeltaY(), event.getX(), event.getY(), event.getZ() );
		getCoordinateStatus().updateZoom( getZoomX(), getZoomY(), getZoomZ() );
	}

	static void zoom( Node geometry, double scrollY, double anchorX, double anchorY, double anchorZ ) {
		double dpc = 63.0;

		// NEXT The scale is the combination of the unit conversion and the zoom property
		double scaleX = dpc * anchorX;

		if( scrollY > 0 ) {
			// Zoom in (scroll up) [increase the scale]
			geometry.setScaleX( geometry.getScaleX() * ZOOM_FACTOR );
			geometry.setScaleY( geometry.getScaleY() * ZOOM_FACTOR );
			double dx = geometry.getTranslateX() - anchorX;
			double dy = geometry.getTranslateY() - anchorY;
			geometry.setTranslateX( anchorX + (dx * ZOOM_FACTOR) );
			geometry.setTranslateY( anchorY + (dy * ZOOM_FACTOR) );
		} else if( scrollY < 0 ) {
			// Zoom out (scroll down) [decrease the scale]
			geometry.setScaleX( geometry.getScaleX() / ZOOM_FACTOR );
			geometry.setScaleY( geometry.getScaleY() / ZOOM_FACTOR );
			double dx = geometry.getTranslateX() - anchorX;
			double dy = geometry.getTranslateY() - anchorY;
			geometry.setTranslateX( anchorX + (dx / ZOOM_FACTOR) );
			geometry.setTranslateY( anchorY + (dy / ZOOM_FACTOR) );
		}
	}

}
