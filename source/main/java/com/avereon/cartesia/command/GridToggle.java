package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandTask;
import static com.avereon.cartesia.command.Command.Result.*;

public class GridToggle extends Command {

	@Override
	public Object execute( CommandTask task) throws Exception {
		task.getTool().setGridVisible( !task.getTool().isGridVisible() );
		return SUCCESS;
	}

}
