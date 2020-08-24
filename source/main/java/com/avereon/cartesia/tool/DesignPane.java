package com.avereon.cartesia.tool;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.data.*;
import com.avereon.data.NodeEvent;
import com.avereon.util.Log;
import com.avereon.zerra.javafx.Fx;
import javafx.beans.property.*;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DesignPane extends StackPane {

	public static final String SHAPE_META_DATA = "shape-meta-data";

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

	static final double DEFAULT_ZOOM = 1;

	static final double DEFAULT_DPI = 96;

	static final Point3D DEFAULT_PAN = new Point3D( 0, 0, 0 );

	private DoubleProperty dpiProperty;

	private DoubleProperty zoomProperty;

	private ObjectProperty<Point3D> viewPointProperty;

	private final Design design;

	private double dpu;

	private final Pane select;

	private final Pane preview;

	private final Pane reference;

	private final Layer layers;

	private final Map<DesignLayer, Layer> layerMap;

	private final Map<Class<?>, Consumer<Object>> addActions;

	private final Map<Class<?>, Consumer<Object>> changeActions;

	private final Map<Class<?>, Consumer<Object>> removeActions;

	public DesignPane( Design design ) {
		this.design = Objects.requireNonNull( design );
		select = new Pane();
		reference = new Pane();
		preview = new Pane();
		layers = new Layer();
		getChildren().addAll( layers, preview, reference, select );

		addOriginReferencePoint();

		setManaged( false );
		rescale( true );

		layerMap = new ConcurrentHashMap<>();
		layerMap.put( design.getRootLayer(), layers );

		// Internal listeners
		dpiProperty().addListener( ( p, o, n ) -> rescale( true ) );
		viewPointProperty().addListener( ( p, o, n ) -> recenter() );
		zoomProperty().addListener( ( p, o, n ) -> rescale( false ) );
		parentProperty().addListener( ( p, o, n ) -> recenter() );

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
		design.getRootLayer().getLayers().forEach( this::addLayer );
		design.getRootLayer().getLayers().forEach( l -> l.getShapes().forEach( this::add ) );
		design.getRootLayer().getLayers().forEach( this::reorderLayer );
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

	private ConstructionPoint cp( ReadOnlyObjectProperty<Bounds> bProperty ) {
		ConstructionPoint cp = new ConstructionPoint();
		bProperty.addListener( ( p, o, n ) -> {
			cp.layoutXProperty().set( bProperty.get().getCenterX() );
			cp.layoutYProperty().set( bProperty.get().getCenterY() );
		} );
		return cp;
	}

	private ConstructionPoint cp( DoubleProperty xProperty, DoubleProperty yProperty ) {
		ConstructionPoint cp = new ConstructionPoint();
		cp.layoutXProperty().bind( xProperty.multiply( scaleXProperty() ) );
		cp.layoutYProperty().bind( yProperty.multiply( scaleYProperty() ).negate() );
		return cp;
	}

	private void addLayer( DesignLayer yy ) {
		Layer parent = layerMap.get( yy.getParent() );
		Layer layer = layerMap.computeIfAbsent( yy, k -> new Layer() );
		Fx.run( () -> layers.getChildren().add( layer ) );
	}

	private void removeLayer( DesignLayer yy ) {
		Layer layer = layerMap.remove( yy );
		if( layer != null ) ((Layer)layer.getParent()).getChildren().remove( layer );
	}

	private void reorderLayer( DesignLayer layer ) {
		reorderLayer( layerMap.get( layer ) );
	}

	private void reorderLayer( Layer pane ) {
		Fx.run( () -> pane.getChildren().setAll( pane.getChildren().sorted( new LayerSorter() ) ) );
	}

	private void addPoint( CsaPoint pp ) {
		// FIXME Should the CsaShape class be used to generate the geometry???
		// TODO All this data may need to be encapsulated to be mapped to the original CsaPoint
		double size = 0.5;
		double offset = 0.1 * size;
		double ox = pp.getOrigin().getX();
		double oy = pp.getOrigin().getY();

		Path p = new Path();
		p.getElements().add( new MoveTo( ox - offset, oy + size ) );
		p.getElements().add( new LineTo( ox + offset, oy + size ) );
		p.getElements().add( new LineTo( ox + offset, oy + offset ) );
		p.getElements().add( new LineTo( ox + size, oy + offset ) );
		p.getElements().add( new LineTo( ox + size, oy - offset ) );
		p.getElements().add( new LineTo( ox + offset, oy - offset ) );
		p.getElements().add( new LineTo( ox + offset, oy - size ) );
		p.getElements().add( new LineTo( ox - offset, oy - size ) );
		p.getElements().add( new LineTo( ox - offset, oy - offset ) );
		p.getElements().add( new LineTo( ox - size, oy - offset ) );
		p.getElements().add( new LineTo( ox - size, oy + offset ) );
		p.getElements().add( new LineTo( ox - offset, oy + offset ) );
		p.getElements().add( new ClosePath() );
		p.setFill( pp.getDrawColor() );
		p.setStrokeWidth( 0 );
		p.setStroke( null );

		pp.register( CsaShape.SELECTED, e -> p.setFill( e.getNewValue() ? Color.MAGENTA : pp.getDrawColor() ) );

		// TODO Generalize and simplify
		ConstructionPoint o = cp( p.boundsInParentProperty() );
		p.getProperties().put( SHAPE_META_DATA, pp );
		p.getProperties().put( "construction-points", Set.of( o ) );

		DesignLayer yy = pp.getParent();
		Layer layer = layerMap.get( yy );
		Fx.run( () -> {
			layer.getChildren().add( p );
			reference.getChildren().addAll( o );
		} );
	}

	private void addLine( CsaLine ll ) {
		// FIXME Should the CsaShape class be used to generate the geometry???
		// TODO All this data may need to be encapsulated to be mapped to the original CsaLine
		Line line = new Line( ll.getOrigin().getX(), ll.getOrigin().getY(), ll.getPoint().getX(), ll.getPoint().getY() );
		line.setStroke( ll.getDrawColor() );
		line.setFill( null );

		ll.register( CsaShape.SELECTED, e -> line.setStroke( e.getNewValue() ? Color.MAGENTA : ll.getDrawColor() ) );

		// TODO Generalize and simplify
		ConstructionPoint o = cp( line.startXProperty(), line.startYProperty() );
		ConstructionPoint p = cp( line.endXProperty(), line.endYProperty() );
		line.getProperties().put( SHAPE_META_DATA, ll );
		line.getProperties().put( "construction-points", Set.of( o, p ) );

		DesignLayer yy = ll.getParent();
		Layer layer = layerMap.get( yy );
		Fx.run( () -> {
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

	public final double getDpi() {
		return (dpiProperty == null) ? DEFAULT_DPI : dpiProperty.get();
	}

	public final void setDpi( double value ) {
		dpiProperty().set( value );
	}

	public final DoubleProperty dpiProperty() {
		if( dpiProperty == null ) dpiProperty = new SimpleDoubleProperty( DEFAULT_DPI );
		return dpiProperty;
	}

	public final Point3D getViewPoint() {
		return viewPointProperty == null ? DEFAULT_PAN : viewPointProperty.get();
	}

	/**
	 * Set the pane view point. The point is the world point that should be in the
	 * center of the view.
	 *
	 * @param point The world point to move to the center of the view
	 */
	public final void setViewPoint( Point3D point ) {
		viewPointProperty().set( point );
	}

	public ObjectProperty<Point3D> viewPointProperty() {
		if( viewPointProperty == null ) viewPointProperty = new SimpleObjectProperty<>( DEFAULT_PAN );
		return viewPointProperty;
	}

	public final double getZoom() {
		return (zoomProperty == null) ? DEFAULT_ZOOM : zoomProperty.get();
	}

	public final void setZoom( double value ) {
		zoomProperty().set( value );
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

	private DesignUnit getDesignUnit() {
		return design.getDesignUnit();
	}

	/**
	 * Pan the viewpoint by an offset in world coordinates.
	 *
	 * @param x The world X offset
	 * @param y The world Y offset
	 */
	void pan( double x, double y ) {
		setViewPoint( getViewPoint().add( x, y, 0 ) );
	}

	/**
	 * Pan the design pane.
	 *
	 * @param viewAnchor The view point location before being dragged
	 * @param dragAnchor The point where the mouse was pressed
	 * @param mouseX The mouse event X coordinate
	 * @param mouseY The mouse event Y coordinate
	 */
	void mousePan( Point3D viewAnchor, Point3D dragAnchor, double mouseX, double mouseY ) {
		double x = (dragAnchor.getX() - mouseX) / getScaleX();
		double y = (dragAnchor.getY() - mouseY) / getScaleY();
		setViewPoint( viewAnchor.add( x, y, 0 ) );
	}

	/**
	 * Zoom the design pane. Zoom in (scroll up) increases the scale. Zoom out
	 * (scroll down) decreases the scale.
	 *
	 * @param mouseX The anchor point X coordinate
	 * @param mouseY The anchor point Y coordinate
	 * @param zoomIn True to zoom in, false to zoom out
	 */
	void mouseZoom( double mouseX, double mouseY, boolean zoomIn ) {
		Point3D anchor = mouseToWorld( mouseX, mouseY, 0 );
		Point3D offset = getViewPoint().subtract( anchor );

		double zoomFactor = zoomIn ? ZOOM_IN_FACTOR : ZOOM_OUT_FACTOR;
		setZoom( getZoom() * zoomFactor );

		setViewPoint( anchor.add( offset.multiply( 1 / zoomFactor ) ) );
	}

	Point3D mouseToWorld( double x, double y, double z ) {
		return parentToLocal( x, y, z );
	}

	Point3D worldToMouse( double x, double y, double z ) {
		return localToParent( x, y, z );
	}

	List<Shape> apertureSelect( double x, double y, double z, double r, DesignUnit u ) {
		// Convert the aperture radius and unit to world values
		double pixels = u.to( r, DesignUnit.INCH ) * getDpi();
		Point2D aperture = parentToLocal( getTranslateX() + pixels, getTranslateY() + pixels );
		return selectByAperture( parentToLocal( x, y, z ), aperture.getX() );
	}

	List<Shape> windowSelect( Point3D a, Point3D b, boolean contains ) {
		return selectByWindow( parentToLocal( a ), parentToLocal( b ), contains );
	}

	List<Shape> selectByAperture( Point3D anchor, double radius ) {
		log.log( Log.INFO, "a.radius=" + radius );
		return selectByShape( new Circle( anchor.getX(), anchor.getY(), radius ), false );
	}

	/**
	 * Find the nodes contained by, or intersecting, the window specified by
	 * points a and b.
	 *
	 * @param a One corner of the window
	 * @param b The other corner of the window
	 * @param contains True to select nodes contained in the window, false to select nodes intersecting the window
	 * @return The set of selected nodes
	 */
	List<Shape> selectByWindow( Point3D a, Point3D b, boolean contains ) {
		double x = Math.min( a.getX(), b.getX() );
		double y = Math.min( a.getY(), b.getY() );
		double w = Math.abs( a.getX() - b.getX() );
		double h = Math.abs( a.getY() - b.getY() );

		Rectangle box = new Rectangle( x, y, w, h );
		return selectByShape( box, contains );
	}

	/**
	 * Select nodes using a shape. The selecting shape can be any shape but it
	 * usually a {@link Circle} or a {@link Rectangle}. Returns the list of
	 * selected shapes in order from top to bottom.
	 *
	 * @param selector The selecting shape
	 * @param contains True to require selected shapes be contained by the selecting shape
	 * @return The list of selected shapes
	 */
	private List<Shape> selectByShape( Shape selector, boolean contains ) {
		// The shape must have a fill but no stroke
		selector.setFill( Color.web( "0xff00ff80" ) );
		//selector.setFill( Color.TRANSPARENT );
		selector.setStrokeWidth( 0.0 );
		selector.setStroke( null );

		List<Shape> shapes = getVisibleShapes();
		Collections.reverse( shapes );

		// check for contains or intersecting
		try {
			select.getChildren().add( selector );
			if( contains ) {
				return shapes.stream().filter( s -> isContained( selector, s ) ).collect( Collectors.toList() );
			} else {
				return shapes.stream().filter( s -> isIntersecting( selector, s ) ).collect( Collectors.toList() );
			}
		} finally {
			select.getChildren().remove( selector );
		}
	}

	private List<Shape> getVisibleShapes() {
		return getLayers( layers )
			.stream()
			.filter( Node::isVisible )
			.flatMap( l -> l.getChildren().stream() )
			.filter( n -> n instanceof Shape )
			.map( n -> (Shape)n )
			.collect( Collectors.toList() );
	}

	private boolean isContained( Shape selector, Shape shape ) {
		return selector.getBoundsInLocal().contains( shape.getBoundsInLocal() );
	}

	private boolean isIntersecting( Shape selector, Shape shape ) {
		return selector.getBoundsInLocal().intersects( shape.getBoundsInLocal() ) && !((Path)Shape.intersect( selector, shape )).getElements().isEmpty();
	}

	private List<Layer> getLayers( Layer root ) {
		List<Layer> layers = new ArrayList<>();

		root.getChildren().stream().filter( c -> c instanceof Layer ).map( c -> (Layer)c ).forEach( l -> {
			layers.add( l );
			layers.addAll( getLayers( l ) );
		} );

		return layers;
	}

	void recenter() {
		Parent parent = getParent();
		Point3D center = localToParent( getViewPoint() ).subtract( getTranslateX(), getTranslateY(), 0 );
		setTranslateX( parent.getLayoutBounds().getCenterX() - center.getX() );
		setTranslateY( parent.getLayoutBounds().getCenterY() - center.getY() );
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
			CsaShape s1 = (CsaShape)o1.getProperties().get( SHAPE_META_DATA );
			CsaShape s2 = (CsaShape)o2.getProperties().get( SHAPE_META_DATA );
			return s2.getOrder() - s1.getOrder();
		}

	}

	// FIXME Change to 'extends Group'
	static class Layer extends Pane {}

}
