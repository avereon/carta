package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignCircle;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignCircleView extends DesignShapeView {

	private EventHandler<NodeEvent> originHandler;

	private EventHandler<NodeEvent> radiusHandler;

	public DesignCircleView( DesignPane pane, DesignCircle circle ) {
		super( pane, circle );
	}

	public DesignCircle getDesignCircle() {
		return (DesignCircle)getDesignNode();
	}

	@Override
	protected List<Shape> generateGeometry() {
		DesignCircle dCircle = getDesignCircle();
		Circle circle = new Circle( getDesignCircle().getOrigin().getX(), getDesignCircle().getOrigin().getY(), getDesignCircle().getRadius() );
		circle.setStrokeWidth( dCircle.calcDrawWidth() );
		circle.setStroke( dCircle.calcDrawPaint() );
		circle.setFill( dCircle.calcFillPaint() );
		return List.of( circle );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Circle circle = (Circle)shapes.get( 0 );
		ConstructionPoint origin = cp( pane, circle.centerXProperty(), circle.centerYProperty() );
		origin.visibleProperty().bind( circle.visibleProperty() );

		ConstructionPoint a = cp( pane, circle.centerXProperty().add( circle.getRadius() ), circle.centerYProperty().add( 0 ) );
		ConstructionPoint b = cp( pane, circle.centerXProperty().add( 0 ), circle.centerYProperty().subtract( circle.getRadius() ) );
		ConstructionPoint c = cp( pane, circle.centerXProperty().subtract( circle.getRadius() ), circle.centerYProperty().add( 0 ) );
		ConstructionPoint d = cp( pane, circle.centerXProperty().add( 0 ), circle.centerYProperty().add( circle.getRadius() ) );

		return setConstructionPoints( circle, List.of( origin, a, b, c, d ) );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
		getDesignShape().register( DesignCircle.ORIGIN, originHandler = e -> Fx.run( () -> {
			((Circle)getShape()).setCenterX( getDesignCircle().getOrigin().getX() );
			((Circle)getShape()).setCenterY( getDesignCircle().getOrigin().getY() );
		} ) );
		getDesignShape().register( DesignCircle.RADIUS, radiusHandler = e -> Fx.run( () -> {
			((Circle)getShape()).setRadius( getDesignCircle().getRadius() );
		} ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignCircle.RADIUS, radiusHandler );
		getDesignShape().unregister( DesignCircle.ORIGIN, originHandler );
		super.unregisterListeners();
	}

}
