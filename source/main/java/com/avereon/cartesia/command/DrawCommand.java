package com.avereon.cartesia.command;

import com.avereon.cartesia.math.CadGeometry;
import javafx.geometry.Point3D;

public abstract class DrawCommand extends Command {

	protected double deriveRotate( Point3D origin, Point3D point ) {
		return CadGeometry.angle360( point.subtract( origin ) );
	}

	protected double deriveYRadius( Point3D origin, Point3D xPoint, Point3D yPoint ) {
		// This is the origin y-point distance
		//return origin.distance( yPoint );

		// This is the y-point distance perpendicular to the origin x-point line
		return CadGeometry.linePointDistance( origin, xPoint, yPoint );
	}

}
