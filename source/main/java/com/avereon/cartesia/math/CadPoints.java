package com.avereon.cartesia.math;

import com.avereon.curve.math.Geometry;
import com.avereon.curve.math.Point;
import com.avereon.curve.math.Vector;
import javafx.geometry.Point3D;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CadPoints {

	public static final Point3D NONE = new Point3D( Double.NaN, Double.NaN, Double.NaN );

	public static final Point3D UNIT_X = CadPoints.toFxPoint( Vector.UNIT_X );

	public static final Point3D UNIT_Y = CadPoints.toFxPoint( Vector.UNIT_Y );

	public static final Point3D UNIT_Z = CadPoints.toFxPoint( Vector.UNIT_Z );

	public static double[] asPoint( Point3D point ) {
		return Point.of( point.getX(), point.getY(), point.getZ() );
	}

	public static double[][] asPoints( List<Point3D> points ) {
		if( points == null ) return new double[ 0 ][];
		return points.stream().map( CadPoints::asPoint ).toArray( double[][]::new );
	}

	public static Point3D toFxPoint( double[] point ) {
		return new Point3D( point[ 0 ], point[ 1 ], point[ 2 ] );
	}

	public static List<Point3D> toFxPoints( double[][] points ) {
		if( points == null ) return List.of();
		return Arrays.stream( points ).map( CadPoints::toFxPoint ).collect( Collectors.toList() );
	}

	static Point3D fromPolar( Point3D point ) {
		return toFxPoint( Geometry.polarToCartesian( Point.of( point.getX(), point.getY() ) ) );
	}

}
