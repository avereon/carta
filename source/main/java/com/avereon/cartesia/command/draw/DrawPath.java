package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.tool.CommandContext;

public class DrawPath extends DrawCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		// This one will be fun because it can be very powerful
		// Once started it can take SVG-like parameters to draw a path
		// Of particular interest is that it can be open or closed

		return COMPLETE;
	}

}
