package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignCurve;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.curve.math.Geometry;
import com.avereon.curve.math.Vector;
import javafx.geometry.Point3D;

import java.util.Arrays;
import java.util.List;

import static com.avereon.cartesia.math.CadPoints.asPoint;
import static com.avereon.cartesia.math.CadPoints.toFxPoint;

public class CadGeometry {

	public static double angle360( Point3D a ) {
		return Math.toDegrees( Geometry.getAngle( asPoint( a ) ) );
	}

	public static double angle360( Point3D a, Point3D b ) {
		return Math.toDegrees( Geometry.getAngle( asPoint( a ), asPoint( b ) ) );
	}

	public static double normalizeAngle360( double angle ) {
		return Math.toDegrees( Math.toRadians( angle ) );
	}

	public static Point3D rotate360( Point3D point, double angle ) {
		return CadPoints.toFxPoint( Vector.rotate( asPoint( point ), Math.toRadians( angle ) ) );
	}

	public static double distance( Point3D a, Point3D b ) {
		return Geometry.distance( asPoint( a ), asPoint( b ) );
	}

	public static Point3D midpoint( Point3D a, Point3D b ) {
		return CadPoints.toFxPoint( Geometry.midpoint( asPoint( a ), asPoint( b ) ) );
	}

	public static Point3D midpoint( Point3D origin, double xRadius, double yRadius, double rotate, double start, double extent ) {
		return CadPoints.toFxPoint( Geometry.midpoint( asPoint( origin ), xRadius, yRadius, rotate, start, extent ) );
	}

	/**
	 * Get the distance between a point and a line.
	 *
	 * @param p The point
	 * @param a The first point on the line
	 * @param b The other point on the line
	 * @return The distance between the point and the line
	 */
	public static double pointLineDistance( Point3D p, Point3D a, Point3D b ) {
		return Geometry.pointLineDistance( asPoint( a ), asPoint( b ), asPoint( p ) );
	}

	/**
	 * Get the distance between a line and a point.
	 *
	 * @param a The first point on the line
	 * @param b The other point on the line
	 * @param p The point
	 * @return The distance between the line and the point
	 */
	public static double linePointDistance( Point3D a, Point3D b, Point3D p ) {
		return Geometry.pointLineDistance( asPoint( a ), asPoint( b ), asPoint( p ) );
	}

	public static double lineLineDistance( Point3D a, Point3D b, Point3D c, Point3D d ) {
		return Geometry.lineLineDistance( asPoint( a ), asPoint( b ), asPoint( c ), asPoint( d ) );
	}

	public static double lineLineAngle( Point3D a, Point3D b, Point3D c, Point3D d ) {
		return Geometry.lineLineAngle( asPoint( a ), asPoint( b ), asPoint( c ), asPoint( d ) );
	}

	public static Point3D ellipsePoint360( DesignEllipse ellipse, double angle ) {
		return ellipsePoint360( ellipse.getOrigin(), ellipse.getXRadius(), ellipse.getYRadius(), ellipse.calcRotate(), angle );
	}

	public static Point3D ellipsePoint360( Point3D o, double xRadius, double yRadius, double rotate, double angle ) {
		return CadPoints.toFxPoint( Geometry.ellipsePoint( asPoint( o ), xRadius, yRadius, Math.toRadians( rotate ), Math.toRadians( angle ) ) );
	}

	public static double ellipseAngle360( DesignEllipse ellipse, Point3D point ) {
		return ellipseAngle360( ellipse.getOrigin(), ellipse.getXRadius(), ellipse.getYRadius(), ellipse.calcRotate(), point );
	}

	public static double ellipseAngle360( Point3D o, double xRadius, double yRadius, double rotate, Point3D point ) {
		return Math.toDegrees( Geometry.ellipseAngle( asPoint( o ), xRadius, yRadius, Math.toRadians( rotate ), asPoint( point ) ) );
	}

	public static double getCurveParametricValue( DesignCurve curve, Point3D point ) {
		return Geometry.curveParametricValue( asPoint( curve.getOrigin() ), asPoint( curve.getOriginControl() ), asPoint( curve.getPointControl() ), asPoint( curve.getPoint() ), asPoint( point ) );
	}

	public static List<DesignCurve> curveSubdivide( DesignCurve curve, double t ) {
		double[][][] curves = Geometry.curveSubdivide( asPoint( curve.getOrigin() ), asPoint( curve.getOriginControl() ), asPoint( curve.getPointControl() ), asPoint( curve.getPoint() ), t );
		if( curves.length < 2 ) return List.of();

		double[][] v0 = curves[ 0 ];
		double[][] v1 = curves[ 1 ];
		DesignCurve c0 = new DesignCurve( toFxPoint( v0[ 0 ] ), toFxPoint( v0[ 1 ] ), toFxPoint( v0[ 2 ] ), toFxPoint( v0[ 3 ] ) );
		DesignCurve c1 = new DesignCurve( toFxPoint( v1[ 0 ] ), toFxPoint( v1[ 1 ] ), toFxPoint( v1[ 2 ] ), toFxPoint( v1[ 3 ] ) );

		return List.of( c0, c1 );
	}

	public static double getSpin( Point3D a, Point3D b, Point3D c ) {
		return Geometry.getSpin( asPoint( a ), asPoint( b ), asPoint( c ) );
	}

	public static Point3D getNormal( Point3D a, Point3D b, Point3D c ) {
		return CadPoints.toFxPoint( Geometry.getNormal( asPoint( a ), asPoint( b ), asPoint( c ) ) );
	}

	public static boolean areSamePoint( Point3D a, Point3D b ) {
		return Geometry.areSamePoint( asPoint( a ), asPoint( b ) );
	}

	public static boolean areCollinear( Point3D a, Point3D b, Point3D c ) {
		return Geometry.areCollinear( asPoint( a ), asPoint( b ), asPoint( c ) );
	}

	public static boolean areCoplanar( CadOrientation orientation, Point3D... points ) {
		double[][] ps = Arrays.stream( points ).map( CadPoints::asPoint ).toArray( double[][]::new );
		return Geometry.areCoplanar( asPoint( orientation.getOrigin() ), asPoint( orientation.getNormal() ), ps );
	}

	public static Point3D polarToCartesian( Point3D polar ) {
		return CadPoints.toFxPoint( Geometry.polarToCartesian( asPoint( polar ) ) );
	}

	public static Point3D polarToCartesian360( Point3D polar ) {
		return CadPoints.toFxPoint( Geometry.polarDegreesToCartesian( asPoint( polar ) ) );
	}

	public static Point3D cartesianToPolar( Point3D point ) {
		return CadPoints.toFxPoint( Geometry.cartesianToPolar( asPoint( point ) ) );
	}

	public static Point3D cartesianToPolar360( Point3D point ) {
		return CadPoints.toFxPoint( Geometry.cartesianToPolarDegrees( asPoint( point ) ) );
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

		// This will be the smaller angle
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
