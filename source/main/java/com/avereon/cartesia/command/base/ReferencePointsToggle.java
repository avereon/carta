package com.avereon.cartesia.command.base;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.CommandTask;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class ReferencePointsToggle extends Command {

	@Override
	public Object execute( CommandTask task) throws Exception {
		task.getTool().setReferenceLayerVisible( !task.getTool().isReferenceLayerVisible() );
		return SUCCESS;
	}

}
