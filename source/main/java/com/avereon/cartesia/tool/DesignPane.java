package com.avereon.cartesia.tool;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.tool.view.DesignLayerView;
import com.avereon.cartesia.tool.view.DesignShapeView;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventType;
import com.avereon.util.Log;
import com.avereon.zerra.color.Colors;
import com.avereon.zerra.javafx.Fx;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.transform.Transform;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DesignPane extends StackPane {

	private static final System.Logger log = Log.get();

	/**
	 * The default zoom magnification reached by applying zoom in or out the {@link #DEFAULT_ZOOM_STEPS} times.
	 */
	private static final double DEFAULT_ZOOM_MAGNIFICATION = 2.0;

	/**
	 * The number of steps required to reach the {@link #DEFAULT_ZOOM_MAGNIFICATION}.
	 */
	private static final int DEFAULT_ZOOM_STEPS = 4;

	/**
	 * This factor is applied to the zoom when zooming in or out. It is generated by calculating a factor that will increase the zoom by a specific magnification in a specific number of steps.
	 */
	public static final double ZOOM_IN_FACTOR = Math.pow( DEFAULT_ZOOM_MAGNIFICATION, 1.0 / DEFAULT_ZOOM_STEPS );

	public static final double ZOOM_OUT_FACTOR = 1.0 / ZOOM_IN_FACTOR;

	static final double DEFAULT_ZOOM = 1;

	static final double DEFAULT_DPI = 96;

	static final Point3D DEFAULT_PAN = new Point3D( 0, 0, 0 );

	static final double DEFAULT_ROTATE = 0;

	// FIXME This should probably be moved to the design settings
	public static final Color DEFAULT_SELECT_DRAW_PAINT = Colors.parse( "#ff00c0ff" );

	// FIXME This should probably be moved to the design settings
	static final Color DEFAULT_SELECT_FILL_PAINT = Colors.parse( "#ff00c040" );

	private final Pane select;

	private final Pane reference;

	private final DesignPaneLayer layers;

	private final Pane grid;

	private final Map<DesignLayer, DesignLayerView> layerMap;

	private final Map<DesignShape, DesignShapeView> geometryMap;

	private final Map<EventType<NodeEvent>, Map<Class<?>, Consumer<Object>>> designActions;

	private final ObservableSet<DesignLayer> visibleLayers;

	private Design design;

	private DoubleProperty dpiProperty;

	private DoubleProperty zoomProperty;

	private ObjectProperty<Point3D> viewPointProperty;

	private DoubleProperty viewRotateProperty;

	private ObjectProperty<Paint> selectDrawPaint;

	private ObjectProperty<Paint> selectFillPaint;

	private double dpu;

	private Transform rotate;

	public DesignPane() {
		select = new Pane();
		reference = new Pane();
		layers = new DesignPaneLayer();
		grid = new Pane();
		getChildren().addAll( grid, layers, reference, select );

		addOriginReferencePoint();
		setManaged( false );

		layerMap = new ConcurrentHashMap<>();
		geometryMap = new ConcurrentHashMap<>();
		visibleLayers = FXCollections.observableSet();

		// Internal listeners
		dpiProperty().addListener( ( p, o, n ) -> updateView() );
		viewPointProperty().addListener( ( p, o, n ) -> updateView() );
		viewRotateProperty().addListener( ( p, o, n ) -> updateView() );
		zoomProperty().addListener( ( p, o, n ) -> updateView() );
		parentProperty().addListener( ( p, o, n ) -> updateView() );

		// The design action map
		designActions = new HashMap<>();
		setupDesignActions();
	}

	private void setupDesignActions() {
		Map<Class<?>, Consumer<Object>> addActions = designActions.computeIfAbsent( NodeEvent.CHILD_ADDED, ( k ) -> new HashMap<>() );
		addActions.put( DesignLayer.class, ( o ) -> doAddLayer( (DesignLayer)o ) );
		addActions.put( DesignMarker.class, ( o ) -> doAddShape( (DesignShape)o ) );
		addActions.put( DesignLine.class, ( o ) -> doAddShape( (DesignShape)o ) );
		addActions.put( DesignEllipse.class, ( o ) -> doAddShape( (DesignShape)o ) );
		addActions.put( DesignArc.class, ( o ) -> doAddShape( (DesignShape)o ) );
		addActions.put( DesignCurve.class, ( o ) -> doAddShape( (DesignShape)o ) );

		Map<Class<?>, Consumer<Object>> removeActions = designActions.computeIfAbsent( NodeEvent.CHILD_REMOVED, ( k ) -> new HashMap<>() );
		removeActions.put( DesignLayer.class, ( o ) -> doRemoveLayer( (DesignLayer)o ) );
		removeActions.put( DesignMarker.class, ( o ) -> doRemoveShape( (DesignShape)o ) );
		removeActions.put( DesignLine.class, ( o ) -> doRemoveShape( (DesignShape)o ) );
		removeActions.put( DesignEllipse.class, ( o ) -> doRemoveShape( (DesignShape)o ) );
		removeActions.put( DesignArc.class, ( o ) -> doRemoveShape( (DesignShape)o ) );
		removeActions.put( DesignCurve.class, ( o ) -> doRemoveShape( (DesignShape)o ) );
	}

	DesignPaneLayer getLayerPane() {
		return layers;
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
	 * Set the pane view point. The point is the world point that should be in the center of the view.
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

	public final double getViewRotate() {
		return viewRotateProperty == null ? DEFAULT_ROTATE : viewRotateProperty.get();
	}

	public final void setViewRotate( double rotate ) {
		viewRotateProperty().set( rotate );
	}

	public DoubleProperty viewRotateProperty() {
		if( viewRotateProperty == null ) viewRotateProperty = new SimpleDoubleProperty( DEFAULT_ROTATE );
		return viewRotateProperty;
	}

	public final double getZoom() {
		return (zoomProperty == null) ? DEFAULT_ZOOM : zoomProperty.get();
	}

	public final void setZoom( double value ) {
		zoomProperty().set( value );
	}

	/**
	 * Defines the factor by which coordinates are zoomed about the center of the {@code Design}. This is used to zoom the design either manually or by using animation.
	 *
	 * @return the zoom for this {@code Design}
	 */
	public final DoubleProperty zoomProperty() {
		if( zoomProperty == null ) zoomProperty = new SimpleDoubleProperty( DEFAULT_ZOOM );
		return zoomProperty;
	}

	public void setView( Point3D center, double zoom ) {
		setView( center, zoom, getViewRotate() );
	}

	public void setView( Point3D center, double zoom, double rotate ) {
		setViewPoint( center );
		setViewRotate( rotate );
		setZoom( zoom );
	}

	public Paint getSelectDrawPaint() {
		return selectDrawPaint == null ? DEFAULT_SELECT_DRAW_PAINT : selectDrawPaint.get();
	}

	public void setSelectDrawPaint( Paint value ) {
		selectDrawPaintProperty().set( value );
	}

	public final ObjectProperty<Paint> selectDrawPaintProperty() {
		if( selectDrawPaint == null ) selectDrawPaint = new SimpleObjectProperty<>( DEFAULT_SELECT_DRAW_PAINT );
		return selectDrawPaint;
	}

	public Paint getSelectFillPaint() {
		return selectFillPaint == null ? DEFAULT_SELECT_FILL_PAINT : selectFillPaint.get();
	}

	public void setSelectFillPaint( Paint value ) {
		selectFillPaintProperty().set( value );
	}

	public final ObjectProperty<Paint> selectFillPaintProperty() {
		if( selectFillPaint == null ) selectFillPaint = new SimpleObjectProperty<>( DEFAULT_SELECT_FILL_PAINT );
		return selectFillPaint;
	}

	public ObservableSet<DesignLayer> visibleLayersProperty() {
		return visibleLayers;
	}

	public boolean isLayerVisible( DesignLayer layer ) {
		DesignLayerView view = layerMap.get( layer );
		return view != null && view.isVisible();
	}

	public void setLayerVisible( DesignLayer layer, boolean visible ) {
		Optional.ofNullable( layerMap.get( layer ) ).ifPresent( v -> Fx.run( () -> {
			v.setVisible( visible );
			if( visible ) {
				visibleLayers.add( layer );
			} else {
				visibleLayers.remove( layer );
			}
		} ) );
	}

	DesignPane setDesign( Design design ) {
		if( this.design != null ) throw new IllegalStateException( "Design already set" );
		this.design = Objects.requireNonNull( design );

		layerMap.put( design.getRootLayer(), new DesignLayerView( this, design.getRootLayer(), layers ) );

		design.getRootLayer().getAllLayers().forEach( this::doAddNode );

		updateView();

		// Design listeners
		design.register( Design.UNIT, e -> updateView() );
		design.register( NodeEvent.CHILD_ADDED, this::doChildAddedAction );
		design.register( NodeEvent.CHILD_REMOVED, this::doChildRemovedAction );

		return this;
	}

	public DesignPaneLayer getShapeLayer( DesignShape shape ) {
		return layerMap.get( shape.getLayer() ).getLayer();
	}

	Pane getReferenceLayer() {
		return reference;
	}

	DesignLayerView getDesignLayerView( DesignLayer layer ) {
		return layerMap.get( layer );
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
	 * @param viewAnchor The view point location before being dragged (world)
	 * @param dragAnchor The point where the mouse was pressed (screen)
	 * @param mouseX The mouse event X coordinate (screen)
	 * @param mouseY The mouse event Y coordinate (screen)
	 */
	void mousePan( Point3D viewAnchor, Point3D dragAnchor, double mouseX, double mouseY ) {
		Point3D anchor = localToParent( viewAnchor );
		Point3D delta = new Point3D( dragAnchor.getX() - mouseX, dragAnchor.getY() - mouseY, 0 );
		setViewPoint( parentToLocal( anchor.add( delta ) ) );
	}

	/**
	 * Change the zoom by the zoom factor. The zoom is centered on the provided anchor point in world coordinates. The current zoom is multiplied by the zoom factor.
	 *
	 * @param anchor The anchor point in world coordinates
	 * @param factor The zoom factor
	 */
	void zoom( Point3D anchor, double factor ) {
		Point3D offset = getViewPoint().subtract( anchor );

		// The zoom has to be set before the viewpoint
		setZoom( getZoom() * factor );
		setViewPoint( anchor.add( offset.multiply( 1 / factor ) ) );
	}

	List<Shape> screenPointSelect( Point3D point, DesignValue tolerance ) {
		double size = valueToWorld( tolerance );
		return worldPointSelect( parentToLocal( point ), new Point2D( size, size ) );
	}

	List<Shape> screenWindowSelect( Point3D a, Point3D b, boolean contains ) {
		return windowSelect( parentToLocal( a ), parentToLocal( b ), contains );
	}

	List<Shape> worldPointSelect( Point3D anchor, DesignValue v ) {
		return worldPointSelect( anchor, v.getValue() );
	}

	List<Shape> worldPointSelect( Point3D anchor, double radius ) {
		return worldPointSelect( anchor, new Point2D( radius, radius ) );
	}

	List<Shape> worldPointSelect( Point3D anchor, Point2D aperture ) {
		return doSelectByShape( new Ellipse( anchor.getX(), anchor.getY(), aperture.getX(), aperture.getY() ), false );
	}

	/**
	 * Find the nodes contained by, or intersecting, the window specified by points a and b.
	 *
	 * @param a One corner of the window
	 * @param b The other corner of the window
	 * @param contains True to select nodes contained in the window, false to select nodes intersecting the window
	 * @return The set of selected nodes
	 */
	List<Shape> windowSelect( Point3D a, Point3D b, boolean contains ) {
		double x = Math.min( a.getX(), b.getX() );
		double y = Math.min( a.getY(), b.getY() );
		double w = Math.abs( a.getX() - b.getX() );
		double h = Math.abs( a.getY() - b.getY() );

		Rectangle box = new Rectangle( x, y, w, h );
		return doSelectByShape( box, contains );
	}

	public void addLayerGeometry( DesignLayerView view ) {
		DesignPaneLayer parent = getDesignLayerView( view.getDesignLayer().getLayer() ).getLayer();
		DesignPaneLayer layer = view.getLayer();
		parent.getChildren().add( layer );
		doReorderLayer( parent );
		layer.showingProperty().bind( layer.visibleProperty().and( parent.showingProperty() ) );
		fireEvent( new DesignLayerEvent( this, DesignLayerEvent.LAYER_ADDED, layer ) );
	}

	public void removeLayerGeometry( DesignLayerView view ) {
		DesignPaneLayer layer = view.getLayer();
		((DesignPaneLayer)layer.getParent()).getChildren().remove( layer );
		layer.showingProperty().unbind();
		fireEvent( new DesignLayerEvent( this, DesignLayerEvent.LAYER_REMOVED, layer ) );
	}

	public void addShapeGeometry( DesignShapeView view ) {
		DesignPaneLayer layer = getShapeLayer( view.getDesignShape() );
		Group group = view.getGroup();
		layer.getChildren().add( group );
	}

	public void removeShapeGeometry( DesignShapeView view ) {
		Group group = view.getGroup();
		DesignPaneLayer layer = (DesignPaneLayer)group.getParent();
		layer.getChildren().remove( group );
	}

	void setGrid( List<Shape> grid ) {
		this.grid.getChildren().clear();
		this.grid.getChildren().addAll( grid );
	}

	void setGridVisible( boolean visible ) {
		this.grid.setVisible( visible );
	}

	void updateView() {
		doUpdateDpu();
		doRotate();
		doRescale();
		doRecenter();
	}

	private void addOriginReferencePoint() {
		reference.getChildren().add( new ConstructionPoint( DesignMarkers.Type.REFERENCE ) );
	}

	private List<DesignPaneLayer> getLayers( DesignPaneLayer root ) {
		List<DesignPaneLayer> layers = new ArrayList<>();

		root.getChildren().stream().filter( c -> c instanceof DesignPaneLayer ).map( c -> (DesignPaneLayer)c ).forEach( l -> {
			layers.add( l );
			layers.addAll( getLayers( l ) );
		} );

		return layers;
	}

	private DesignUnit getDesignUnit() {
		return design == null ? Design.DEFAULT_DESIGN_UNIT : design.getDesignUnit();
	}

	private double valueToPixels( DesignValue v ) {
		return v.getUnit().to( v.getValue(), DesignUnit.INCH ) * getDpi();
	}

	private double valueToWorld( DesignValue v ) {
		return valueToPixels( v ) / getInternalScale();
	}

	private double getInternalScale() {
		return this.dpu * getZoom();
	}

	private void doUpdateDpu() {
		this.dpu = DesignUnit.INCH.from( getDpi(), getDesignUnit() );
	}

	private void doRecenter() {
		Parent parent = getParent();
		if( parent == null ) return;
		Point3D center = localToParent( getViewPoint() ).subtract( getTranslateX(), getTranslateY(), 0 );
		setTranslateX( parent.getLayoutBounds().getCenterX() - center.getX() );
		setTranslateY( parent.getLayoutBounds().getCenterY() - center.getY() );
		//validateGrid();
	}

	private void doRotate() {
		Point3D viewPoint = getViewPoint();
		getTransforms().remove( rotate );
		getTransforms().add( rotate = Transform.rotate( getViewRotate(), viewPoint.getX(), viewPoint.getY() ) );
	}

	private void doRescale() {
		double scale = getInternalScale();
		setScaleX( scale );
		setScaleY( -scale );
		reference.setScaleX( 1 / scale );
		reference.setScaleY( 1 / scale );
		//validateGrid();
	}

	private void doChildAddedAction( NodeEvent event ) {
		Object value = event.getNewValue();
		if( value == null ) return;
		Consumer<Object> c = designActions.get( event.getEventType() ).get( value.getClass() );
		if( c != null ) c.accept( value );
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
		doAddLayerShapes( yy );
		doReorderLayer( yy );
	}

	private void doAddLayerShapes( DesignLayer yy ) {
		yy.getShapes().forEach( this::doAddNode );
	}

	private void doRemoveLayer( DesignLayer yy ) {
		yy.getShapes().forEach( this::doRemoveShape );
		layerMap.computeIfPresent( yy, ( k, v ) -> {
			v.removeLayerGeometry();
			return null;
		} );
	}

	private void doReorderLayer( DesignLayer layer ) {
		doReorderLayer( layerMap.get( layer ).getLayer() );
	}

	private void doReorderLayer( DesignPaneLayer pane ) {
		pane.getChildren().setAll( pane.getChildren().sorted( new LayerSorter() ) );
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
	 * Select nodes using a shape. The selecting shape can be any shape but it usually a {@link Circle} or a {@link Rectangle}. Returns the list of selected shapes in order from top to bottom.
	 *
	 * @param selector The selecting shape
	 * @param contains True to require selected shapes be contained by the selecting shape
	 * @return The list of selected shapes
	 */
	private List<Shape> doSelectByShape( Shape selector, boolean contains ) {
		// The shape must have a fill but no stroke. The selector color is not
		// important since the selector shape is not shown, is just needs to be set.
		selector.setFill( Color.RED );
		selector.setStrokeWidth( 0.0 );
		selector.setStroke( null );

		// check for contains or intersecting
		List<Shape> shapes = getVisibleShapes();
		try {
			select.getChildren().add( selector );

			Stream<Shape> selectStream = shapes.stream().filter( s -> isIntersecting( selector, s ) );
			if( contains ) selectStream = shapes.stream().filter( s -> isContained( selector, s ) );

			// This list is in design order
			List<Shape> selected = selectStream.filter( s -> !DesignShapeView.getDesignData( s ).isReference() ).collect( Collectors.toList() );
			Collections.reverse( selected );
			return selected;
		} finally {
			select.getChildren().remove( selector );
		}
	}

	List<DesignPaneLayer> getLayers() {
		return getLayers( layers );
	}

	List<DesignPaneLayer> getVisibleLayers() {
		return getLayers( layers ).stream().filter( Node::isVisible ).collect( Collectors.toList() );
	}

	List<Shape> getVisibleShapes() {
		return getLayers( layers )
			.stream()
			.filter( Node::isVisible )
			.flatMap( l -> l.getChildren().stream() )
			.filter( n -> n instanceof Group )
			.flatMap( g -> ((Group)g).getChildren().stream() )
			.filter( n -> n instanceof Shape )
			.map( n -> (Shape)n )
			.collect( Collectors.toList() );
	}

	private boolean isContained( Shape selector, Shape shape ) {
		// This first test is an optimization to determine if the the accurate test needs to be used
		if( !selector.getBoundsInParent().intersects( shape.getBoundsInParent() ) ) return false;
		// This is the slow but accurate test if the shape is contained
		return ((Path)Shape.subtract( shape, selector )).getElements().isEmpty();
	}

	private boolean isIntersecting( Shape selector, Shape shape ) {
		// This first test is an optimization to determine if the the accurate test needs to be used
		if( !selector.getBoundsInParent().intersects( shape.getBoundsInParent() ) ) return false;
		// This is the slow but accurate test if the shape is intersecting
		return !((Path)Shape.intersect( shape, selector )).getElements().isEmpty();
	}

	private static class LayerSorter implements Comparator<Node> {

		@Override
		public int compare( Node o1, Node o2 ) {
			DesignDrawable s1 = DesignShapeView.getDesignData( o1 );
			DesignDrawable s2 = DesignShapeView.getDesignData( o2 );
			return s2.getOrder() - s1.getOrder();
		}

	}

}
