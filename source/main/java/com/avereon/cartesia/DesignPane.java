package com.avereon.cartesia;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class DesignPane extends Pane {

	static final double ZOOM_FACTOR = Math.pow( 2, 1.0 / 4.0 );

	static final double DEFAULT_ZOOM = 1;

	private DoubleProperty zoomProperty;

	public DesignPane() {
		getChildren().add( new Line( -10, -10, 10, 10 ) );
		getChildren().add( new Line( 10, -10, -10, 10 ) );

		// Initial values
		setScaleY( -1 );
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
		zoom( event.getDeltaY(), event.getX(), event.getY() );
	}

	void zoom( double scrollY, double anchorX, double anchorY ) {
		double dpc = 63.0;

		// NEXT The scale is the combination of the unit conversion and the zoom property
		double scaleX = dpc * anchorX;

		if( scrollY > 0 ) {
			double newZoom = getZoom() * ZOOM_FACTOR;

			// Zoom in (scroll up) [increase the scale]
			double dx = getTranslateX() - anchorX;
			double dy = getTranslateY() - anchorY;
			setTranslateX( anchorX + (dx * ZOOM_FACTOR) );
			setTranslateY( anchorY + (dy * ZOOM_FACTOR) );
			setScaleX( newZoom );
			setScaleY( -newZoom );
			setZoom( newZoom );
		} else if( scrollY < 0 ) {
			double newZoom = getZoom() / ZOOM_FACTOR;

			// Zoom out (scroll down) [decrease the scale]
			double dx = getTranslateX() - anchorX;
			double dy = getTranslateY() - anchorY;
			setTranslateX( anchorX + (dx / ZOOM_FACTOR) );
			setTranslateY( anchorY + (dy / ZOOM_FACTOR) );
			setScaleX( newZoom );
			setScaleY( -newZoom );
			setZoom( newZoom );
		}
	}

}
