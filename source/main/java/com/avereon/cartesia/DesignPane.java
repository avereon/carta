package com.avereon.cartesia;

import com.avereon.util.Log;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;

public class DesignPane extends Group {

	private static final System.Logger log = Log.get();

	/**
	 * The default zoom magnification reached by applying zoom in or out the
	 * {@link #DEFAULT_ZOOM_STEPS} times.
	 */
	private static final double DEFAULT_ZOOM_MAGNIFICATION = 2.0;

	/**
	 * The number of steps required to reach the {@link #DEFAULT_ZOOM_MAGNIFICATION}.
	 */
	private static final int DEFAULT_ZOOM_STEPS = 4;

	/**
	 * This factor is applied to the zoom when zooming in or out. It is generated
	 * by calculating a factor that will increase the zoom by a specific
	 * magnification in a specific number of steps.
	 */
	static final double ZOOM_FACTOR = Math.pow( DEFAULT_ZOOM_MAGNIFICATION, 1.0 / DEFAULT_ZOOM_STEPS );

	static final double DEFAULT_ZOOM = 1;

	static final double DEFAULT_DPI = 96;

	private DoubleProperty zoomProperty;

	public DesignPane() {
		double r = 10;
		double d = r * Math.sqrt( 0.5 );
		Group layer = new Group();
		layer.getChildren().add( new Line( -d, -d, d, d ) );
		layer.getChildren().add( new Line( d, -d, -d, d ) );
		layer.getChildren().add( new Line( -r, 0, r, 0 ) );
		layer.getChildren().add( new Line( 0, -r, 0, r ) );
		layer.getChildren().forEach( c -> {
			Shape s = (Shape)c;
			s.setStyle( "-fx-stroke: green; -fx-stroke-width: 1mm" );
			//s.setStroke( Color.GREY );
			//s.setStrokeWidth( 1.0/60.0 );
		} );
		getChildren().add( layer );

		// Initial values
		setZoom( 1 );
	}

	/**
	 * Defines the factor by which coordinates are zoomed about the center of the
	 * {@code Design}. This is used to zoom the design either manually or by using
	 * animation.
	 *
	 * @return the zoom for this {@code Design}
	 * @defaultValue 1.0
	 */
	public final DoubleProperty zoomProperty() {
		if( zoomProperty == null ) zoomProperty = new SimpleDoubleProperty( DEFAULT_ZOOM );
		return zoomProperty;
	}

	public final void setZoom( double value ) {
		zoomProperty().set( value );

		// FIXME From here down need to move to a rescale() method that can be called on changes to dpi/scale/zoom.

		// This value comes from the layer/group
		DesignUnit unit = DesignUnit.CENTIMETER;
		// This value comes from the screen
		//double dpi = Screen.getPrimary().getDpi();
		double dpi = DEFAULT_DPI;

		if( getScene() != null ) {
			Screen s = Screen.getPrimary();
			//Window.getWindows().get( 0 ).getScene().getRoot().getD
			double r = getScene().getWindow().getRenderScaleX();
			double o = getScene().getWindow().getOutputScaleX();
			log.log( Log.WARN, "r={0} o={1} d={2}", r, o, s.getDpi() );
		}

		// NEXT The scale is the combination of the unit conversion and the zoom property
		double dpu = DesignUnit.INCH.from( dpi, unit );
		dpu = 1;
		double scaleX = dpu * value;
		double scaleY = dpu * value;

		setScaleX( scaleX );
		setScaleY( -scaleY );
	}

	public final double getZoom() {
		return (zoomProperty == null) ? DEFAULT_ZOOM : zoomProperty.get();
	}

	/**
	 * Pan the design pane.
	 *
	 * @param panAnchor The pane location before being dragged
	 * @param dragAnchor The point where the mouse was pressed
	 * @param mouseX The mouse event X coordinate
	 * @param mouseY The mouse event Y coordinate
	 */
	void pan( Point2D panAnchor, Point2D dragAnchor, double mouseX, double mouseY ) {
		setTranslateX( panAnchor.getX() + ((mouseX - dragAnchor.getX())) );
		setTranslateY( panAnchor.getY() + ((mouseY - dragAnchor.getY())) );
	}

	void zoom( ScrollEvent event ) {
		if( Math.abs( event.getDeltaY() ) != 0.0 ) zoom( event.getX(), event.getY(), event.getDeltaY() > 0 );
	}

	void zoom( double anchorX, double anchorY, boolean zoomIn ) {
		double dx = getTranslateX() - anchorX;
		double dy = getTranslateY() - anchorY;
		if( zoomIn ) {
			// Zoom in (scroll up) [increase the scale]
			setTranslateX( anchorX + (dx * ZOOM_FACTOR) );
			setTranslateY( anchorY + (dy * ZOOM_FACTOR) );
			setZoom( getZoom() * ZOOM_FACTOR );
		} else {
			// Zoom out (scroll down) [decrease the scale]
			setTranslateX( anchorX + (dx / ZOOM_FACTOR) );
			setTranslateY( anchorY + (dy / ZOOM_FACTOR) );
			setZoom( getZoom() / ZOOM_FACTOR );
		}
	}

}
