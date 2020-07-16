package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandException;
import javafx.geometry.Point3D;

public class SnapNearestPoint extends Command<Point3D> {

	@Override
	public void evaluate( Object... parameters ) {
		// Get a point and find the nearest construction? point in the design tool
	}

	@Override
	public Point3D getResult() throws CommandException {
		return new Point3D( 0.0, 0.0, 0.0 );
	}

}
