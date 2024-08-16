package com.avereon.cartesia.command.snap;

import com.avereon.cartesia.command.ToggleCommand;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.tool.DesignTool;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class SnapGridToggle extends ToggleCommand {

	@Override
	public Object execute( CommandTask task) throws Exception {
		DesignTool tool = task.getTool();
		tool.setGridSnapEnabled( !tool.isGridSnapEnabled() );
		return SUCCESS;
	}

}
