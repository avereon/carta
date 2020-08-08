package com.avereon.cartesia.tool;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.geometry.CsaLine;
import com.avereon.cartesia.geometry.CsaPoint;
import com.avereon.cartesia.geometry.CsaShape;
import com.avereon.data.Node;
import com.avereon.data.NodeEvent;
import com.avereon.util.Log;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class DesignPane extends StackPane {

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

	private Pane reference;

	private StackPane layers;

	public DesignPane( Design design ) {
		this.design = Objects.requireNonNull( design );
		reference = new Pane();
		layers = new StackPane();

		setManaged( false );
		rescale( true );

		layers.getChildren().add( new Pane() );
		getChildren().addAll( layers, reference );

		// Internal listeners
		dpiProperty().addListener( ( v, o, n ) -> rescale( true ) );
		zoomProperty().addListener( ( v, o, n ) -> rescale( false ) );

		Map<Class<? extends CsaShape>, Consumer<? extends CsaShape>> m = new HashMap<>();
		m.put( CsaPoint.class, ( s ) -> addPoint( (CsaPoint)s ) );
		m.put( CsaLine.class, ( s ) -> addLine( (CsaLine)s ) );

		// Design listeners
		design.register( Design.UNIT, e -> rescale( true ) );
		design.register( NodeEvent.CHILD_ADDED, e -> {
			Consumer<? extends CsaShape> c = m.get( e.getNewValue().getClass() );
			if( c != null ) c.accept( e.getNewValue() );
			log.log( Log.INFO, e.getNewValue().getClass().getSimpleName() + " added to " + ((Node)e.getNode()).getParent() );
		} );

		addOriginReferencePoint();
	}

	private ConstructionPoint cp( DoubleProperty xProperty, DoubleProperty yProperty ) {
		ConstructionPoint cp = new ConstructionPoint();
		cp.layoutXProperty().bind( xProperty.multiply( scaleXProperty() ) );
		cp.layoutYProperty().bind( yProperty.multiply( scaleYProperty() ).negate() );
		return cp;
	}

	private void addPoint( CsaPoint pp ) {
		Circle point = new Circle(pp.getOrigin().getX(), pp.getOrigin().getY(), 0.5 );
		ConstructionPoint o = cp( point.centerXProperty(), point.centerYProperty() );
		Pane layer = (Pane)layers.getChildren().get( 0 );
		Platform.runLater( () -> {
			layer.getChildren().add( point );
			reference.getChildren().addAll( o );
		} );
	}

	private void addLine( CsaLine ll ) {
		Line line = new Line( ll.getOrigin().getX(), ll.getOrigin().getY(), ll.getPoint().getX(), ll.getPoint().getY() );
		ConstructionPoint o = cp(line.startXProperty(),line.startYProperty());
		ConstructionPoint p = cp( line.endXProperty(),line.endYProperty());
		Pane layer = (Pane)layers.getChildren().get( 0 );
		Platform.runLater( () -> {
			layer.getChildren().add( line );
			reference.getChildren().addAll( o, p );
		} );
	}

	private void addOriginReferencePoint() {
		reference.getChildren().add( new ConstructionPoint( ConstructionPoint.Type.REFERENCE ) );
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
		reference.setScaleX( 1 / scale );
		reference.setScaleY( 1 / scale );
	}

	private double getDpu() {
		return dpu;
	}

}
