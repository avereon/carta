package com.avereon.cartesia.command.base;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.ToggleCommand;

import static com.avereon.cartesia.command.Command.Result.*;

public class GridToggle extends ToggleCommand {

	@Override
	public Object execute( CommandTask task) throws Exception {
		task.getTool().setGridVisible( !task.getTool().isGridVisible() );
		return SUCCESS;
	}

}
