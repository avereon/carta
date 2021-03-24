package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignDrawable;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.cartesia.tool.DesignPane;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.util.Log;
import com.avereon.zerra.javafx.Fx;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

import java.util.List;
import java.util.concurrent.Callable;

public class DesignShapeView extends DesignDrawableView {

	private static final System.Logger log = Log.get();

	private static final String DESIGN_DATA = "design-data";

	private static final String CONSTRUCTION_POINTS = "construction-points";

	private Group group;

	private List<Shape> geometry;

	private EventHandler<NodeEvent> parentChangedHandler;

	private EventHandler<NodeEvent> drawWidthHandler;

	private EventHandler<NodeEvent> drawPaintHandler;

	private EventHandler<NodeEvent> drawCapHandler;

	private EventHandler<NodeEvent> drawPatternHandler;

	private EventHandler<NodeEvent> fillPaintHandler;

	private EventHandler<NodeEvent> selectedHandler;

	private Rotate rotate;

	public DesignShapeView( DesignPane pane, DesignShape designShape ) {
		super( pane, designShape );
		generate();
	}

	public DesignShape getDesignShape() {
		return (DesignShape)getDesignNode();
	}

	public Group getGroup() {
		return group;
	}

	protected Shape getShape() {
		return geometry == null || geometry.isEmpty() ? null : geometry.get( 0 );
	}

