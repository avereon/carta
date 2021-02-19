package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignEllipse;
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

		if( ellipse.getRotate() != null ) shape.setRotate( ellipse.getRotate() );
		shape.setStrokeWidth( ellipse.calcDrawWidth() );
		shape.setStroke( ellipse.calcDrawPaint() );
		shape.setFill( ellipse.calcFillPaint() );
		return List.of( shape );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Ellipse ellipse = (Ellipse)shapes.get( 0 );
		ConstructionPoint origin = cp( pane, ellipse, ellipse.centerXProperty(), ellipse.centerYProperty() );

		ConstructionPoint a = cp( pane, ellipse, ellipse.centerXProperty().add( ellipse.getRadiusX() ), ellipse.centerYProperty().add( 0 ) );
		ConstructionPoint b = cp( pane, ellipse, ellipse.centerXProperty().add( 0 ), ellipse.centerYProperty().subtract( ellipse.getRadiusY() ) );
		ConstructionPoint c = cp( pane, ellipse, ellipse.centerXProperty().subtract( ellipse.getRadiusX() ), ellipse.centerYProperty().add( 0 ) );
		ConstructionPoint d = cp( pane, ellipse, ellipse.centerXProperty().add( 0 ), ellipse.centerYProperty().add( ellipse.getRadiusY() ) );

		setConstructionPoints( ellipse, List.of( origin, a, b, c, d ) );

		return getConstructionPoints( ellipse );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
		DesignEllipse designEllipse = getDesignEllipse();
		getDesignShape().register( DesignEllipse.ORIGIN, originHandler = e -> Fx.run( () -> {
			((Ellipse)getShape()).setCenterX( designEllipse.getOrigin().getX() );
			((Ellipse)getShape()).setCenterY( designEllipse.getOrigin().getY() );
		} ) );
		getDesignShape().register( DesignEllipse.X_RADIUS, xRadiusHandler = e -> Fx.run( () -> {
			((Ellipse)getShape()).setRadiusX( designEllipse.getXRadius() );
		} ) );
		getDesignShape().register( DesignEllipse.Y_RADIUS, yRadiusHandler = e -> Fx.run( () -> {
			((Ellipse)getShape()).setRadiusY( designEllipse.getYRadius() );
		} ) );
		getDesignShape().register( DesignEllipse.ROTATE, rotateHandler = e -> Fx.run( () -> {
			getShape().setRotate( designEllipse.getRotate() );
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
