package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;

public class LayerShowCommand extends LayerCommand {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
		tool.setLayerVisible( tool.getCurrentLayer(), true );
		return complete();
	}

}