	protected List<Shape> generateGeometry() {
		return List.of();
	}

	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		return List.of();
	}

	protected void updateGeometry() {
		removeShapeGeometry();
		generate();
		addShapeGeometry();
	}

	protected void configureShape( Shape shape ) {
		shape.setStrokeWidth( getDesignShape().calcDrawWidth() );
		shape.setStroke( getDesignShape().calcDrawPaint() );
		shape.setStrokeLineCap( getDesignShape().calcDrawCap() );
		shape.setFill( getDesignShape().calcFillPaint() );
	}

	public void addShapeGeometry() {
		getPane().addShapeGeometry( this );
		registerListeners();
	}

	public void removeShapeGeometry() {
		unregisterListeners();
		getPane().removeShapeGeometry( this );
	}

	@Override
	void registerListeners() {
		getDesignShape().register( NodeEvent.PARENT_CHANGED, parentChangedHandler = e -> Fx.run( this::updateShapeValues ) );
		getDesignShape().register( DesignShape.DRAW_PAINT, drawPaintHandler = e -> Fx.run( this::updateShapeValues ) );
		getDesignShape().register( DesignShape.DRAW_WIDTH, drawWidthHandler = e -> Fx.run( this::updateShapeValues ) );
		getDesignShape().register( DesignShape.DRAW_CAP, drawCapHandler = e -> Fx.run( this::updateShapeValues ) );
		getDesignShape().register( DesignShape.DRAW_PATTERN, drawPatternHandler = e -> Fx.run( this::updateShapeValues ) );
		getDesignShape().register( DesignShape.FILL_PAINT, fillPaintHandler = e -> Fx.run( this::updateShapeValues ) );
		getDesignShape().register( DesignShape.SELECTED, selectedHandler = e -> Fx.run( () -> doSetSelected( e.getNewValue() != null && (Boolean)e.getNewValue() ) ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignShape.SELECTED, selectedHandler );
		getDesignShape().unregister( DesignShape.FILL_PAINT, fillPaintHandler );
		getDesignShape().unregister( DesignShape.DRAW_PATTERN, drawPatternHandler );
		getDesignShape().unregister( DesignShape.DRAW_CAP, drawCapHandler );
		getDesignShape().unregister( DesignShape.DRAW_WIDTH, drawWidthHandler );
		getDesignShape().unregister( DesignShape.DRAW_PAINT, drawPaintHandler );
		getDesignShape().unregister( NodeEvent.PARENT_CHANGED, parentChangedHandler );
	}

	void updateRotate( DesignEllipse ellipse, Shape shape ) {
		shape.getTransforms().remove( this.rotate );
		this.rotate = Transform.rotate( ellipse.calcRotate(), ellipse.getOrigin().getX(), ellipse.getOrigin().getY() );
		shape.getTransforms().add( this.rotate );
	}

	Point3D getArcPoint( Arc arc, double angle ) {
		// NOTE The rotate angle does not come from the shape rotate property, but from the rotate transform
		return CadGeometry.ellipsePoint360( new Point3D( arc.getCenterX(), arc.getCenterY(), 0 ), arc.getRadiusX(), -arc.getRadiusY(), getRotate(), angle );
	}

	Point3D getEllipsePoint( Ellipse ellipse, double angle ) {
		// NOTE The rotate angle does not come from the shape rotate property, but from the rotate transform
		return CadGeometry.ellipsePoint360( new Point3D( ellipse.getCenterX(), ellipse.getCenterY(), 0 ), ellipse.getRadiusX(), -ellipse.getRadiusY(), getRotate(), angle );
	}

	private double getRotate() {
		return rotate == null ? 0.0 : rotate.getAngle();
	}

	private void generate() {
		geometry = generateGeometry();
		geometry.forEach( this::configureShape );
		List<ConstructionPoint> cps = generateConstructionPoints( getPane(), geometry );

		Group cpGroup = new Group();
		cpGroup.getChildren().addAll( cps );

		group = new Group();
		group.getChildren().addAll( geometry );
		group.getChildren().addAll( cpGroup );
		setDesignData( group, getDesignShape() );
	}

	private void updateShapeValues() {
		configureShape( getShape() );
	}

	private void doSetSelected( boolean selected ) {
		if( ((DesignDrawable)getDesignNode()).getLayer() == null ) return;

		Paint fillPaint = getDesignShape().calcFillPaint();
		Paint selectedFillPaint = fillPaint == null ? null : getPane().getSelectFillPaint();

		//		if( fillPaint instanceof Color && selectedFillPaint instanceof Color ) {
		//			double opacity = ((Color)fillPaint).getOpacity();
		//			selectedFillPaint = Colors.mix( Colors.opaque( (Color)selectedFillPaint ), Color.TRANSPARENT, opacity );
		//			selectedFillPaint = Colors.opaque( (Color)selectedFillPaint );
		//		}

		getShape().setStroke( selected ? getPane().getSelectDrawPaint() : getDesignShape().calcDrawPaint() );
		getShape().setFill( selected ? selectedFillPaint : fillPaint );
	}

	public static DesignDrawable getDesignData( Node node ) {
		DesignDrawable data = (DesignDrawable)node.getProperties().get( DESIGN_DATA );
		if( data == null ) log.log( Log.WARN, "Missing design data for " + node );
		return data;
	}

	public static DesignShape getDesignData( Shape s ) {
		return (DesignShape)getDesignData( s.getParent() );
	}

	public static DesignDrawable setDesignData( Node node, DesignDrawable data ) {
		node.getProperties().put( DESIGN_DATA, data );
		return data;
	}

	@SuppressWarnings( "unchecked" )
	public static List<ConstructionPoint> getConstructionPoints( Shape shape ) {
		return (List<ConstructionPoint>)shape.getProperties().getOrDefault( CONSTRUCTION_POINTS, List.of() );
	}

	static ConstructionPoint cp( DesignPane pane, Shape shape, ObservableValue<Number> xProperty, Callable<Double> xAction, ObservableValue<Number> yProperty, Callable<Double> yAction ) {
		DoubleBinding xBinding = Bindings.createDoubleBinding( xAction, xProperty );
		DoubleBinding yBinding = Bindings.createDoubleBinding( yAction, yProperty );
		return cp( pane, shape, xBinding, yBinding );
	}

	static ConstructionPoint cp( DesignPane pane, Shape shape, ObservableValue<Number> xBinding, ObservableValue<Number> yBinding ) {
		ConstructionPoint cp = new ConstructionPoint();
		cp.visibleProperty().bind( shape.visibleProperty() );
		cp.scaleXProperty().bind( Bindings.divide( 1, pane.scaleXProperty() ) );
		cp.scaleYProperty().bind( Bindings.divide( 1, pane.scaleYProperty() ) );
		cp.layoutXProperty().bind( xBinding );
		cp.layoutYProperty().bind( yBinding );
		return cp;
	}

	static List<ConstructionPoint> setConstructionPoints( Shape shape, List<ConstructionPoint> cps ) {
		shape.getProperties().put( CONSTRUCTION_POINTS, cps );
		return cps;
	}

}
