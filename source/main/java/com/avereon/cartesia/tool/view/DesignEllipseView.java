package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignEllipseView extends DesignShapeView {

	private EventHandler<NodeEvent> originHandler;

	private EventHandler<NodeEvent> xRadiusHandler;

	private EventHandler<NodeEvent> yRadiusHandler;

	private EventHandler<NodeEvent> rotateHandler;

	public DesignEllipseView( DesignPane pane, DesignEllipse ellipse ) {
		super( pane, ellipse );
	}

	public DesignEllipse getDesignEllipse() {
		return (DesignEllipse)getDesignNode();
	}

	@Override
	protected List<Shape> generateGeometry() {
		DesignEllipse ellipse = getDesignEllipse();

		Shape shape = new Ellipse( ellipse.getOrigin().getX(), ellipse.getOrigin().getY(), ellipse.getXRadius(), ellipse.getYRadius() );
		updateRotate( ellipse, shape );
		shape.setStrokeWidth( ellipse.calcDrawWidth() );
		shape.setStroke( ellipse.calcDrawPaint() );
		shape.setFill( ellipse.calcFillPaint() );
		return List.of( shape );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Ellipse ellipse = (Ellipse)shapes.get( 0 );

		ConstructionPoint origin = cp( pane, ellipse, ellipse.centerXProperty(), ellipse.centerYProperty() );
		ConstructionPoint a = cp( pane, ellipse, ellipse.centerXProperty(), () -> getEllipsePoint( ellipse, 0 ).getX(), ellipse.centerYProperty(), () -> getEllipsePoint( ellipse, 0 ).getY() );
		ConstructionPoint b = cp( pane, ellipse, ellipse.centerXProperty(), () -> getEllipsePoint( ellipse, 90 ).getX(), ellipse.centerYProperty(), () -> getEllipsePoint( ellipse, 90 ).getY() );
		ConstructionPoint c = cp( pane, ellipse, ellipse.centerXProperty(), () -> getEllipsePoint( ellipse, 180 ).getX(), ellipse.centerYProperty(), () -> getEllipsePoint( ellipse, 180 ).getY() );
		ConstructionPoint d = cp( pane, ellipse, ellipse.centerXProperty(), () -> getEllipsePoint( ellipse, -90 ).getX(), ellipse.centerYProperty(), () -> getEllipsePoint( ellipse, -90 ).getY() );

		return setConstructionPoints( ellipse, List.of( origin, a, b, c, d ) );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
		DesignEllipse designEllipse = getDesignEllipse();
		getDesignShape().register( DesignEllipse.ORIGIN, originHandler = e -> Fx.run( () -> {
			((Ellipse)getShape()).setCenterX( designEllipse.getOrigin().getX() );
			((Ellipse)getShape()).setCenterY( designEllipse.getOrigin().getY() );
			// Needed for transforms to work correctly
			updateRotate( designEllipse, getShape() );
		} ) );
		getDesignShape().register( DesignEllipse.X_RADIUS, xRadiusHandler = e -> Fx.run( () -> {
			((Ellipse)getShape()).setRadiusX( designEllipse.getXRadius() );
		} ) );
		getDesignShape().register( DesignEllipse.Y_RADIUS, yRadiusHandler = e -> Fx.run( () -> {
			((Ellipse)getShape()).setRadiusY( designEllipse.getYRadius() );
		} ) );
		getDesignShape().register( DesignEllipse.ROTATE, rotateHandler = e -> Fx.run( () -> {
			updateRotate( designEllipse, getShape() );
		} ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignEllipse.ROTATE, rotateHandler );
		getDesignShape().unregister( DesignEllipse.Y_RADIUS, xRadiusHandler );
		getDesignShape().unregister( DesignEllipse.X_RADIUS, yRadiusHandler );
		getDesignShape().unregister( DesignEllipse.ORIGIN, originHandler );
		super.unregisterListeners();
	}

}
