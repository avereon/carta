package com.avereon.cartesia.command.base;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.InvalidInputException;
import lombok.CustomLog;

@CustomLog
public class Value extends Command {

	public Value() {}

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public boolean clearReferenceAndPreviewWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandTask task ) throws Exception {
		if( task.getParameterCount() == 0 ) throw new InvalidInputException( this, "value", null );
		return task.getParameters();
	}

}
