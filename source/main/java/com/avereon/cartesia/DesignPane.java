package com.avereon.cartesia;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class DesignPane extends Pane {

	static final double ZOOM_FACTOR = Math.pow( 2, 1.0 / 4.0 );

	static final double DEFAULT_ZOOM_X = 1;

	static final double DEFAULT_ZOOM_Y = 1;

	static final double DEFAULT_ZOOM_Z = 1;

	private DoubleProperty zoomXProperty;

	private DoubleProperty zoomYProperty;

	private DoubleProperty zoomZProperty;

	//private Point3D dragAnchor;

	//private Point3D translateAnchor;

	public DesignPane() {
		getChildren().add( new Line( -10, -10, 10, 10 ) );
		getChildren().add( new Line( 10, -10, -10, 10 ) );

		// Initial values
		setScaleY( -1 );
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

	void pan( Point2D translateAnchor, Point2D dragAnchor, MouseEvent event ) {
		if( event.isPrimaryButtonDown() && event.isShiftDown() ) {
			setTranslateX( translateAnchor.getX() + ((event.getX() - dragAnchor.getX())) );
			setTranslateY( translateAnchor.getY() + ((event.getY() - dragAnchor.getY())) );
		}
	}

	void zoom( ScrollEvent event ) {
		zoom( event.getDeltaY(), event.getX(), event.getY(), event.getZ() );
	}

	void zoom( double scrollY, double anchorX, double anchorY, double anchorZ ) {
		double dpc = 63.0;

		// NEXT The scale is the combination of the unit conversion and the zoom property
		double scaleX = dpc * anchorX;

		if( scrollY > 0 ) {
			// Zoom in (scroll up) [increase the scale]
			setScaleX( getScaleX() * ZOOM_FACTOR );
			setScaleY( getScaleY() * ZOOM_FACTOR );
			double dx = getTranslateX() - anchorX;
			double dy = getTranslateY() - anchorY;
			setTranslateX( anchorX + (dx * ZOOM_FACTOR) );
			setTranslateY( anchorY + (dy * ZOOM_FACTOR) );
		} else if( scrollY < 0 ) {
			// Zoom out (scroll down) [decrease the scale]
			setScaleX( getScaleX() / ZOOM_FACTOR );
			setScaleY( getScaleY() / ZOOM_FACTOR );
			double dx = getTranslateX() - anchorX;
			double dy = getTranslateY() - anchorY;
			setTranslateX( anchorX + (dx / ZOOM_FACTOR) );
			setTranslateY( anchorY + (dy / ZOOM_FACTOR) );
		}
	}

}
