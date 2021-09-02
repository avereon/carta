package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.beans.binding.Bindings;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignArcView extends DesignShapeView {

	private EventHandler<NodeEvent> originHandler;

	private EventHandler<NodeEvent> xRadiusHandler;

	private EventHandler<NodeEvent> yRadiusHandler;

	private EventHandler<NodeEvent> rotateHandler;

	private EventHandler<NodeEvent> startHandler;

	private EventHandler<NodeEvent> extentHandler;

	public DesignArcView( DesignPane pane, DesignArc arc ) {
		super( pane, arc );
	}

	public DesignArc getDesignArc() {
		return (DesignArc)getDesignNode();
	}

	@Override
	protected List<Shape> generateGeometry() {
		DesignArc designArc = getDesignArc();
		Arc arc = new Arc( designArc.getOrigin().getX(), designArc.getOrigin().getY(), designArc.getXRadius(), designArc.getYRadius(), -designArc.getStart(), -designArc.getExtent() );
		return List.of( arc );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		// Points should be at the origin, each endpoint and the midpoint
		Arc arc = (Arc)shapes.get( 0 );

		ConstructionPoint a = cp( pane,
			Bindings.createDoubleBinding( () -> getArcPoint( arc, arc.getStartAngle() ).getX(), arc.centerXProperty(), arc.radiusXProperty(), arc.getTransforms(), arc.startAngleProperty() ),
			Bindings.createDoubleBinding( () -> getArcPoint( arc, arc.getStartAngle() ).getY(), arc.centerYProperty(), arc.radiusYProperty(), arc.getTransforms(), arc.startAngleProperty() )
		);
		ConstructionPoint c = cp( pane, Bindings.createDoubleBinding( () -> getArcPoint( arc, arc.getStartAngle() + 0.5 * arc.getLength() ).getX(),
			arc.centerXProperty(),
			arc.radiusXProperty(),
			arc.getTransforms(),
			arc.startAngleProperty(),
			arc.lengthProperty()
		), Bindings.createDoubleBinding( () -> getArcPoint( arc, arc.getStartAngle() + 0.5 * arc.getLength() ).getY(),
			arc.centerYProperty(),
			arc.radiusYProperty(),
			arc.getTransforms(),
			arc.startAngleProperty(),
			arc.lengthProperty()
		) );
		ConstructionPoint b = cp( pane,
			Bindings.createDoubleBinding( () -> getArcPoint( arc, arc.getStartAngle() + arc.getLength() ).getX(),
				arc.centerXProperty(),
				arc.radiusXProperty(),
				arc.getTransforms(),
				arc.startAngleProperty(),
				arc.lengthProperty()
			),
			Bindings.createDoubleBinding( () -> getArcPoint( arc, arc.getStartAngle() + arc.getLength() ).getY(),
				arc.centerYProperty(),
				arc.radiusYProperty(),
				arc.getTransforms(),
				arc.startAngleProperty(),
				arc.lengthProperty()
			)
		);
		return setConstructionPoints( arc, List.of( a, b, c ) );
	}

	@Override
	protected void configureShape( Shape shape ) {
		super.configureShape( shape );

		DesignArc designArc = getDesignArc();
		Arc arc = (Arc)shape;

		arc.setType( designArc.getType() == null ? ArcType.OPEN : designArc.getType().arcType() );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
		DesignArc designArc = getDesignArc();
		getDesignShape().register( DesignEllipse.ORIGIN, originHandler = e -> Fx.run( () -> {
			((Arc)getShape()).setCenterX( designArc.getOrigin().getX() );
			((Arc)getShape()).setCenterY( designArc.getOrigin().getY() );
			// Needed for transforms to work correctly
			updateRotate( designArc, getShape() );
		} ) );
		getDesignShape().register( DesignEllipse.X_RADIUS, xRadiusHandler = e -> Fx.run( () -> {
			((Arc)getShape()).setRadiusX( designArc.getXRadius() );
		} ) );
		getDesignShape().register( DesignEllipse.Y_RADIUS, yRadiusHandler = e -> Fx.run( () -> {
			((Arc)getShape()).setRadiusY( designArc.getYRadius() );
		} ) );
		getDesignShape().register( DesignEllipse.ROTATE, rotateHandler = e -> Fx.run( () -> {
			updateRotate( designArc, getShape() );
		} ) );
		getDesignShape().register( DesignArc.START, startHandler = e -> Fx.run( () -> {
			((Arc)getShape()).setStartAngle( -designArc.getStart() );
		} ) );
		getDesignShape().register( DesignArc.EXTENT, extentHandler = e -> Fx.run( () -> {
			((Arc)getShape()).setLength( -designArc.getExtent() );
		} ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignArc.EXTENT, extentHandler );
		getDesignShape().unregister( DesignArc.START, startHandler );
		getDesignShape().unregister( DesignEllipse.ROTATE, rotateHandler );
		getDesignShape().unregister( DesignEllipse.Y_RADIUS, xRadiusHandler );
		getDesignShape().unregister( DesignEllipse.X_RADIUS, yRadiusHandler );
		getDesignShape().unregister( DesignEllipse.ORIGIN, originHandler );
		super.unregisterListeners();
	}

}
