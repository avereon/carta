package com.avereon.cartesia.tool;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.math.Points;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventType;
import com.avereon.util.Log;
import com.avereon.zerra.javafx.Fx;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

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

	private final Pane select;

	private final Pane preview;

	private final Pane reference;

	private final Layer layers;

	private final Map<DesignLayer, DesignLayerView> layerMap;

	private final Map<DesignShape, DesignShapeView> geometryMap;

	private final Map<EventType<NodeEvent>, Map<Class<?>, Consumer<Object>>> designActions;

	private Design design;

	private DoubleProperty dpiProperty;

	private DoubleProperty zoomProperty;

	private ObjectProperty<Point3D> viewPointProperty;

	private double dpu;

	public DesignPane() {
		select = new Pane();
		reference = new Pane();
		preview = new Pane();
		layers = new Layer();
		getChildren().addAll( layers, preview, reference, select );

		addOriginReferencePoint();
		setManaged( false );

		layerMap = new ConcurrentHashMap<>();
		geometryMap = new ConcurrentHashMap<>();

		// Internal listeners
		dpiProperty().addListener( ( p, o, n ) -> rescale( true ) );
		viewPointProperty().addListener( ( p, o, n ) -> recenter() );
		zoomProperty().addListener( ( p, o, n ) -> rescale( false ) );
		parentProperty().addListener( ( p, o, n ) -> recenter() );

		// The design action map
		setupDesignActions( designActions = new HashMap<>() );
	}

	private void addOriginReferencePoint() {
		reference.getChildren().add( new ConstructionPoint( Points.Type.REFERENCE ) );
	}

	private void setupDesignActions( Map<EventType<NodeEvent>, Map<Class<?>, Consumer<Object>>> designActions ) {
		Map<Class<?>, Consumer<Object>> addActions = designActions.computeIfAbsent( NodeEvent.CHILD_ADDED, ( k ) -> new HashMap<>() );
		addActions.put( DesignLayer.class, ( o ) -> doAddLayer( (DesignLayer)o ) );
		addActions.put( DesignPoint.class, ( o ) -> doAddShape( (DesignShape)o ) );
		addActions.put( DesignLine.class, ( o ) -> doAddShape( (DesignShape)o ) );

		Map<Class<?>, Consumer<Object>> removeActions = designActions.computeIfAbsent( NodeEvent.CHILD_REMOVED, ( k ) -> new HashMap<>() );
		removeActions.put( DesignLayer.class, ( o ) -> doRemoveLayer( (DesignLayer)o ) );
		removeActions.put( DesignPoint.class, ( o ) -> doRemoveShape( (DesignPoint)o ) );
		removeActions.put( DesignLine.class, ( o ) -> doRemoveShape( (DesignLine)o ) );
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

	public boolean isLayerVisible( DesignLayer layer ) {
		DesignLayerView view = layerMap.get( layer );
		return view != null && view.isVisble();
	}

	public void setLayerVisible( DesignLayer layer, boolean visible ) {
		Optional.ofNullable( layerMap.get( layer ) ).ifPresent( v -> Fx.run( () -> v.setVisible( visible ) ) );
	}

	DesignPane loadDesign( Design design ) {
		if( this.design != null ) throw new IllegalStateException( "Design already set" );
		this.design = Objects.requireNonNull( design );

		layerMap.put( design.getRootLayer(), new DesignLayerView( this, design.getRootLayer(), layers ) );

		design.getRootLayer().getAllLayers().forEach( this::doAddNode );
		design.getRootLayer().getAllLayers().forEach( l -> l.getShapes().forEach( this::doAddNode ) );
		design.getRootLayer().getAllLayers().forEach( this::doReorderLayer );

		rescale( true );

		// Design listeners
		design.register( Design.UNIT, e -> rescale( true ) );
		design.register( NodeEvent.CHILD_ADDED, this::doChildAddedAction );
		design.register( NodeEvent.VALUE_CHANGED, this::doValueChangedAction );
		design.register( NodeEvent.CHILD_REMOVED, this::doChildRemovedAction );

		return this;
	}

	Layer getShapeLayer( DesignShape shape ) {
		return layerMap.get( shape.getParentLayer() ).getLayer();
	}

	Pane getReferenceLayer() {
		return reference;
	}

	DesignLayerView getDesignLayerView( DesignLayer layer ) {
		return layerMap.get( layer );
	}

	void recenter() {
		Parent parent = getParent();
		Point3D center = localToParent( getViewPoint() ).subtract( getTranslateX(), getTranslateY(), 0 );
		setTranslateX( parent.getLayoutBounds().getCenterX() - center.getX() );
		setTranslateY( parent.getLayoutBounds().getCenterY() - center.getY() );
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

	List<Shape> apertureSelect( double x, double y, double z, DesignValue v ) {
		// Convert the aperture radius and unit to world values
		double pixels = v.getUnit().to( v.getValue(), DesignUnit.INCH ) * getDpi();
		Point2D aperture = parentToLocal( getTranslateX() + pixels, getTranslateY() + pixels );
		return selectByAperture( parentToLocal( x, y, z ), aperture.getX() );
	}

	List<Shape> windowSelect( Point3D a, Point3D b, boolean contains ) {
		return selectByWindow( parentToLocal( a ), parentToLocal( b ), contains );
	}

	List<Shape> selectByAperture( Point3D anchor, double radius ) {
		log.log( Log.TRACE, "a.radius=" + radius );
		return doSelectByShape( new Circle( anchor.getX(), anchor.getY(), radius ), false );
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
		return doSelectByShape( box, contains );
	}

	void addLayerGeometry( DesignLayerView view ) {
		Layer parent = getDesignLayerView( view.getDesignLayer().getParentLayer() ).getLayer();
		Fx.run( () -> {
			parent.getChildren().add( view.getLayer() );
			fireEvent( new DesignLayerEvent( this, DesignLayerEvent.LAYER_ADDED, view.getLayer() ) );
		} );
	}

	void removeLayerGeometry( DesignLayerView view ) {
		Layer parent = getDesignLayerView( view.getDesignLayer().getParentLayer() ).getLayer();
		Fx.run( () -> {
			parent.getChildren().remove( view.getLayer() );
			fireEvent( new DesignLayerEvent( this, DesignLayerEvent.LAYER_ADDED, view.getLayer() ) );
		} );
	}

	void addShapeGeometry( DesignShapeView view ) {
		Layer layer = getShapeLayer( view.getDesignShape() );
		List<Shape> shapes = new ArrayList<>( view.getGeometry() );
		List<ConstructionPoint> cps = new ArrayList<>( view.getConstructionPoints() );

		shapes.forEach( s -> s.visibleProperty().bind( layer.visibleProperty() ) );

		Fx.run( () -> {
			layer.getChildren().addAll( shapes );
			getReferenceLayer().getChildren().addAll( cps );
		} );
	}

	void removeShapeGeometry( DesignShapeView view ) {
		Layer layer = (Layer)view.getGeometry().get( 0 ).getParent();
		List<Shape> shapes = new ArrayList<>( view.getGeometry() );
		List<ConstructionPoint> cps = new ArrayList<>( view.getConstructionPoints() );

		shapes.forEach( s -> s.visibleProperty().unbind() );

		Fx.run( () -> {
			getReferenceLayer().getChildren().removeAll( cps );
			layer.getChildren().removeAll( shapes );
		} );
	}

	private List<Layer> getLayers( Layer root ) {
		List<Layer> layers = new ArrayList<>();

		root.getChildren().stream().filter( c -> c instanceof Layer ).map( c -> (Layer)c ).forEach( l -> {
			layers.add( l );
			layers.addAll( getLayers( l ) );
		} );

		return layers;
	}

	private DesignUnit getDesignUnit() {
		return design.getDesignUnit();
	}

	private void rescale( boolean recalculateDpu ) {
		if( recalculateDpu ) this.dpu = DesignUnit.INCH.from( getDpi(), getDesignUnit() );
		double scale = this.dpu * getZoom();
		setScaleX( scale );
		setScaleY( -scale );
		reference.setScaleX( 1 / scale );
		reference.setScaleY( 1 / scale );
	}

	private void doChildAddedAction( NodeEvent event ) {
		Object value = event.getNewValue();
		if( value == null ) return;
		Consumer<Object> c = designActions.get( event.getEventType() ).get( value.getClass() );
		if( c != null ) c.accept( value );
	}

	private void doValueChangedAction( NodeEvent event ) {
		//		com.avereon.data.Node node = event.getNode();
		//		Consumer<Object> c = designActions.get( event.getEventType() ).get( node.getClass() );
		//		if( c != null ) c.accept( node );
	}

	private void doChildRemovedAction( NodeEvent event ) {
		Object value = event.getOldValue();
		if( value == null ) return;
		Consumer<Object> c = designActions.get( event.getEventType() ).get( value.getClass() );
		if( c != null ) c.accept( value );
	}

	private void doAddNode( DesignNode node ) {
		Consumer<Object> c = designActions.get( NodeEvent.CHILD_ADDED ).get( node.getClass() );
		if( c != null ) c.accept( node );
	}

	private void doAddLayer( DesignLayer yy ) {
		layerMap.computeIfAbsent( yy, k -> {
			DesignLayerView view = new DesignLayerView( this, yy );
			view.addLayerGeometry();
			return view;
		} );
	}

	private void doRemoveLayer( DesignLayer yy ) {
		layerMap.computeIfPresent( yy, ( k, v ) -> {
			v.removeLayerGeometry();
			return null;
		} );
	}

	private void doReorderLayer( DesignLayer layer ) {
		doReorderLayer( layerMap.get( layer ).getLayer() );
	}

	private void doReorderLayer( Layer pane ) {
		Fx.run( () -> pane.getChildren().setAll( pane.getChildren().sorted( new LayerSorter() ) ) );
	}

	private void doAddShape( DesignShape shape ) {
		geometryMap.computeIfAbsent( shape, ( k ) -> {
			DesignShapeView view = DesignGeometry.from( this, shape );
			if( view != null ) view.addShapeGeometry();
			return view;
		} );
	}

	private void doRemoveShape( DesignShape shape ) {
		geometryMap.computeIfPresent( shape, ( k, v ) -> {
			v.removeShapeGeometry();
			return null;
		} );
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
	private List<Shape> doSelectByShape( Shape selector, boolean contains ) {
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

	private static class LayerSorter implements Comparator<Node> {

		@Override
		public int compare( Node o1, Node o2 ) {
			DesignDrawable s1 = (DesignDrawable)o1.getProperties().get( SHAPE_META_DATA );
			DesignDrawable s2 = (DesignDrawable)o2.getProperties().get( SHAPE_META_DATA );
			return s2.getOrder() - s1.getOrder();
		}

	}

	/**
	 * This is the internal layer that represents the design layer.
	 */
	public static class Layer extends Pane {}

}
