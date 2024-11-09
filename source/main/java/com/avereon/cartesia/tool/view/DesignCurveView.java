package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignCubic;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zarra.javafx.Fx;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Shape;

import java.util.List;

@Deprecated
public class DesignCurveView extends DesignShapeView {

	private EventHandler<NodeEvent> originHandler;

	private EventHandler<NodeEvent> originControlHandler;

	private EventHandler<NodeEvent> pointControlHandler;

	private EventHandler<NodeEvent> pointHandler;

	public DesignCurveView( DesignPane pane, DesignShape designShape ) {
		super( pane, designShape );
	}

	public DesignCubic getDesignCurve() {
		return (DesignCubic)getDesignNode();
	}

	@Override
	protected List<Shape> generateGeometry() {
		DesignCubic curve = getDesignCurve();
		Shape shape = new CubicCurve(
			curve.getOrigin().getX(),
			curve.getOrigin().getY(),
			curve.getOriginControl().getX(),
			curve.getOriginControl().getY(),
			curve.getPointControl().getX(),
			curve.getPointControl().getY(),
			curve.getPoint().getX(),
			curve.getPoint().getY()
		);
		return List.of( shape );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		CubicCurve curve = (CubicCurve)shapes.get( 0 );
		ConstructionPoint a = cp( pane, curve.startXProperty(), curve.startYProperty() );
		ConstructionPoint b = cp( pane, curve.controlX1Property(), curve.controlY1Property() );
		ConstructionPoint c = cp( pane, curve.controlX2Property(), curve.controlY2Property() );
		ConstructionPoint d = cp( pane, curve.endXProperty(), curve.endYProperty() );

		return setConstructionPoints( curve, List.of( a, b, c, d ) );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
		DesignCubic designEllipse = getDesignCurve();
		getDesignShape().register( DesignCubic.ORIGIN, originHandler = e -> Fx.run( () -> {
			((CubicCurve)getShape()).setStartX( designEllipse.getOrigin().getX() );
			((CubicCurve)getShape()).setStartY( designEllipse.getOrigin().getY() );
		} ) );
		getDesignShape().register( DesignCubic.ORIGIN_CONTROL, originControlHandler = e -> Fx.run( () -> {
			((CubicCurve)getShape()).setControlX1( designEllipse.getOriginControl().getX() );
			((CubicCurve)getShape()).setControlY1( designEllipse.getOriginControl().getY() );
		} ) );
		getDesignShape().register( DesignCubic.POINT_CONTROL, pointControlHandler = e -> Fx.run( () -> {
			((CubicCurve)getShape()).setControlX2( designEllipse.getPointControl().getX() );
			((CubicCurve)getShape()).setControlY2( designEllipse.getPointControl().getY() );
		} ) );
		getDesignShape().register( DesignCubic.POINT, pointHandler = e -> Fx.run( () -> {
			((CubicCurve)getShape()).setEndX( designEllipse.getPoint().getX() );
			((CubicCurve)getShape()).setEndY( designEllipse.getPoint().getY() );
		} ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignCubic.POINT, pointHandler );
		getDesignShape().unregister( DesignCubic.POINT_CONTROL, pointControlHandler );
		getDesignShape().unregister( DesignCubic.ORIGIN_CONTROL, originControlHandler );
		getDesignShape().unregister( DesignCubic.ORIGIN, originHandler );
		super.unregisterListeners();
	}

}
