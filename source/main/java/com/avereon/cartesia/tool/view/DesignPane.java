package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventType;
import com.avereon.zerra.color.Colors;
import com.avereon.zerra.color.Paints;
import com.avereon.zerra.javafx.Fx;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
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
import lombok.CustomLog;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CustomLog
public class DesignPane extends StackPane {

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

	public static final double DEFAULT_ZOOM = 1;

	public static final double DEFAULT_DPI = 96;

	public static final Point3D DEFAULT_PAN = new Point3D( 0, 0, 0 );

	public static final double DEFAULT_ROTATE = 0;

	// FIXME This should probably be moved to the design settings
	public static final Color DEFAULT_SELECT_DRAW_PAINT = Colors.parse( "#ff00c0ff" );

	// FIXME This should probably be moved to the design settings
	static final Color DEFAULT_SELECT_FILL_PAINT = Colors.parse( "#ff00c040" );

	private static final DesignLayerPane NO_LAYER = new DesignLayerPane();

	private static final Comparator<Node> LAYER_SORTER = new LayerSorter();

	private static final Paint BARELY_VISIBLE = Paints.parse( "#80808001" );

	private final Pane select;

	private final Pane reference;

	private final DesignLayerPane layers;

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
		layers = new DesignLayerPane();
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
		setViewRotate( rotate );
		setZoom( zoom );
		setViewPoint( center );
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
		Fx.run( () -> Optional.ofNullable( layerMap.get( layer ) ).ifPresent( yy -> {
			yy.setVisible( visible );
			if( visible ) {
				visibleLayers.add( layer );
			} else {
				visibleLayers.remove( layer );
			}
		} ) );
	}

	public DesignPane setDesign( Design design ) {
		if( this.design != null ) throw new IllegalStateException( "Design already set" );
		this.design = Objects.requireNonNull( design );

		// Create the root layer view
		layerMap.put( design.getLayers(), new DesignLayerView( this, design.getLayers(), layers ) );

		design.getLayers().getAllLayers().forEach( this::doAddNode );

		updateView();

		// Design listeners
		design.register( Design.UNIT, e -> updateView() );
		design.register( NodeEvent.CHILD_ADDED, this::doChildAddedAction );
		design.register( NodeEvent.CHILD_REMOVED, this::doChildRemovedAction );

		return this;
	}

	public DesignLayerPane getShapeLayer( DesignShape shape ) {
		DesignLayer designLayer = shape.getLayer();
		if( designLayer == null ) log.atWarn().log( "Shape missing design layer, shape=%s", shape );

		DesignLayerView view = designLayer == null ? null : layerMap.get( designLayer );
		if( view == null ) log.atWarn().log( "Shape missing design view, shape=%s", shape );

		DesignLayerPane layer = view == null ? null : view.getLayerPane();
		if( layer == null ) log.atWarn().log( "Shape missing layer: shape=%s", shape );

		return layer == null ? NO_LAYER : layer;
	}

	Pane getReferenceLayer() {
		return reference;
	}

	public boolean isReferenceLayerVisible() {
		return getReferenceLayer().isVisible();
	}

	public void setReferenceLayerVisible( boolean visible ) {
		getReferenceLayer().setVisible( visible );
	}

	public BooleanProperty referenceLayerVisible() {
		return getReferenceLayer().visibleProperty();
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
	public void mousePan( Point3D viewAnchor, Point3D dragAnchor, double mouseX, double mouseY ) {
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
	public void zoom( Point3D anchor, double factor ) {
		Point3D offset = getViewPoint().subtract( anchor );

		// The zoom has to be set before the viewpoint
		setZoom( getZoom() * factor );
		setViewPoint( anchor.add( offset.multiply( 1 / factor ) ) );
	}

	public List<Shape> screenPointSelect( Point3D point, DesignValue tolerance ) {
		double size = valueToWorld( tolerance );
		return worldPointSelect( parentToLocal( point ), new Point2D( size, size ) );
	}

	public List<Shape> screenWindowSelect( Point3D a, Point3D b, boolean contains ) {
		return worldWindowSelect( parentToLocal( a ), parentToLocal( b ), contains );
	}

	public List<Shape> worldPointSelect( Point3D anchor, DesignValue v ) {
		return worldPointSelect( anchor, v.getValue() );
	}

	public List<Shape> worldPointSelect( Point3D anchor, double radius ) {
		return worldPointSelect( anchor, new Point2D( radius, radius ) );
	}

	public List<Shape> worldPointSelect( Point3D anchor, Point2D radii ) {
		return doSelectByShape( new Ellipse( anchor.getX() - radii.getX(), anchor.getY() - radii.getY(), 2 * radii.getX(), 2 * radii.getY() ), false );
	}

	/**
	 * Find the nodes contained by, or intersecting, the window specified by points a and b.
	 *
	 * @param a One corner of the window
	 * @param b The other corner of the window
	 * @param contains True to select nodes contained in the window, false to select nodes intersecting the window
	 * @return The set of selected nodes
	 */
	public List<Shape> worldWindowSelect( Point3D a, Point3D b, boolean contains ) {
		double x = Math.min( a.getX(), b.getX() );
		double y = Math.min( a.getY(), b.getY() );
		double w = Math.abs( a.getX() - b.getX() );
		double h = Math.abs( a.getY() - b.getY() );

		Rectangle box = new Rectangle( x, y, w, h );
		return doSelectByShape( box, contains );
	}

	public List<DesignLayerPane> getLayers() {
		return getLayers( layers );
	}

	public List<DesignLayerPane> getVisibleLayers() {
		return getLayers( layers ).stream().filter( DesignLayerPane::isShowing ).collect( Collectors.toList() );
	}

	public void addLayerGeometry( DesignLayerView view ) {
		DesignLayerPane parent = getDesignLayerView( view.getDesignLayer().getLayer() ).getLayerPane();
		DesignLayerPane layer = view.getLayerPane();
		parent.getChildren().add( layer );
		doReorderLayer( parent );
		layer.showingProperty().bind( layer.visibleProperty().and( parent.showingProperty() ) );
		fireEvent( new DesignLayerEvent( this, DesignLayerEvent.LAYER_ADDED, layer ) );
	}

	public void removeLayerGeometry( DesignLayerView view ) {
		DesignLayerPane layer = view.getLayerPane();
		((DesignLayerPane)layer.getParent()).getChildren().remove( layer );
		layer.showingProperty().unbind();
		fireEvent( new DesignLayerEvent( this, DesignLayerEvent.LAYER_REMOVED, layer ) );
	}

	public List<Shape> getVisibleShapes() {
		return getVisibleLayers()
			.stream()
			.flatMap( l -> l.getChildren().stream() )
			.filter( n -> n instanceof Group )
			.flatMap( g -> ((Group)g).getChildren().stream() )
			.filter( n -> n instanceof Shape )
			.map( n -> (Shape)n )
			.collect( Collectors.toList() );
	}

	public void setGrid( List<Shape> grid ) {
		this.grid.getChildren().clear();
		this.grid.getChildren().addAll( grid );
	}

	public boolean isGridVisible() {
		return this.grid.isVisible();
	}

	public void setGridVisible( boolean visible ) {
		this.grid.setVisible( visible );
	}

	public BooleanProperty gridVisible() {
		return this.grid.visibleProperty();
	}

	public void updateView() {
		doUpdateDpu();
		doRotate();
		doRescale();
		doRecenter();
	}

	DesignLayerPane getLayerPane() {
		return layers;
	}

	void addShapeGeometry( DesignShapeView view ) {
		getShapeLayer( view.getDesignShape() ).getChildren().add( view.getGroup() );
		reference.getChildren().add( view.getCpGroup() );
	}

	void removeShapeGeometry( DesignShapeView view ) {
		((Pane)view.getCpGroup().getParent()).getChildren().remove( view.getCpGroup() );
		((Pane)view.getGroup().getParent()).getChildren().remove( view.getGroup() );
	}

	private void addOriginReferencePoint() {
		ConstructionPoint cp = DesignShapeView.cp( this, Bindings.createDoubleBinding( () -> 0.0 ), Bindings.createDoubleBinding( () -> 0.0 ) );
		reference.getChildren().add( cp.setType( DesignMarker.Type.REFERENCE ) );
	}

	private List<DesignLayerPane> getLayers( DesignLayerPane root ) {
		List<DesignLayerPane> layers = new ArrayList<>();

		root.getChildren().stream().filter( c -> c instanceof DesignLayerPane ).map( c -> (DesignLayerPane)c ).forEach( l -> {
			layers.add( l );
			layers.addAll( getLayers( l ) );
		} );

		return layers;
	}

	private DesignUnit getDesignUnit() {
		return design == null ? Design.DEFAULT_DESIGN_UNIT : design.calcDesignUnit();
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
		//reference.setScaleX( 1 / scale );
		//reference.setScaleY( 1 / scale );
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
		doReorderLayer( layerMap.get( layer ).getLayerPane() );
	}

	private void doReorderLayer( DesignLayerPane pane ) {
		Fx.run( () -> pane.getChildren().setAll( pane.getChildren().sorted( LAYER_SORTER ) ) );
	}

	private void doAddShape( DesignShape shape ) {
		geometryMap.computeIfAbsent( shape, ( k ) -> {
			DesignShapeView view = DesignGeometry.from( this, shape );
			if( view != null ) Fx.run( view::addShapeGeometry );
			return view;
		} );
	}

	private void doRemoveShape( DesignShape shape ) {
		geometryMap.computeIfPresent( shape, ( k, view ) -> {
			Fx.run( view::removeShapeGeometry );
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
	private List<Shape> doSelectByShape( final Shape selector, final boolean contains ) {
		// This method should be thread agnostic, however, the code to calculate
		// selections must be run on the FX thread. If we are on the FX thread then
		// this is just a simple call to fxSelectByShape(). If not, a future must
		// be created to run on the FX thread and return the result.

		if( Fx.isFxThread() ) return fxSelectByShape( selector, contains );

		try {
			return Fx.run( new FutureTask<>( () -> fxSelectByShape( selector, contains ) ) ).get( 500, TimeUnit.MILLISECONDS );
		} catch( ExecutionException | TimeoutException | InterruptedException exception ) {
			log.atWarn( exception ).log( "Unable to select shapes" );
		}

		return List.of();
	}

	// THREAD JavaFX Application Thread
	private List<Shape> fxSelectByShape( final Shape selector, final boolean contains ) {
		Fx.checkFxThread();

		// The shape must have a fill but no stroke. The selector color is not
		// important since the selector shape is not shown, is just needs to be set.
		selector.setFill( Color.RED );
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

	private boolean isIntersecting( Shape selector, Shape shape ) {
		boolean invisibleShape = shape.getFill() == null && shape.getStroke() == null;
		if( invisibleShape ) shape.setStroke( BARELY_VISIBLE );

		// This first test is an optimization to determine if the the accurate test needs to be used
		if( !selector.getBoundsInParent().intersects( shape.getBoundsInParent() ) ) return false;
		// This is the slow but accurate test if the shape is intersecting
		boolean result = !((Path)Shape.intersect( shape, selector )).getElements().isEmpty();

		if( invisibleShape ) shape.setStroke( null );

		return result;
	}

	private boolean isContained( Shape selector, Shape shape ) {
		boolean invisibleShape = shape.getFill() == null && shape.getStroke() == null;
		if( invisibleShape ) shape.setStroke( BARELY_VISIBLE );

		// This first test is an optimization to determine if the the accurate test needs to be used
		if( !selector.getBoundsInParent().intersects( shape.getBoundsInParent() ) ) return false;
		// This is the slow but accurate test if the shape is contained
		boolean result = ((Path)Shape.subtract( shape, selector )).getElements().isEmpty();

		if( invisibleShape ) shape.setStroke( null );

		return result;
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
