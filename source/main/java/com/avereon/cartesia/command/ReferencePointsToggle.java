package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandTask;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class ReferencePointsToggle extends Command {

	@Override
	public Object execute( CommandTask task) throws Exception {
		task.getTool().setReferenceLayerVisible( !task.getTool().isReferenceLayerVisible() );
		return SUCCESS;
	}

}
