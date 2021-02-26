package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

import java.util.List;

public class DesignArcView extends DesignShapeView {

	private EventHandler<NodeEvent> originHandler;

	private EventHandler<NodeEvent> xRadiusHandler;

	private EventHandler<NodeEvent> yRadiusHandler;

	private EventHandler<NodeEvent> rotateHandler;

	private EventHandler<NodeEvent> startHandler;

	private EventHandler<NodeEvent> extentHandler;

	private Rotate rotate;

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
		if( designArc.getRotate() != null ) updateRotate( designArc );
		if( designArc.getType() != null ) arc.setType( designArc.getType().arcType() );
		arc.setStrokeWidth( designArc.calcDrawWidth() );
		arc.setStroke( designArc.calcDrawPaint() );
		arc.setFill( designArc.calcFillPaint() );
		arc.setFill( null );
		return List.of( arc );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		// Points should be at the origin, each endpoint and the midpoint
		Arc arc = (Arc)shapes.get( 0 );

		//ConstructionPoint origin = cp( pane, arc, arc.centerXProperty(), arc.centerYProperty() );
		ConstructionPoint a = cp( pane, arc, arc.centerXProperty(), () -> getArcPoint( arc, arc.getStartAngle() ).getX(), arc.centerYProperty(), () -> getArcPoint( arc, arc.getStartAngle() ).getY() );
		ConstructionPoint b = cp( pane, arc, arc.centerXProperty(), () -> getArcPoint( arc, arc.getStartAngle() + arc.getLength() ).getX(), arc.centerYProperty(), () -> getArcPoint( arc, arc.getStartAngle() + arc.getLength() ).getY() );
		ConstructionPoint c = cp( pane, arc, arc.centerXProperty(), () -> getArcPoint( arc, arc.getStartAngle() + 0.5 * arc.getLength() ).getX(), arc.centerYProperty(), () -> getArcPoint( arc, arc.getStartAngle() + 0.5 * arc.getLength() ).getY() );

		setConstructionPoints( arc, List.of( a, b, c ) );

		return getConstructionPoints( arc );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
		DesignArc designArc = getDesignArc();
		getDesignShape().register( DesignEllipse.ORIGIN, originHandler = e -> Fx.run( () -> {
			((Arc)getShape()).setCenterX( designArc.getOrigin().getX() );
			((Arc)getShape()).setCenterY( designArc.getOrigin().getY() );
		} ) );
		getDesignShape().register( DesignEllipse.X_RADIUS, xRadiusHandler = e -> Fx.run( () -> {
			((Arc)getShape()).setRadiusX( designArc.getXRadius() );
		} ) );
		getDesignShape().register( DesignEllipse.Y_RADIUS, yRadiusHandler = e -> Fx.run( () -> {
			((Arc)getShape()).setRadiusY( designArc.getYRadius() );
		} ) );
		getDesignShape().register( DesignEllipse.ROTATE, rotateHandler = e -> Fx.run( () -> {
			updateRotate( designArc );
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

	private void updateRotate( DesignArc arc ) {
		if( getShape() == null || arc.getRotate() == null ) return;
		getShape().getTransforms().remove( this.rotate );
		this.rotate = new Rotate( arc.getRotate(), arc.getOrigin().getX(), arc.getOrigin().getY() );
		getShape().getTransforms().add( this.rotate );
	}

}
