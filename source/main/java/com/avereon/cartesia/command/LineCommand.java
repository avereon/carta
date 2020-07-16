package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandException;
import javafx.scene.shape.Line;

public class LineCommand extends Command<Line> {

	@Override
	public void evaluate( Object... parameters ) {

	}

	@Override
	public Line getResult() throws CommandException {
		return null;
	}

}
