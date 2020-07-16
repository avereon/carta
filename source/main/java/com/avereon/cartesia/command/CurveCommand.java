package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandException;
import javafx.scene.shape.CubicCurve;

public class CurveCommand extends Command<CubicCurve> {

	@Override
	public void evaluate( Object... parameters ) {

	}

	@Override
	public CubicCurve getResult() throws CommandException {
		return null;
	}

}
