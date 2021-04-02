package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.CommandContext;

public class LayerDelete extends LayerCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		DesignLayer layer = context.getTool().getCurrentLayer();
		layer.getLayer().removeLayer( layer );
		return COMPLETE;
	}

}
