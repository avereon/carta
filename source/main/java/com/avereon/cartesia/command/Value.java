package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandTask;
import lombok.CustomLog;

@CustomLog
public class Value extends Command {

	public Value() {}

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandTask task ) throws Exception {
		if( task.getParameterCount() == 0 ) throw new InvalidInputException( this, "value", null );
		return task.getParameters();
	}

}
