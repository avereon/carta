package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignMarker;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zarra.javafx.Fx;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignMarkerView extends DesignShapeView {

	private EventHandler<NodeEvent> originHandler;

	private EventHandler<NodeEvent> markerTypeHandler;

	private EventHandler<NodeEvent> markerSizeHandler;

	public DesignMarkerView( DesignPane pane, DesignMarker marker ) {
		super( pane, marker );
	}

	public DesignMarker getDesignMarker() {
		return (DesignMarker)getDesignNode();
	}

	@Override
	public List<Shape> generateGeometry() {
		DesignMarker marker = getDesignMarker();
		Path path = marker.calcType().getPath();
		path.setLayoutX( marker.getOrigin().getX() );
		path.setLayoutY( marker.getOrigin().getY() );
		path.setScaleX( marker.calcSize() );
		path.setScaleY( marker.calcSize() );
		return List.of( path );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Path path = (Path)shapes.get( 0 );
		ConstructionPoint o = cp( pane, path.layoutXProperty(), path.layoutYProperty() );
		return setConstructionPoints( path, List.of( o ) );
	}

	public void configureShape( Shape shape ) {
		// Do the normal stuff
		super.configureShape( shape );

		// But then do some things different for markers
		shape.setFill( getDesignShape().calcDrawPaint() );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
		getDesignShape().register( DesignMarker.ORIGIN, originHandler = e -> Fx.run( () -> {
			getShape().setLayoutX( getDesignShape().getOrigin().getX() );
			getShape().setLayoutY( getDesignShape().getOrigin().getY() );
		} ) );
		getDesignShape().register( DesignMarker.TYPE, markerTypeHandler = e -> Fx.run( this::updateGeometry ) );
		getDesignShape().register( DesignMarker.SIZE, markerSizeHandler = e -> Fx.run( this::updateGeometry ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignMarker.SIZE, markerSizeHandler );
		getDesignShape().unregister( DesignMarker.TYPE, markerTypeHandler );
		getDesignShape().unregister( DesignMarker.ORIGIN, originHandler );
		super.unregisterListeners();
	}

}
