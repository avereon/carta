package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.DesignTool;

public class PathCommand extends Command {

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		// This one will be fun because it can be very powerful
		// Once started it can take SVG-like parameters to draw a path
		// Of particular interest is that it can be open or closed
	}

}
