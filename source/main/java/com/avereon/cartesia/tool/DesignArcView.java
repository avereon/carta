package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignArcView extends DesignShapeView {

	private EventHandler<NodeEvent> originHandler;

	private EventHandler<NodeEvent> xRadiusHandler;

	private EventHandler<NodeEvent> yRadiusHandler;

	public DesignArcView( DesignPane pane, DesignArc arc ) {
		super( pane, arc );
	}

	public DesignArc getDesignArc() {
		return (DesignArc)getDesignNode();
	}

	@Override
	protected List<Shape> generateGeometry() {
		DesignArc designArc = getDesignArc();
		Arc arc = new Arc( designArc.getOrigin().getX(), designArc.getOrigin().getY(), designArc.getXRadius(), designArc.getYRadius(), designArc.getStart(), designArc.getExtent() );
		if( designArc.getType() != null ) arc.setType( designArc.getType().arcType() );
		if( designArc.getRotate() != null ) arc.setRotate( designArc.getRotate() );
		arc.setStrokeWidth( designArc.calcDrawWidth() );
		arc.setStroke( designArc.calcDrawPaint() );
		arc.setFill( designArc.calcFillPaint() );
		return List.of( arc );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Arc arc = (Arc)shapes.get( 0 );
		ConstructionPoint origin = cp( pane, arc, arc.centerXProperty(), arc.centerYProperty() );

		ConstructionPoint a = cp( pane, arc, arc.centerXProperty().add( arc.getRadiusX() ), arc.centerYProperty().add( 0 ) );
		ConstructionPoint b = cp( pane, arc, arc.centerXProperty().add( 0 ), arc.centerYProperty().subtract( arc.getRadiusY() ) );
		ConstructionPoint c = cp( pane, arc, arc.centerXProperty().subtract( arc.getRadiusX() ), arc.centerYProperty().add( 0 ) );
		ConstructionPoint d = cp( pane, arc, arc.centerXProperty().add( 0 ), arc.centerYProperty().add( arc.getRadiusY() ) );

		setConstructionPoints( arc, List.of( origin, a, b, c, d ) );

		return getConstructionPoints( arc );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
		DesignArc designArc = getDesignArc();
		getDesignShape().register( DesignArc.ORIGIN, originHandler = e -> Fx.run( () -> {
			((Arc)getShape()).setCenterX( designArc.getOrigin().getX() );
			((Arc)getShape()).setCenterY( designArc.getOrigin().getY() );
		} ) );
		getDesignShape().register( DesignArc.X_RADIUS, xRadiusHandler = e -> Fx.run( () -> {
			((Arc)getShape()).setRadiusX( designArc.getXRadius() );
		} ) );
		getDesignShape().register( DesignArc.Y_RADIUS, yRadiusHandler = e -> Fx.run( () -> {
			((Arc)getShape()).setRadiusY( designArc.getYRadius() );
		} ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignArc.Y_RADIUS, yRadiusHandler );
		getDesignShape().unregister( DesignArc.X_RADIUS, xRadiusHandler );
		getDesignShape().unregister( DesignArc.ORIGIN, originHandler );
		super.unregisterListeners();
	}

}
