package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;

public class GridToggle extends Command {
	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		context.getTool().setGridVisible( !context.getTool().isGridVisible() );
		return COMPLETE;
	}

}
