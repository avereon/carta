package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;

public class GridToggleCommand extends Command {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
		tool.setGridVisible( !tool.isGridVisible() );
		return COMPLETE;
	}

}
