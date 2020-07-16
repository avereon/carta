package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandException;
import javafx.geometry.Point3D;

import java.util.Objects;

public class PointCommand extends Command<Point3D> {

	@Override
	public void evaluate( Object... parameters ) {
		String expression = "";
		if( parameters.length > 0 ) expression = Objects.requireNonNullElse( String.valueOf( parameters[0] ), expression );

		// TODO Parse the point expression
	}

	@Override
	public Point3D getResult() throws CommandException {
		return null;
	}

}
