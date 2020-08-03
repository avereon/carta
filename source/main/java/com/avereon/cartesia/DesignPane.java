package com.avereon.cartesia;

import com.avereon.cartesia.data.Design;
import com.avereon.util.Log;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;

import java.util.Objects;

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
	static final double ZOOM_IN_FACTOR = Math.pow( DEFAULT_ZOOM_MAGNIFICATION, 1.0 / DEFAULT_ZOOM_STEPS );

	static final double ZOOM_OUT_FACTOR = 1.0 / ZOOM_IN_FACTOR;

	static final double DEFAULT_DPI = 96;

	static final double DEFAULT_ZOOM = 1;

	private DoubleProperty dpiProperty;

	private DoubleProperty zoomProperty;

	private final Design design;

	private double dpu;

	public DesignPane( Design design ) {
		this.design = Objects.requireNonNull( design );

		setManaged( false );
		rescale( true );

		// Internal listeners
		dpiProperty().addListener( ( v, o, n ) -> rescale( true ) );
		zoomProperty().addListener( ( v, o, n ) -> rescale( false ) );

		// Design listeners
		design.register( Design.UNIT, e -> rescale( true ) );

		// TODO Remove
		generateTestData();
	}

	// TODO Remove this method that creates test data
	private void generateTestData() {
		double r = 1;
		double d = r * Math.sqrt( 0.5 );
		Group layer = new Group();
		layer.getChildren().add( new Line( -d, -d, d, d ) );
		layer.getChildren().add( new Line( d, -d, -d, d ) );
		layer.getChildren().add( new Line( -r, 0, r, 0 ) );
		layer.getChildren().add( new Line( 0, -r, 0, r ) );
		layer.getChildren().forEach( c -> {
			Shape s = (Shape)c;
			//s.setStyle( "-fx-stroke: green; -fx-stroke-width: 1mm" );
			s.setStroke( Color.GREY );
			s.setStrokeWidth( 1.0 / 10.0 );
			s.setStrokeLineCap( StrokeLineCap.BUTT );
		} );
		getChildren().add( layer );
	}

	public Design getDesign() {
		return design;
	}

	public final DoubleProperty dpiProperty() {
		if( dpiProperty == null ) dpiProperty = new SimpleDoubleProperty( DEFAULT_DPI );
		return dpiProperty;
	}

	public final void setDpi( double value ) {
		dpiProperty().set( value );
	}

	public final double getDpi() {
		return (dpiProperty == null) ? DEFAULT_DPI : dpiProperty.get();
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

	protected DesignUnit getDesignUnit() {
		return design.getDesignUnit();
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

	/**
	 * Zoom the design pane. Zoom in (scroll up) increases the scale. Zoom out
	 * (scroll down) decreases the scale.
	 *
	 * @param anchorX The anchor point X coordinate
	 * @param anchorY The anchor point Y coordinate
	 * @param zoomIn True to zoom in, false to zoom out
	 */
	void zoom( double anchorX, double anchorY, boolean zoomIn ) {
		double dx = getTranslateX() - anchorX;
		double dy = getTranslateY() - anchorY;
		double zoomFactor = zoomIn ? ZOOM_IN_FACTOR : ZOOM_OUT_FACTOR;
		setTranslateX( anchorX + (dx * zoomFactor) );
		setTranslateY( anchorY + (dy * zoomFactor) );
		setZoom( getZoom() * zoomFactor );
	}

	private void rescale( boolean recalculateDpu ) {
		if( recalculateDpu ) this.dpu = DesignUnit.INCH.from( getDpi(), getDesignUnit() );
		double scale = getDpu() * getZoom();
		setScaleX( scale );
		setScaleY( -scale );
	}

	private double getDpu() {
		return dpu;
	}

}
