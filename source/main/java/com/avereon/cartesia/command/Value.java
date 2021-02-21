package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;

public class Value extends Command {

	public Value() {}

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
		return parameters[0];
	}

}
