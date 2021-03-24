package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;

public class LayerDelete extends LayerCommand {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
		DesignLayer layer = tool.getCurrentLayer();
		layer.getLayer().removeLayer( layer );
		return COMPLETE;
	}

}
