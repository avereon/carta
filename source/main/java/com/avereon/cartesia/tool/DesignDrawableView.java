package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignDrawable;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;

public class DesignDrawableView extends DesignNodeView {

	private EventHandler<NodeEvent> drawWidthHandler;

	private EventHandler<NodeEvent> drawColorHandler;

	private EventHandler<NodeEvent> fillColorHandler;

	public DesignDrawableView( DesignPane pane, DesignDrawable drawable ) {
		super( pane, drawable );
	}

}
