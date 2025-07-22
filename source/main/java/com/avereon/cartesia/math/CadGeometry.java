package com.avereon.cartesia.math;

import com.avereon.cartesia.data.*;
import com.avereon.curve.math.Geometry;
import com.avereon.curve.math.Vector;
import com.avereon.zerra.font.FontUtil;
import com.avereon.zerra.javafx.FxUtil;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

	public static double theta360( Point2D a, Point2D b ) {
		return Math.toDegrees( Geometry.theta( asPoint( a ), asPoint( b ) ) );
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

	public static List<Point3D> rotate360( Point3D axis, double angle, Point3D... points ) {
		return Arrays.stream( points ).map( p -> rotate360( axis, p, angle ) ).toList();
	}

	public static List<Point3D> rotate360( Point3D axis, double angle, List<Point3D> points ) {
		return points.stream().map( p -> rotate360( axis, p, angle ) ).toList();
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

	public static Bounds ellipseBounds( DesignEllipse ellipse ) {
		double[][] b = Geometry.ellipseBounds( asPoint( ellipse.getOrigin() ), asPoint( ellipse.getRadii() ), Math.toRadians( ellipse.calcRotate() ) );
		return FxUtil.bounds( CadPoints.toFxPoint( b[ 0 ] ), CadPoints.toFxPoint( b[ 1 ] ) );
	}

	public static Bounds arcBounds( DesignArc arc ) {
		double[][] b = Geometry.arcBounds(
			asPoint( arc.getOrigin() ),
			asPoint( arc.getRadii() ),
			Math.toRadians( arc.calcRotate() ),
			Math.toRadians( arc.calcStart() ),
			Math.toRadians( arc.calcExtent() )
		);
		return FxUtil.bounds( CadPoints.toFxPoint( b[ 0 ] ), CadPoints.toFxPoint( b[ 1 ] ) );
	}

	public static double arcLength( DesignArc arc ) {
		return Geometry.arcLength( asPoint( arc.getOrigin() ), asPoint( arc.getRadii() ), Math.toRadians( arc.calcRotate() ), Math.toRadians( arc.calcStart() ), Math.toRadians( arc.calcExtent() ) );
	}

	/**
	 * Convert an arc definition from endpoint format to center format.
	 * <p>
	 * NOTE: Angles values are expected in degrees and returned in degrees
	 * <p>
	 * Derived from <a href="https://www.w3.org/TR/SVG11/implnote.html#ArcConversionEndpointToCenter">https://www.w3.org/TR/SVG11/implnote.html#ArcConversionEndpointToCenter</a>
	 *
	 * @param p1 The start point of the arc
	 * @param data The remaining data for the arc in the format [x, y, rx, ry, rotate, largeArc, sweep]
	 * @return The center format of the arc in the format [cx, cy, rx, ry, startAngle, deltaAngle]
	 */
	public static double[] arcEndpointToCenter( double[] p1, double[] data ) {
		double[] endpointData = new double[]{ p1[ 0 ], p1[ 1 ], data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ], Math.toRadians( data[ 4 ] ), data[ 5 ], data[ 6 ] };
		double[] centerData = Geometry.arcEndpointToCenter( p1, endpointData );
		return new double[]{ centerData[ 0 ], centerData[ 1 ], centerData[ 2 ], centerData[ 3 ], Math.toDegrees( centerData[ 4 ] ), Math.toDegrees( centerData[ 5 ] ) };
	}

	public static List<Point3D> arcReferencePoints( DesignArc arc ) {
		return CadPoints.toFxPoints( Geometry.arcReferencePoints(
			asPoint( arc.getOrigin() ),
			asPoint( arc.getRadii() ),
			Math.toRadians( arc.calcRotate() ),
			Math.toRadians( arc.calcStart() ),
			Math.toRadians( arc.calcExtent() )
		) );
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
		return Geometry.cubicArcLength(
			asPoint( cubic.getOrigin() ),
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
		return FxUtil.bounds( a, b );
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
				yield new Ellipse( ellipse.getOrigin().getX(), ellipse.getOrigin().getY(), ellipse.getXRadius(), ellipse.getYRadius() );
			}
			case ARC -> {
				DesignArc arc = (DesignArc)shape;
				yield new Arc( arc.getOrigin().getX(), arc.getOrigin().getY(), arc.getXRadius(), arc.getYRadius(), -arc.calcStart(), -arc.calcExtent() );
			}
			case QUAD -> {
				DesignQuad quad = (DesignQuad)shape;
				yield new QuadCurve( quad.getOrigin().getX(), quad.getOrigin().getY(), quad.getControl().getX(), quad.getControl().getY(), quad.getPoint().getX(), quad.getPoint().getY() );
			}
			case CUBIC -> {
				DesignCubic cubic = (DesignCubic)shape;
				yield new CubicCurve(
					cubic.getOrigin().getX(),
					cubic.getOrigin().getY(),
					cubic.getOriginControl().getX(),
					cubic.getOriginControl().getY(),
					cubic.getPointControl().getX(),
					cubic.getPointControl().getY(),
					cubic.getPoint().getX(),
					cubic.getPoint().getY()
				);
			}
			case PATH -> {
				DesignPath path = (DesignPath)shape;
				yield mapToFxPath( path.getSteps() );
			}
			case MARKER -> {
				DesignMarker marker = (DesignMarker)shape;
				Path path = mapToFxPath( marker.getElements() );
				double size = marker.calcSize();
				Transform scale = Transform.scale( size, size, marker.getOrigin().getX(), marker.getOrigin().getY() );
				path.getTransforms().add( scale );
				yield path;
			}
			case TEXT -> {
				DesignText text = (DesignText)shape;
				Text fxText = new Text( (text.getOrigin().getX()), -text.getOrigin().getY(), text.getText() );
				fxText.getTransforms().add( Transform.scale( 1, -1 ) );
				fxText.setFont( FontUtil.derive( text.calcFont(), text.calcTextSize() ) );
				yield fxText;
			}
		};

		// Determine the draw and fill paints
		Paint drawPaint = shape.calcDrawPaint();
		Paint fillPaint = shape.calcFillPaint();
		boolean hasDraw = drawPaint != null && drawPaint != Color.TRANSPARENT;
		boolean hasFill = fillPaint != null && fillPaint != Color.TRANSPARENT;

		fxShape.setStroke( hasDraw ? Color.YELLOW : null );
		fxShape.setFill( hasFill ? Color.RED : null );

		fxShape.setStrokeWidth( shape.calcDrawWidth() );
		fxShape.setStrokeType( StrokeType.CENTERED );
		fxShape.setStrokeLineCap( shape.calcDrawCap() );
		//fxShape.setStrokeLineJoin( shape.calcDrawJoin() );
		//fxShape.setStrokeMiterLimit( shape.calcDrawMiterLimit() );
		//fxShape.setStrokeDashOffset( shape.calcDashOffset() );
		fxShape.getStrokeDashArray().setAll( shape.calcDashPattern() );

		// Handle the rotate transform, if needed
		double rotate = shape.calcRotate();
		if( rotate != 0.0 ) {
			if( Objects.requireNonNull( shape.getType() ) == DesignShape.Type.TEXT ) {
				fxShape.getTransforms().add( Transform.rotate( -rotate, shape.getOrigin().getX(), -shape.getOrigin().getY() ) );
			} else {
				fxShape.getTransforms().add( Transform.rotate( rotate, shape.getOrigin().getX(), shape.getOrigin().getY() ) );
			}
		}

		return fxShape;
	}

	private static Path mapToFxPath( List<DesignPath.Step> steps ) {
		Path fxPath = new Path();
		for( DesignPath.Step step : steps ) {
			switch( step.command() ) {
				case M -> fxPath.getElements().add( new MoveTo( step.data()[ 0 ], step.data()[ 1 ] ) );
				case L -> fxPath.getElements().add( new LineTo( step.data()[ 0 ], step.data()[ 1 ] ) );
				case Q -> fxPath.getElements().add( new QuadCurveTo( step.data()[ 0 ], step.data()[ 1 ], step.data()[ 2 ], step.data()[ 3 ] ) );
				case B -> fxPath.getElements().add( new CubicCurveTo( step.data()[ 0 ], step.data()[ 1 ], step.data()[ 2 ], step.data()[ 3 ], step.data()[ 4 ], step.data()[ 5 ] ) );
				case A -> fxPath.getElements().add( new ArcTo( step.data()[ 2 ], step.data()[ 3 ], step.data()[ 4 ], step.data()[ 0 ], step.data()[ 1 ], step.data()[ 5 ] > 0, step.data()[ 6 ] > 0 ) );
				case Z -> fxPath.getElements().add( new ClosePath() );
			}
		}
		return fxPath;
	}

}
