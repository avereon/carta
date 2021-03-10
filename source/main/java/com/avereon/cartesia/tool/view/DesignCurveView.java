package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignCurve;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.cartesia.tool.DesignPane;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignCurveView extends DesignShapeView {

	private EventHandler<NodeEvent> originHandler;

	private EventHandler<NodeEvent> originControlHandler;

	private EventHandler<NodeEvent> pointControlHandler;

	private EventHandler<NodeEvent> pointHandler;

	public DesignCurveView( DesignPane pane, DesignShape designShape ) {
		super( pane, designShape );
	}

	public DesignCurve getDesignCurve() {
		return (DesignCurve)getDesignNode();
	}

	@Override
	protected List<Shape> generateGeometry() {
		DesignCurve curve = getDesignCurve();
		Shape shape = new CubicCurve( curve.getOrigin().getX(), curve.getOrigin().getY(), curve.getOriginControl().getX(), curve.getOriginControl().getY(), curve.getPointControl().getX(), curve.getPointControl().getY(), curve.getPoint().getX(), curve.getPoint().getY() );
		shape.setStrokeWidth( curve.calcDrawWidth() );
		shape.setStroke( curve.calcDrawPaint() );
		shape.setFill( curve.calcFillPaint() );
		return List.of( shape );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		CubicCurve curve = (CubicCurve)shapes.get( 0 );
		ConstructionPoint a = cp( pane, curve, curve.startXProperty(), curve.startYProperty() );
		ConstructionPoint b = cp( pane, curve, curve.controlX1Property(), curve.controlY1Property() );
		ConstructionPoint c = cp( pane, curve, curve.controlX2Property(), curve.controlY2Property() );
		ConstructionPoint d = cp( pane, curve, curve.endXProperty(), curve.endYProperty() );

		setConstructionPoints( curve, List.of( a, b, c, d ) );

		return getConstructionPoints( curve );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
		DesignCurve designEllipse = getDesignCurve();
		getDesignShape().register( DesignCurve.ORIGIN, originHandler = e -> Fx.run( () -> {
			((CubicCurve)getShape()).setStartX( designEllipse.getOrigin().getX() );
			((CubicCurve)getShape()).setStartY( designEllipse.getOrigin().getY() );
		} ) );
		getDesignShape().register( DesignCurve.ORIGIN_CONTROL, originControlHandler = e -> Fx.run( () -> {
			((CubicCurve)getShape()).setControlX1( designEllipse.getOriginControl().getX() );
			((CubicCurve)getShape()).setControlY1( designEllipse.getOriginControl().getY() );
		} ) );
		getDesignShape().register( DesignCurve.POINT_CONTROL, pointControlHandler = e -> Fx.run( () -> {
			((CubicCurve)getShape()).setControlX2( designEllipse.getPointControl().getX() );
			((CubicCurve)getShape()).setControlY2( designEllipse.getPointControl().getY() );
		} ) );
		getDesignShape().register( DesignCurve.POINT, pointHandler = e -> Fx.run( () -> {
			((CubicCurve)getShape()).setEndX( designEllipse.getPoint().getX() );
			((CubicCurve)getShape()).setEndY( designEllipse.getPoint().getY() );
		} ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignCurve.POINT, pointHandler );
		getDesignShape().unregister( DesignCurve.POINT_CONTROL, pointControlHandler );
		getDesignShape().unregister( DesignCurve.ORIGIN_CONTROL, originControlHandler );
		getDesignShape().unregister( DesignCurve.ORIGIN, originHandler );
		super.unregisterListeners();
	}

}
