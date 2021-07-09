package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import lombok.CustomLog;

@CustomLog
public class Value extends Command {

	public Value() {}

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandContext context, Object... parameters ) {
		return parameters[0];
	}

}
