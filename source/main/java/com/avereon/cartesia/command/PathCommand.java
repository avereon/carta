package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandException;
import javafx.scene.shape.Path;

public class PathCommand extends Command<Path> {

	@Override
	public void evaluate( Object... parameters ) {
		// This one will be fun because it can be very powerful
		// Once started it can take SVG-like parameters to draw a path
		// Of particular interest is that it can be open or closed
	}

	@Override
	public Path getResult() throws CommandException {
		return null;
	}

}
