package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.curve.math.Geometry;
import javafx.geometry.Point3D;

import java.util.Arrays;
import java.util.List;

public class CadGeometry {

	public static double angle360( Point3D a ) {
		return Math.toDegrees( Geometry.getAngle( CadPoints.asPoint( a ) ) );
	}

	public static double angle360( Point3D a, Point3D b ) {
		// FIXME Confused about the results of this method
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

	public static Point3D ellipsePoint360( Point3D o, double xRadius, double yRadius, double rotate, double angle ) {
		return CadPoints.toFxPoint( Geometry.ellipsePoint( CadPoints.asPoint( o ), xRadius, yRadius, Math.toRadians( rotate ), Math.toRadians( angle ) ) );
	}

	public static double getSpin( Point3D a, Point3D b, Point3D c ) {
		return Geometry.getSpin( CadPoints.asPoint( a ), CadPoints.asPoint( b ), CadPoints.asPoint( c ) );
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

	public static DesignEllipse circleFromThreePoints( Point3D start, Point3D mid, Point3D end ) {
		Point3D sm = mid.subtract( start );
		Point3D me = end.subtract( mid );
		Point3D u = new Point3D( sm.getY(), -sm.getX(), 0 );
		Point3D v = new Point3D( me.getY(), -me.getX(), 0 );

		Point3D a = start.midpoint( mid );
		Point3D b = a.add( u );
		Point3D c = mid.midpoint( end );
		Point3D d = c.add( v );

		List<Point3D> xns = CadIntersection.intersectLineLine( new DesignLine( a, b ), new DesignLine( c, d ) );
		if( xns.isEmpty() ) return null;

		Point3D origin = xns.get( 0 );
		double radius = origin.distance( mid );

		return new DesignEllipse( origin, radius );
	}

	public static DesignArc arcFromThreePoints( Point3D start, Point3D mid, Point3D end ) {
		Point3D sm = mid.subtract( start );
		Point3D me = end.subtract( mid );
		Point3D u = new Point3D( sm.getY(), -sm.getX(), 0 );
		Point3D v = new Point3D( me.getY(), -me.getX(), 0 );

		Point3D a = start.midpoint( mid );
		Point3D b = a.add( u );
		Point3D c = mid.midpoint( end );
		Point3D d = c.add( v );

		List<Point3D> xns = CadIntersection.intersectLineLine( new DesignLine( a, b ), new DesignLine( c, d ) );
		if( xns.isEmpty() ) return null;

		Point3D origin = xns.get( 0 );
		double radius = origin.distance( mid );
		double startAngle = CadGeometry.angle360( start.subtract( origin ) );
		double spin = getSpin( start, mid, end );

		// This will be the smaller angle with the sign in the correct direction
		double extent = CadGeometry.angle360( start.subtract( origin ), end.subtract( origin ) );
		double angle = Math.abs( extent );
		double sweep = Math.signum( extent );

		// If spin and sweep are in the same direction but the angle is small...add to the angle
		if( spin > 0 && sweep > 0 && angle < 180 ) extent = sweep * (360 - angle);
		if( spin < 0 && sweep < 0 && angle < 180 ) extent = sweep * (360 - angle);

		// If spin and sweep are not in the same direction invert the extent
		if( spin != sweep ) extent *= -1;

		return new DesignArc( origin, radius, startAngle, extent, DesignArc.Type.OPEN );
	}

}
