package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandTask;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.INVALID;

@CustomLog
public class Value extends Command {

	public Value() {}

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandTask task ) throws Exception {
		if( task.getParameters().length < 1 ) return INVALID;
		return task.getParameter( 0 );
	}

}
