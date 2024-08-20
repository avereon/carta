package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.math.CadGeometry;
import javafx.geometry.Point3D;

import java.util.List;

public abstract class DrawCommand extends Command {

	protected double deriveRotate( Point3D origin, Point3D point ) {
		return CadGeometry.angle360( point.subtract( origin ) );
	}

	protected double deriveXRadius( Point3D origin, Point3D xPoint ) {
		// This is the x-point distance from the origin
		return CadGeometry.distance( origin, xPoint );
	}

	protected double deriveYRadius( Point3D origin, Point3D xPoint, Point3D yPoint ) {
		// This is the y-point distance perpendicular to the origin x-point line
		return CadGeometry.linePointDistance( origin, xPoint, yPoint );
	}

}
