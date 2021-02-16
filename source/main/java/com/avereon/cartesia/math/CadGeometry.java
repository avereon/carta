package com.avereon.cartesia.math;

import com.avereon.curve.math.Geometry;
import javafx.geometry.Point3D;

public class CadGeometry {

	public static double distance( Point3D a, Point3D b ) {
		return Geometry.distance( CadPoints.asPoint( a ), CadPoints.asPoint( b ) );
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

	public static boolean areCollinear( Point3D a, Point3D b, Point3D c ) {
		return Geometry.areCollinear( CadPoints.asPoint( a ), CadPoints.asPoint( b ), CadPoints.asPoint( c ) );
	}

}
