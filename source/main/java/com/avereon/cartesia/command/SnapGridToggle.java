package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;

public class SnapGridToggle extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		DesignTool tool = context.getTool();
		tool.setGridSnapEnabled( !tool.isGridSnapEnabled() );
		return COMPLETE;
	}

}
