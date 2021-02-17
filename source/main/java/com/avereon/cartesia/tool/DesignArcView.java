package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignArcView extends DesignShapeView {

	private EventHandler<NodeEvent> originHandler;

	private EventHandler<NodeEvent> radiusHandler;

	public DesignArcView( DesignPane pane, DesignArc arc ) {
		super( pane, arc );
	}

	public DesignArc getDesignArc() {
		return (DesignArc)getDesignNode();
	}

	@Override
	protected List<Shape> generateGeometry() {
		DesignArc designArc = getDesignArc();

		// NEXT Generate non-circle arc geometry

		Circle circle = new Circle( getDesignArc().getOrigin().getX(), getDesignArc().getOrigin().getY(), getDesignArc().getRadius() );
		circle.setStrokeWidth( designArc.calcDrawWidth() );
		circle.setStroke( designArc.calcDrawPaint() );
		circle.setFill( designArc.calcFillPaint() );
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
		getDesignShape().register( DesignArc.ORIGIN, originHandler = e -> Fx.run( () -> {
			((Circle)getShape()).setCenterX( getDesignArc().getOrigin().getX() );
			((Circle)getShape()).setCenterY( getDesignArc().getOrigin().getY() );
		} ) );
		getDesignShape().register( DesignArc.RADIUS, radiusHandler = e -> Fx.run( () -> {
			((Circle)getShape()).setRadius( getDesignArc().getRadius() );
		} ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignArc.RADIUS, radiusHandler );
		getDesignShape().unregister( DesignArc.ORIGIN, originHandler );
		super.unregisterListeners();
	}

}
