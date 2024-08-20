package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.command.CommandTask;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class Undo extends EditCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		task.getContext().getTool().getAsset().getUndoManager().undo();
		return SUCCESS;
	}

}

