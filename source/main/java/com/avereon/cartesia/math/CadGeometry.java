package com.avereon.cartesia.math;

import com.avereon.cartesia.data.*;
import com.avereon.curve.math.Geometry;
import com.avereon.curve.math.Vector;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

import java.util.Arrays;
import java.util.List;

import static com.avereon.cartesia.math.CadPoints.asPoint;
import static com.avereon.cartesia.math.CadPoints.toFxPoint;

@CustomLog
public class CadGeometry {

	public static double angle360( Point3D a ) {
		return Math.toDegrees( Geometry.getAngle( asPoint( a ) ) );
	}

	public static double angle360( Point3D a, Point3D b ) {
		return Math.toDegrees( Geometry.getAngle( asPoint( a ), asPoint( b ) ) );
	}

	public static double pointAngle360( Point3D a, Point3D b, Point3D c ) {
		return Math.toDegrees( Geometry.pointAngle( asPoint( a ), asPoint( b ), asPoint( c ) ) );
	}

	public static double clampAngle360( double angle ) {
		angle %= 360;
		if( angle < -180 ) angle += 360;
		if( angle > 180 ) angle -= 360;
		return angle;
	}

	public static double normalizeAngle360( double angle ) {
		return angle % 360;
	}

	public static Point3D rotate360( Point3D point, double angle ) {
		return toFxPoint( Vector.rotate( asPoint( point ), Math.toRadians( angle ) ) );
	}

	public static Point3D rotate360( Point3D axis, Point3D point, double angle ) {
		return toFxPoint( Vector.rotate( asPoint( axis ), asPoint( point ), Math.toRadians( angle ) ) );
	}

	public static double distance( Point3D a, Point3D b ) {
		return Geometry.distance( asPoint( a ), asPoint( b ) );
	}

	public static Point3D midpoint( Point3D a, Point3D b ) {
		return toFxPoint( Geometry.midpoint( asPoint( a ), asPoint( b ) ) );
	}

	public static Point3D midpoint( Point3D origin, double xRadius, double yRadius, double rotate, double start, double extent ) {
		return toFxPoint( Geometry.midpoint( asPoint( origin ), xRadius, yRadius, rotate, start, extent ) );
	}

	public static Point3D nearestLinePoint( Point3D a, Point3D b, Point3D p ) {
		return toFxPoint( Geometry.nearestLinePoint( asPoint( a ), asPoint( b ), asPoint( p ) ) );
	}

	public static Point3D nearestBoundLinePoint( Point3D a, Point3D b, Point3D p ) {
		return toFxPoint( Geometry.nearestBoundLinePoint( asPoint( a ), asPoint( b ), asPoint( p ) ) );
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
		return Geometry.linePointDistance( asPoint( a ), asPoint( b ), asPoint( p ) );
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
		return Geometry.linePointDistance( asPoint( a ), asPoint( b ), asPoint( p ) );
	}

	public static double lineLineDistance( Point3D a, Point3D b, Point3D c, Point3D d ) {
		return Geometry.lineLineDistance( asPoint( a ), asPoint( b ), asPoint( c ), asPoint( d ) );
	}

	public static double lineLineAngle( Point3D a, Point3D b, Point3D c, Point3D d ) {
		return Geometry.lineLineAngle( asPoint( a ), asPoint( b ), asPoint( c ), asPoint( d ) );
	}

	public static Point3D ellipsePoint360( DesignEllipse ellipse, double angle ) {
		return ellipsePoint360( ellipse.getOrigin(), ellipse.getRadii(), ellipse.calcRotate(), angle );
	}

	/**
	 * @deprecated Use {@link #ellipsePoint360(Point3D, Point3D, double, double)} instead
	 */
	@Deprecated
	public static Point3D ellipsePoint360( Point3D o, double xRadius, double yRadius, double rotate, double angle ) {
		return ellipsePoint360( o, new Point3D( xRadius, yRadius, 0 ), rotate, angle );
	}

	public static Point3D ellipsePoint360( Point3D o, Point3D radii, double rotate, double angle ) {
		return toFxPoint( Geometry.ellipsePoint( asPoint( o ), asPoint( radii ), Math.toRadians( rotate ), Math.toRadians( angle ) ) );
	}

	public static double ellipseAngle360( DesignEllipse ellipse, Point3D point ) {
		return ellipseAngle360( ellipse.getOrigin(), ellipse.getXRadius(), ellipse.getYRadius(), ellipse.calcRotate(), point );
	}

