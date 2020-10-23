package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;

public class ArcCommand extends DrawCommand {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		return complete();
	}

}
