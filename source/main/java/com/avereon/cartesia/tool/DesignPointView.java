package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignPoint;
import com.avereon.cartesia.math.Points;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;

import java.util.List;

public class DesignPointView extends DesignShapeView {

	private EventHandler<NodeEvent> pointTypeHandler;

	private EventHandler<NodeEvent> pointSizeHandler;

	public DesignPointView( DesignPane pane, DesignPoint point ) {
		super( pane, point );
	}

	public DesignPoint getDesignPoint() {
		return (DesignPoint)getDesignNode();
	}

	@Override
	protected List<Shape> generateGeometry() {
		double ox = getDesignPoint().getOrigin().getX();
		double oy = getDesignPoint().getOrigin().getY();
		Path path = Points.createPoint( getDesignPoint().calcType(), ox, oy, getDesignPoint().getRadius() );
		return List.of( path );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Path path = (Path)shapes.get( 0 );
		MoveTo m = ((MoveTo)path.getElements().get( 0 ));
		ConstructionPoint o = cp( pane, m.xProperty(), m.yProperty() );
		o.visibleProperty().bind( path.visibleProperty() );

		List<ConstructionPoint> cps = List.of( o );
		path.getProperties().put( CONSTRUCTION_POINTS, cps );
		return cps;
	}

	protected void configureShape( Shape shape ) {
		shape.setStrokeLineCap( StrokeLineCap.BUTT );
		shape.setStrokeWidth( getDesignShape().calcDrawWidth() );
		shape.setStroke( getDesignShape().calcDrawColor() );
		shape.setFill( getDesignShape().calcDrawColor() );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
		getDesignShape().register( DesignPoint.TYPE, pointTypeHandler = e -> Fx.run( this::updateGeometry ) );
		getDesignShape().register( DesignPoint.SIZE, pointSizeHandler = e -> Fx.run( this::updateGeometry ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignPoint.SIZE, pointSizeHandler );
		getDesignShape().unregister( DesignPoint.TYPE, pointTypeHandler );
		super.unregisterListeners();
	}

}
