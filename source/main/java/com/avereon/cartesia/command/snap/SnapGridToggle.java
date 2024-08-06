package com.avereon.cartesia.command.snap;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.tool.CommandTask;
import com.avereon.cartesia.tool.DesignTool;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class SnapGridToggle extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandTask task) throws Exception {
		DesignTool tool = task.getTool();
		tool.setGridSnapEnabled( !tool.isGridSnapEnabled() );
		return SUCCESS;
	}

}
