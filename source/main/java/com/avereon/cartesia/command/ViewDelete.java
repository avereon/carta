package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;

public class ViewDelete extends ViewCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		return COMPLETE;
	}

}