	public static double ellipseAngle360( Point3D o, double xRadius, double yRadius, double rotate, Point3D point ) {
		return Math.toDegrees( Geometry.ellipseAngle( asPoint( o ), xRadius, yRadius, Math.toRadians( rotate ), asPoint( point ) ) );
	}

	public static double arcLength( DesignArc arc ) {
		return Geometry.arcLength( asPoint( arc.getOrigin() ), asPoint( arc.getRadii() ), Math.toRadians( arc.calcRotate() ), Math.toRadians( arc.calcStart() ), Math.toRadians( arc.calcExtent() ) );
	}

	public static double quadArcLength( DesignQuad quad ) {
		return Geometry.quadArcLength( asPoint( quad.getOrigin() ), asPoint( quad.getControl() ), asPoint( quad.getPoint() ), CadConstants.RESOLUTION_LENGTH );
	}

	public static double getCubicParametricValue( DesignCubic curve, Point3D point ) {
		return Geometry.curveParametricValue( asPoint( curve.getOrigin() ), asPoint( curve.getOriginControl() ), asPoint( curve.getPointControl() ), asPoint( curve.getPoint() ), asPoint( point ) );
	}

	public static double getCubicParametricValueNear( DesignCubic curve, Point3D point ) {
		return Geometry.curveParametricValueNear( asPoint( curve.getOrigin() ), asPoint( curve.getOriginControl() ), asPoint( curve.getPointControl() ), asPoint( curve.getPoint() ), asPoint( point ) );
	}

	public static List<DesignCubic> cubicSubdivide( DesignCubic curve, double t ) {
		double[][][] curves = Geometry.curveSubdivide( asPoint( curve.getOrigin() ), asPoint( curve.getOriginControl() ), asPoint( curve.getPointControl() ), asPoint( curve.getPoint() ), t );
		if( curves.length < 2 ) return List.of();

		double[][] v0 = curves[ 0 ];
		double[][] v1 = curves[ 1 ];
		DesignCubic c0 = new DesignCubic( toFxPoint( v0[ 0 ] ), toFxPoint( v0[ 1 ] ), toFxPoint( v0[ 2 ] ), toFxPoint( v0[ 3 ] ) );
		DesignCubic c1 = new DesignCubic( toFxPoint( v1[ 0 ] ), toFxPoint( v1[ 1 ] ), toFxPoint( v1[ 2 ] ), toFxPoint( v1[ 3 ] ) );

		return List.of( c0, c1 );
	}

	public static double cubicArcLength( DesignCubic cubic ) {
		return Geometry.cubicArcLength( asPoint( cubic.getOrigin() ),
			asPoint( cubic.getOriginControl() ),
			asPoint( cubic.getPointControl() ),
			asPoint( cubic.getPoint() ),
			CadConstants.RESOLUTION_LENGTH
		);
	}

	public static double getSpin( Point3D a, Point3D b, Point3D c ) {
		return Geometry.getSpin( asPoint( a ), asPoint( b ), asPoint( c ) );
	}

	public static Point3D getNormal( Point3D a, Point3D b, Point3D c ) {
		return toFxPoint( Geometry.getNormal( asPoint( a ), asPoint( b ), asPoint( c ) ) );
	}

	public static boolean areSameSize( double a, double b ) {
		return Geometry.areSameSize( a, b );
	}

	public static boolean areSameAngle360( double a, double b ) {
		return Geometry.areSameAngle( Math.toRadians( a ), Math.toRadians( b ) );
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
		return toFxPoint( Geometry.polarToCartesian( asPoint( polar ) ) );
	}

	public static Point3D polarToCartesian360( Point3D polar ) {
		return toFxPoint( Geometry.polarDegreesToCartesian( asPoint( polar ) ) );
	}

	public static Point3D cartesianToPolar( Point3D point ) {
		return toFxPoint( Geometry.cartesianToPolar( asPoint( point ) ) );
	}

	public static Point3D cartesianToPolar360( Point3D point ) {
		return toFxPoint( Geometry.cartesianToPolarDegrees( asPoint( point ) ) );
	}

