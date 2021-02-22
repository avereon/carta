package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignMarker;
import com.avereon.cartesia.data.DesignMarkers;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;

import java.util.List;

public class DesignMarkerView extends DesignShapeView {

	private EventHandler<NodeEvent> originHandler;

	private EventHandler<NodeEvent> pointTypeHandler;

	private EventHandler<NodeEvent> pointSizeHandler;

	public DesignMarkerView( DesignPane pane, DesignMarker point ) {
		super( pane, point );
	}

	public DesignMarker getDesignMarker() {
		return (DesignMarker)getDesignNode();
	}

	@Override
	public List<Shape> generateGeometry() {
		double ox = getDesignMarker().getOrigin().getX();
		double oy = getDesignMarker().getOrigin().getY();
		Path path = DesignMarkers.createPoint( getDesignMarker().calcType(), ox, oy, getDesignMarker().getRadius() );
		return List.of( path );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Path path = (Path)shapes.get( 0 );
		MoveTo origin = ((MoveTo)path.getElements().get( 0 ));
		ConstructionPoint o = cp( pane, path, origin.xProperty(), origin.yProperty() );
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
		getDesignShape().register( DesignMarker.ORIGIN, originHandler = e -> Fx.run( () -> {
			getShape().setLayoutX( getDesignShape().getOrigin().getX() );
			getShape().setLayoutY( getDesignShape().getOrigin().getY() );
		} ) );
		getDesignShape().register( DesignMarker.TYPE, pointTypeHandler = e -> Fx.run( this::updateGeometry ) );
		getDesignShape().register( DesignMarker.SIZE, pointSizeHandler = e -> Fx.run( this::updateGeometry ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignMarker.SIZE, pointSizeHandler );
		getDesignShape().unregister( DesignMarker.TYPE, pointTypeHandler );
		getDesignShape().unregister( DesignMarker.ORIGIN, originHandler );
		super.unregisterListeners();
	}

}
