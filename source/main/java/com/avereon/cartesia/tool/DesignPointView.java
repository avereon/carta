package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignPoint;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;

public class DesignPointView extends DesignShapeView {

	private EventHandler<NodeEvent> pointTypeHandler;

	private EventHandler<NodeEvent> pointSizeHandler;

	public DesignPointView( DesignPane pane, DesignPoint point ) {
		super( pane, point );
	}

	@Override
	public void registerListeners() {
		super.registerListeners();
		getDesignShape().register( DesignPoint.TYPE, pointTypeHandler = e -> Fx.run( this::updateGeometry ) );
		getDesignShape().register( DesignPoint.SIZE, pointSizeHandler = e -> Fx.run( this::updateGeometry ) );
	}

	@Override
	public void unregisterListeners() {
		getDesignShape().unregister( DesignPoint.SIZE, pointSizeHandler );
		getDesignShape().unregister( DesignPoint.TYPE, pointTypeHandler );
		super.unregisterListeners();
	}

}
