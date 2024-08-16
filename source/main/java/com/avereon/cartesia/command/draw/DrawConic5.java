package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.CommandTask;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class DrawConic5 extends DrawCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		// NOTE It turns out that five points define a conic, not just an ellipse
		// therefore, implementing this command is left for "later"
		return SUCCESS;
	}

}
