package com.avereon.cartesia.command.snap;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.BaseDesignTool;

public class SnapGridToggle extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		BaseDesignTool tool = context.getTool();
		tool.setGridSnapEnabled( !tool.isGridSnapEnabled() );
		return COMPLETE;
	}

}
