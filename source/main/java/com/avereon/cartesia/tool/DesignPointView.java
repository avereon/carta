package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignPoint;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.shape.Shape;

public class DesignPointView extends DesignShapeView{

	private EventHandler<NodeEvent> pointTypeHandler;

	private EventHandler<NodeEvent> pointSizeHandler;

	public DesignPointView( DesignPane pane, DesignPoint point, Shape shape ) {
		super( pane, point, shape );
	}

	@Override
	public void registerListeners() {
		super.registerListeners();
		getDesignShape().register( DesignPoint.TYPE, pointTypeHandler = e -> Fx.run( () -> updateGeometry() ) );
		getDesignShape().register( DesignPoint.SIZE, pointSizeHandler = e -> Fx.run( () -> updateGeometry() ) );
	}

	@Override
	public void unregisterListeners() {
//		designShape.unregister( DesignPoint.SIZE, pointSizeHandler );
//		designShape.unregister( DesignPoint.TYPE, pointTypeHandler );
		super.unregisterListeners();
	}
}
