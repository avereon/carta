package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignPoint;
import com.avereon.cartesia.data.DesignPoints;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;

import java.util.List;

public class DesignPointView extends DesignShapeView {

	private EventHandler<NodeEvent> originHandler;

	private EventHandler<NodeEvent> pointTypeHandler;

	private EventHandler<NodeEvent> pointSizeHandler;

	public DesignPointView( DesignPane pane, DesignPoint point ) {
		super( pane, point );
	}

	public DesignPoint getDesignPoint() {
		return (DesignPoint)getDesignNode();
	}

	@Override
	public List<Shape> generateGeometry() {
		double ox = getDesignPoint().getOrigin().getX();
		double oy = getDesignPoint().getOrigin().getY();
		Path path = DesignPoints.createPoint( getDesignPoint().calcType(), ox, oy, getDesignPoint().getRadius() );
		return List.of( path );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Path path = (Path)shapes.get( 0 );
		MoveTo m = ((MoveTo)path.getElements().get( 0 ));
		ConstructionPoint o = cp( pane, m.xProperty(), m.yProperty() );
		o.visibleProperty().bind( path.visibleProperty() );
		return setConstructionPoints( path, List.of( o ) );
	}

	public void configureShape( Shape shape ) {
		// Do the normal stuff
		super.configureShape( shape );

		// But then do some things different for points
		shape.setStrokeLineCap( StrokeLineCap.BUTT );
		shape.setFill( getDesignShape().calcDrawPaint() );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
		getDesignShape().register( DesignPoint.ORIGIN, originHandler = e -> Fx.run( () -> {
			getShape().setLayoutX( getDesignPoint().getOrigin().getX() );
			getShape().setLayoutY( getDesignPoint().getOrigin().getY() );
		}));
		getDesignShape().register( DesignPoint.TYPE, pointTypeHandler = e -> Fx.run( this::updateGeometry ) );
		getDesignShape().register( DesignPoint.SIZE, pointSizeHandler = e -> Fx.run( this::updateGeometry ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignPoint.SIZE, pointSizeHandler );
		getDesignShape().unregister( DesignPoint.TYPE, pointTypeHandler );
		getDesignShape().unregister( DesignPoint.ORIGIN, originHandler );
		super.unregisterListeners();
	}

}
