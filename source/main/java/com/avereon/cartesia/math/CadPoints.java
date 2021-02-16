package com.avereon.cartesia.math;

import com.avereon.curve.math.Geometry;
import com.avereon.curve.math.Point;
import com.avereon.curve.math.Vector;
import javafx.geometry.Point3D;

public class CadPoints {

	public static final Point3D UNIT_X = CadPoints.toFxPoint( Vector.UNIT_X );

	public static final Point3D UNIT_Y = CadPoints.toFxPoint( Vector.UNIT_Y );

	public static final Point3D UNIT_Z = CadPoints.toFxPoint( Vector.UNIT_Z );

	public static double[] asPoint( Point3D point ) {
		return Point.of( point.getX(), point.getY(), point.getZ() );
	}

	public static Point3D toFxPoint( double[] point ) {
		return new Point3D( point[ 0 ], point[ 1 ], point[ 2 ] );
	}

	static Point3D fromPolar( Point3D point ) {
		return toFxPoint( Geometry.polarToCartesian( Point.of( point.getX(), point.getY() ) ) );
	}

}
