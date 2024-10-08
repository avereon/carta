package com.avereon.cartesia.command;

import static com.avereon.cartesia.command.Command.Result.*;

public class GridToggle extends ToggleCommand {

	@Override
	public Object execute( CommandTask task) throws Exception {
		task.getTool().setGridVisible( !task.getTool().isGridVisible() );
		return SUCCESS;
	}

}
