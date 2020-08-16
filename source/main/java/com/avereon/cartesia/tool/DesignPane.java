package com.avereon.cartesia.tool;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.data.*;
import com.avereon.data.NodeEvent;
import com.avereon.util.Log;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

	static final Point3D DEFAULT_PAN = new Point3D( 1, 1, 0 );

	static final double DEFAULT_ZOOM = 1;

	private DoubleProperty dpiProperty;

	private ObjectProperty<Point3D> panProperty;

	private DoubleProperty zoomProperty;

	private final Design design;

	private double dpu;

	private final StackPane layers;

	private final Pane preview;

	private final Pane reference;

	private final Map<DesignLayer, Node> layerMap;

	private final Map<Class<?>, Consumer<Object>> addActions;

	private final Map<Class<?>, Consumer<Object>> changeActions;

	private final Map<Class<?>, Consumer<Object>> removeActions;

	public DesignPane( Design design ) {
		this.design = Objects.requireNonNull( design );
		layers = new StackPane();
		preview = new Pane();
		reference = new Pane();
		getChildren().addAll( layers, preview, reference );

		addOriginReferencePoint();

		setManaged( false );
		rescale( true );

		layerMap = new ConcurrentHashMap<>();

		// Internal listeners
		dpiProperty().addListener( ( p, o, n ) -> rescale( true ) );
		panProperty().addListener( ( p, o, n ) -> repan( n ) );
		zoomProperty().addListener( ( p, o, n ) -> rescale( false ) );

		// TODO Move this map somewhere else
		addActions = new HashMap<>();
		addActions.put( DesignLayer.class, ( o ) -> addLayer( (DesignLayer)o ) );
		addActions.put( CsaPoint.class, ( s ) -> addPoint( (CsaPoint)s ) );
		addActions.put( CsaLine.class, ( s ) -> addLine( (CsaLine)s ) );

		// TODO Move this map somewhere else
		changeActions = new HashMap<>();
		changeActions.put( DesignLayer.class, ( o ) -> {
			log.log( Log.WARN, "Layer changed --- still needs implementing" );
		} );

		// TODO Move this map somewhere else
		removeActions = new HashMap<>();
		//removeActions.put( CsaPoint.class, ( s ) -> removePoint( (CsaPoint)s ) );
		//removeActions.put( CsaLine.class, ( s ) -> removeLine( (CsaLine)s ) );

		// Design listeners
		design.register( Design.UNIT, e -> rescale( true ) );
		design.register( NodeEvent.CHILD_ADDED, this::addShape );
		design.register( NodeEvent.VALUE_CHANGED, this::valueChanged );
		design.register( NodeEvent.CHILD_REMOVED, this::removeShape );

		loadDesign( design );
	}

	private void loadDesign( Design design ) {
		design.getLayers().forEach( this::addLayer );
		design.getLayers().forEach( l -> l.getShapes().forEach( this::add ) );
		design.getLayers().forEach( this::reorderLayer );
	}

	private void add( DesignNode node ) {
		Consumer<Object> c = addActions.get( node.getClass() );
		if( c == null ) return;
		c.accept( node );
	}

	private void addShape( NodeEvent event ) {
		add( event.getNewValue() );
	}

	private void valueChanged( NodeEvent event ) {
		changeActions.computeIfPresent( event.getNode().getClass(), ( k, c ) -> {
			c.accept( event.getNewValue() );
			return c;
		} );
	}

	private void removeShape( NodeEvent event ) {
		removeActions.computeIfPresent( event.getOldValue().getClass(), ( k, c ) -> {
			c.accept( event.getNewValue() );
			return c;
		} );
	}

	private ConstructionPoint cp( DoubleProperty xProperty, DoubleProperty yProperty ) {
		ConstructionPoint cp = new ConstructionPoint();
		cp.layoutXProperty().bind( xProperty.multiply( scaleXProperty() ) );
		cp.layoutYProperty().bind( yProperty.multiply( scaleYProperty() ).negate() );
		return cp;
	}

	private void addLayer( DesignLayer yy ) {
		Pane layer = new Pane();
		layerMap.put( yy, layer );
		Platform.runLater( () -> {
			layers.getChildren().add( layer );
		} );
	}

	private void reorderLayer( DesignLayer layer ) {
		reorderLayer( (Pane)layerMap.get( layer ) );
	}

	private void reorderLayer( Pane pane ) {
		Platform.runLater( () -> pane.getChildren().setAll( pane.getChildren().sorted( new LayerSorter() ) ) );
	}

	private void addPoint( CsaPoint pp ) {
		// FIXME Should the CsaShape class be used to generate the geometry???
		// TODO All this data may need to be encapsulated to be mapped to the original CsaPoint
		double size = 0.5;
		Line h = new Line( pp.getOrigin().getX() - size, pp.getOrigin().getY(), pp.getOrigin().getX() + size, pp.getOrigin().getY() );
		h.setStroke( pp.getDrawColor() );
		h.setStrokeWidth( 0.1 );
		Line v = new Line( pp.getOrigin().getX(), pp.getOrigin().getY() - size, pp.getOrigin().getX(), pp.getOrigin().getY() + size );
		v.setStroke( pp.getDrawColor() );
		v.setStrokeWidth( 0.1 );
		Group point = new Group( h, v );

		// TODO Generalize and simplify
		ConstructionPoint o = cp( v.startXProperty(), h.startYProperty() );
		point.getProperties().put( "data", pp );
		point.getProperties().put( "construction-points", Set.of( o ) );

		DesignLayer yy = pp.getParent();
		Pane layer = (Pane)layerMap.get( yy );
		Platform.runLater( () -> {
			layer.getChildren().add( point );
			reference.getChildren().addAll( o );
		} );
	}

	private void addLine( CsaLine ll ) {
		// FIXME Should the CsaShape class be used to generate the geometry???
		// TODO All this data may need to be encapsulated to be mapped to the original CsaLine
		Line line = new Line( ll.getOrigin().getX(), ll.getOrigin().getY(), ll.getPoint().getX(), ll.getPoint().getY() );
		line.setStroke( ll.getDrawColor() );

		// TODO Generalize and simplify
		ConstructionPoint o = cp( line.startXProperty(), line.startYProperty() );
		ConstructionPoint p = cp( line.endXProperty(), line.endYProperty() );
		line.getProperties().put( "data", ll );
		line.getProperties().put( "construction-points", Set.of( o, p ) );

		DesignLayer yy = ll.getParent();
		Pane layer = (Pane)layerMap.get( yy );
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

	public ObjectProperty<Point3D> panProperty() {
		if( panProperty == null ) panProperty = new SimpleObjectProperty<>( DEFAULT_PAN );
		return panProperty;
	}

	/**
	 * Pan the design pane
	 *
	 * @param point The world point to move to the center of the view
	 */
	public final void setPan( Point3D point ) {
		panProperty().set( point );
	}

	public final Point3D getPan() {
		return panProperty == null ? DEFAULT_PAN : panProperty.get();
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

	void panOffset( double x, double y ) {
		if( x != 0.0 ) setTranslateX( getTranslateX() + x );
		if( y != 0.0 ) setTranslateY( getTranslateY() + y );
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

	Transform mouseToWorld() {
		try {
			return getLocalToParentTransform().createInverse();
		} catch( NonInvertibleTransformException exception ) {
			log.log( Log.ERROR, "Unable to obtain parent to local transform" );
		}
		return new Affine();
	}

	Set<Node> apertureSelect( double x, double y, double z, double r, DesignUnit u ) {
		// Convert the aperture radius and unit to world values
		double pixels = u.to( r, DesignUnit.INCH ) * getDpi();
		Point2D aperture  = mouseToWorld().transform( getTranslateX() + pixels, getTranslateY() + pixels );
		log.log( Log.INFO, "a=" + aperture.getX() );
		return selectByAperture( mouseToWorld().transform( x, y, z ), aperture.getX() );
	}

	Set<Node> windowSelect( Point3D a, Point3D b, boolean crossing ) {
		return selectByWindow( mouseToWorld().transform( a ), mouseToWorld().transform( b ), crossing );
	}

	Set<Node> selectByAperture( Point3D anchor, double radius ) {
		return selectByShape( new Circle( anchor.getX(), anchor.getY(), radius ), true );
	}

	/**
	 * Find the nodes contained by, or intersecting, the window specified by
	 * points a and b.
	 *
	 * @param a One corner of the window
	 * @param b The other corner of the window
	 * @param crossing False to select nodes contained in the window, true to select nodes contained by or intersecting the window
	 * @return The set of selected nodes
	 */
	Set<Node> selectByWindow( Point3D a, Point3D b, boolean crossing ) {
		double x = Math.min( a.getX(), b.getX() );
		double y = Math.min( a.getY(), b.getY() );
		double w = Math.abs( a.getX() - b.getX() );
		double h = Math.abs( a.getY() - b.getY() );

		Rectangle box = new Rectangle( x, y, w, h );
		return selectByShape( box, crossing );
	}

	private Set<Node> selectByShape( Shape shape, boolean crossing ) {
		// The shape must have a fill but no stroke
		shape.setFill( Color.TRANSPARENT );
		shape.setStroke( null );

		Set<Node> visibleNodes = getVisibleNodes();

		// check for contains or intersecting
		try {
			preview.getChildren().add( shape );
			Bounds bounds = shape.getBoundsInLocal();
			if( crossing ) {
				return visibleNodes
					.stream()
					.filter( n -> bounds.intersects( n.getBoundsInLocal() ) )
					.filter( n -> n instanceof Shape )
					.filter( n -> !((Path)Shape.intersect( (Shape)n, shape )).getElements().isEmpty() )
					.collect( Collectors.toSet() );
			} else {
				return visibleNodes.stream().filter( n -> bounds.contains( n.getBoundsInLocal() ) ).collect( Collectors.toSet() );
			}
		} finally {
			preview.getChildren().remove( shape );
		}
	}

	private Set<Node> getVisibleNodes() {
		return layers.getChildren().stream().filter( Node::isVisible ).flatMap( l -> ((Pane)l).getChildren().stream() ).collect( Collectors.toSet() );
	}

	private void repan( Point3D point ) {
		Parent parent = getParent();
		if( parent == null ) return;

		Point3D p = getLocalToParentTransform().transform( point ).subtract( getTranslateX(), getTranslateY(), 0 );
		setTranslateX( parent.getLayoutBounds().getCenterX() - p.getX() );
		setTranslateY( parent.getLayoutBounds().getCenterY() - p.getY() );
	}

	private void rescale( boolean recalculateDpu ) {
		if( recalculateDpu ) this.dpu = DesignUnit.INCH.from( getDpi(), getDesignUnit() );
		double scale = this.dpu * getZoom();
		setScaleX( scale );
		setScaleY( -scale );
		reference.setScaleX( 1 / scale );
		reference.setScaleY( 1 / scale );
	}

	private static class LayerSorter implements Comparator<Node> {

		@Override
		public int compare( Node o1, Node o2 ) {
			CsaShape s1 = (CsaShape)o1.getProperties().get( "data" );
			CsaShape s2 = (CsaShape)o2.getProperties().get( "data" );
			return s2.getOrder() - s1.getOrder();
		}

	}

}
