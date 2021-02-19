package com.avereon.cartesia.math;

import com.avereon.curve.math.Geometry;
import javafx.geometry.Point3D;

import java.util.Arrays;

public class CadGeometry {

	public static double angle360( Point3D a ) {
		return Math.toDegrees( Geometry.getAngle( CadPoints.asPoint( a ) ) );
	}

	public static double angle360( Point3D a, Point3D b ) {
		return Math.toDegrees( Geometry.getAngle( CadPoints.asPoint( a ), CadPoints.asPoint( b ) ) );
	}

	public static double distance( Point3D a, Point3D b ) {
		return Geometry.distance( CadPoints.asPoint( a ), CadPoints.asPoint( b ) );
	}

	public static Point3D midpoint( Point3D a, Point3D b ) {
		return CadPoints.toFxPoint( Geometry.midpoint( CadPoints.asPoint( a ), CadPoints.asPoint( b ) ) );
	}

	public static Point3D midpoint( Point3D origin, double xRadius, double yRadius, double rotate, double start, double extent ) {
		return CadPoints.toFxPoint( Geometry.midpoint( CadPoints.asPoint( origin ), xRadius, yRadius, rotate, start, extent ) );
	}

	public static double lineLineDistance( Point3D a, Point3D b, Point3D c, Point3D d ) {
		return Geometry.lineLineDistance( CadPoints.asPoint( a ), CadPoints.asPoint( b ), CadPoints.asPoint( c ), CadPoints.asPoint( d ) );
	}

	public static double lineLineAngle( Point3D a, Point3D b, Point3D c, Point3D d ) {
		return Geometry.lineLineAngle( CadPoints.asPoint( a ), CadPoints.asPoint( b ), CadPoints.asPoint( c ), CadPoints.asPoint( d ) );
	}

	public static Point3D getNormal( Point3D a, Point3D b, Point3D c ) {
		return CadPoints.toFxPoint( Geometry.getNormal( CadPoints.asPoint( a ), CadPoints.asPoint( b ), CadPoints.asPoint( c ) ) );
	}

	public static boolean areSamePoint( Point3D a, Point3D b ) {
		return Geometry.areSamePoint( CadPoints.asPoint( a ), CadPoints.asPoint( b ) );
	}

	public static boolean areCollinear( Point3D a, Point3D b, Point3D c ) {
		return Geometry.areCollinear( CadPoints.asPoint( a ), CadPoints.asPoint( b ), CadPoints.asPoint( c ) );
	}

	public static boolean areCoplanar( CadOrientation orientation, Point3D... points ) {
		double[][] ps = Arrays.stream( points ).map( CadPoints::asPoint ).toArray( double[][]::new );
		return Geometry.areCoplanar( CadPoints.asPoint( orientation.getOrigin() ), CadPoints.asPoint( orientation.getNormal() ), ps );
	}

	public static Point3D polarToCartesian( Point3D polar ) {
		return CadPoints.toFxPoint( Geometry.polarToCartesian( CadPoints.asPoint( polar ) ) );
	}

	public static Point3D polarToCartesian360( Point3D polar ) {
		return CadPoints.toFxPoint( Geometry.polarDegreesToCartesian( CadPoints.asPoint( polar ) ) );
	}

	public static Point3D cartesianToPolar( Point3D point ) {
		return CadPoints.toFxPoint( Geometry.cartesianToPolar( CadPoints.asPoint( point ) ) );
	}

	public static Point3D cartesianToPolar360( Point3D point ) {
		return CadPoints.toFxPoint( Geometry.cartesianToPolarDegrees( CadPoints.asPoint( point ) ) );
	}

}
