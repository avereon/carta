package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.CommandContext;

public class ViewUpdate extends ViewCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		DesignView view = context.getTool().getCurrentView();
		if( view == null ) return COMPLETE;

		view.setOrigin( context.getTool().getViewPoint() );
		view.setZoom( context.getTool().getZoom() );
		view.setRotate( context.getTool().getViewRotate() );
		view.setLayers( context.getTool().getVisibleLayers() );

		return COMPLETE;
	}

}
