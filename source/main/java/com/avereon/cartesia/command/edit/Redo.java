package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.command.CommandTask;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class Redo extends EditCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		task.getContext().getTool().getResource().getUndoManager().redo();
		return SUCCESS;
	}

}