	public static Bounds getBounds( Point3D a, Point3D b ) {
		double x = Math.min( a.getX(), b.getX() );
		double y = Math.min( a.getY(), b.getY() );
		double w = Math.abs( a.getX() - b.getX() );
		double h = Math.abs( a.getY() - b.getY() );
		return new BoundingBox( x, y, w, h );
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

		Point3D origin = xns.getFirst();
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
		if( xns.isEmpty() ) xns = List.of( end );

		Point3D origin = xns.get( 0 );
		double radius = origin.distance( mid );
		double startAngle = CadGeometry.angle360( start.subtract( origin ) );
		double spin = getSpin( start, mid, end );

		double extent = CadGeometry.angle360( start.subtract( origin ), end.subtract( origin ) );
		double angle = Math.abs( extent );
		double sweep = Math.signum( extent );

		// If spin and sweep are in the same direction but the angle is small...add to the angle
		if( spin > 0 && sweep < 0 && angle < 180 ) extent = sweep * (360 - angle);
		if( spin < 0 && sweep > 0 && angle < 180 ) extent = sweep * (360 - angle);

		// If spin and sweep are not in the same direction invert the extent
		if( spin != sweep ) extent *= -1;

		return new DesignArc( origin, radius, startAngle, extent, DesignArc.Type.OPEN );
	}

	public static Shape toFxShape( DesignShape shape ) {
		Shape fxShape = switch( shape.getType() ) {
			case BOX -> {
				DesignBox box = (DesignBox)shape;
				yield new Rectangle( box.getOrigin().getX(), box.getOrigin().getY(), box.getSize().getX(), box.getSize().getY() );
			}
			case LINE -> {
				DesignLine line = (DesignLine)shape;
				yield new Line( line.getOrigin().getX(), line.getOrigin().getY(), line.getPoint().getX(), line.getPoint().getY() );
			}
			case ELLIPSE -> {
				DesignEllipse ellipse = (DesignEllipse)shape;
				Ellipse fxEllipse = new Ellipse( ellipse.getOrigin().getX(), ellipse.getOrigin().getY(), ellipse.getXRadius(), ellipse.getYRadius() );
				if( ellipse.calcRotate() != 0.0 ) fxEllipse.getTransforms().add( Transform.rotate( ellipse.calcRotate(), ellipse.getOrigin().getX(), ellipse.getOrigin().getY() ) );
				yield fxEllipse;
			}
			case ARC -> {
				DesignArc arc = (DesignArc)shape;
				yield new Arc( arc.getOrigin().getX(), arc.getOrigin().getY(), arc.getXRadius(), arc.getYRadius(), arc.calcStart(), arc.calcExtent() );
			}
			case QUAD -> {
				DesignQuad quad = (DesignQuad)shape;
				yield new QuadCurve( quad.getOrigin().getX(), quad.getOrigin().getY(), quad.getControl().getX(), quad.getControl().getY(), quad.getPoint().getX(), quad.getPoint().getY() );
			}
			case CUBIC -> {
				DesignCubic cubic = (DesignCubic)shape;
				yield new CubicCurve( cubic.getOrigin().getX(),
					cubic.getOrigin().getY(),
					cubic.getOriginControl().getX(),
					cubic.getOriginControl().getY(),
					cubic.getPointControl().getX(),
					cubic.getPointControl().getY(),
					cubic.getPoint().getX(),
					cubic.getPoint().getY()
				);
			}
			case MARKER -> {
				// TODO This should return a path
				DesignMarker marker = (DesignMarker)shape;
				double size = marker.calcSize();
				yield new Rectangle( marker.getOrigin().getX() - 0.5 * size, marker.getOrigin().getY() - 0.5 * size, size, size );
			}
			case PATH -> {
				DesignPath path = (DesignPath)shape;
				// TODO Calculate path shape
				yield new Rectangle( path.getOrigin().getX(), path.getOrigin().getY(), 1, 1 );
			}
			case TEXT -> {
				DesignText text = (DesignText)shape;
				yield new Text( text.getOrigin().getX(), text.getOrigin().getY(), text.getText() );
			}
		};

		Paint drawPaint = shape.calcDrawPaint();
		Paint fillPaint = shape.calcFillPaint();
		if( drawPaint != null ) fxShape.setStroke( Color.YELLOW );
		if( fillPaint != null ) fxShape.setFill( Color.RED );
		fxShape.setStrokeWidth( shape.calcDrawWidth() );
		fxShape.setStrokeLineCap( shape.calcDrawCap() );
		//fxShape.setStrokeLineJoin( shape.calcDrawJoin() );
		fxShape.getStrokeDashArray().setAll( shape.calcDrawPattern() );
		//fxShape.setStrokeDashOffset( shape.calcDrawDashOffset() );

		// Handle the rotate transform, if needed
		double rotate = shape.calcRotate();
		if( rotate != 0.0) fxShape.getTransforms().add( Transform.rotate( shape.calcRotate(), shape.getOrigin().getX(), shape.getOrigin().getY() ) );

		return fxShape;
	}

}
