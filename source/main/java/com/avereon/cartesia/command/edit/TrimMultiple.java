package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.command.CommandTask;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

/**
 * Select a shape to be a trim edge, then select multiple shapes to trim.
 * Press ESC to stop.
 */
public class TrimMultiple extends EditCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		return SUCCESS;
	}

}
