package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignCurve;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.curve.math.Intersection;
import com.avereon.curve.math.Intersection2D;
import com.avereon.curve.math.Intersection3D;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

import java.util.List;
import java.util.stream.Collectors;

import static com.avereon.cartesia.math.CadPoints.*;

//   | L | A | C | P |
// L | ✓ | ✓ | ✓ |   |
// A | - | ✓ |   |   |
// C | - | - |   |   |
// P | - | - | - |   |

public class CadIntersection {

	private static final System.Logger log = Log.get();

	public static List<Point3D> getIntersections( DesignShape a, DesignShape b ) {
		if( a instanceof DesignLine ) {
			if( b instanceof DesignLine ) {
				return intersectLineLine( (DesignLine)a, (DesignLine)b );
			} else if( b instanceof DesignEllipse ) {
				return intersectLineEllipse( (DesignLine)a, (DesignEllipse)b );
			} else if( b instanceof DesignCurve ) {
				return intersectLineCurve( (DesignLine)a, (DesignCurve)b );
			}
		} else if( a instanceof DesignEllipse ) {
			if( b instanceof DesignLine ) {
				return intersectLineEllipse( (DesignLine)b, (DesignEllipse)a );
			} else if( b instanceof DesignEllipse ) {
				return intersectEllipseEllipse( (DesignEllipse)a, (DesignEllipse)b );
			} else if( b instanceof DesignCurve ) {
				return intersectEllipseCurve( (DesignEllipse)a, (DesignCurve)b );
			}
		} else if( a instanceof DesignCurve ) {
			if( b instanceof DesignLine ) {
				return intersectLineCurve( (DesignLine)b, (DesignCurve)a );
			} else if( b instanceof DesignEllipse ) {
				return intersectEllipseCurve( (DesignEllipse)b, (DesignCurve)a );
			} else if( b instanceof DesignCurve ) {
				return intersectCurveCurve( (DesignCurve)a, (DesignCurve)b );
			}
		}

		return List.of();
	}

	public static List<Point3D> intersectLineLine( DesignLine a, DesignLine b ) {
		Point3D p1 = a.getOrigin();
		Point3D p2 = a.getPoint();
		Point3D p3 = b.getOrigin();
		Point3D p4 = b.getPoint();

		// Check for skew lines
		double skewDistance = CadGeometry.lineLineDistance( p1, p2, p3, p4 );
		if( skewDistance > CadConstants.RESOLUTION_LENGTH ) {
			log.log( Log.WARN, "Lines are too far apart to intersect: " + skewDistance + " > separation limit(" + CadConstants.RESOLUTION_LENGTH + ")" );
			return List.of();
		}

		// Check for parallel lines
		double parallelAngle = CadGeometry.lineLineAngle( p1, p2, p3, p4 );
		if( parallelAngle < CadConstants.RESOLUTION_ANGLE ) {
			log.log( Log.WARN, "Lines are too parallel to intersect: " + parallelAngle + " < separation angle(" + CadConstants.RESOLUTION_ANGLE + ")" );
			return List.of();
		}

		// Check for collinear end points
		CadOrientation orientation = new CadOrientation( new Point3D( p1.getX(), p1.getY(), p1.getZ() ) );
		if( CadGeometry.areCollinear( p1, p2, p3 ) ) {
			orientation.setNormalCorrectRotate( CadGeometry.getNormal( p1, p2, p4 ) );
		} else {
			orientation.setNormalCorrectRotate( CadGeometry.getNormal( p1, p2, p3 ) );
		}

		// Transform all the points to the common plane
		CadTransform transform = orientation.getTargetToLocalTransform();
		p1 = transform.apply( p1 );
		p2 = transform.apply( p2 );
		p3 = transform.apply( p3 );
		p4 = transform.apply( p4 );

		Intersection2D xn = Intersection2D.intersectLineLine( asPoint( p1 ), asPoint( p2 ), asPoint( p3 ), asPoint( p4 ) );
		if( xn.getType() == Intersection.Type.INTERSECTION ) {
			return List.of( orientation.getLocalToTargetTransform().apply( toFxPoint( xn.getPoints()[ 0 ] ) ) );
		}

		return List.of();
	}

	public static List<Point3D> intersectLineEllipse( DesignLine line, DesignEllipse ellipse ) {
		if( line == null || ellipse == null ) return null;

		// If the line and ellipse are not coplanar then the intersection is potentially the line to the plane.
		boolean coplanar = CadGeometry.areCoplanar( ellipse.getOrientation(), line.getOrigin(), line.getPoint() );

		if( !coplanar ) {
			// Use the line-plane intersection method.
			CadOrientation orientation = ellipse.getOrientation();
			List<Point3D> points = intersectLinePlane( line, orientation.getOrigin(), orientation.getNormal() );
			return points.stream().filter( ellipse::isCoincident ).findFirst().stream().collect( Collectors.toList() );
		}

		// FIXME Move this logic to Intersection2D
		// From here to intersectLineCircle() below should be moved
		double rx = ellipse.getXRadius();
		double ry = ellipse.getYRadius();

		CadTransform targetToLocal = CadTransform.scale( 1, rx / ry, 0 ).combine( ellipse.getOrientation().getTargetToLocalTransform() );
		CadTransform localToTarget = ellipse.getOrientation().getLocalToTargetTransform().combine( CadTransform.scale( 1, ry / rx, 0 ) );

		// Transform the line points according to the eccentricity of the ellipse
		Point3D p1 = targetToLocal.apply( line.getOrigin() );
		Point3D p2 = targetToLocal.apply( line.getPoint() );
		List<Point3D> points = intersectLineCircle( new DesignLine( p1, p2 ), ellipse );

		// Transform the intersection points back to the world
		return points.stream().map( localToTarget::apply ).collect( Collectors.toList() );
	}

	public static List<Point3D> intersectLineCurve( DesignLine a, DesignCurve b ) {
		Intersection2D xn = Intersection2D.intersectLineCurve( asPoint( a.getOrigin() ),
			asPoint( a.getPoint() ),
			asPoint( b.getOrigin() ),
			asPoint( b.getOriginControl() ),
			asPoint( b.getPointControl() ),
			asPoint( b.getPoint() )
		);
		return toFxPoints( xn.getPoints() );
	}

	public static List<Point3D> intersectEllipseEllipse( DesignEllipse a, DesignEllipse b ) {
		if( a == null || b == null ) return null;

		// If the line and ellipse are not coplanar then the intersection is potentially the line to the plane.
		// FIXME This coplanar test is not complete
		boolean coplanar = CadGeometry.areCoplanar( a.getOrientation(), b.getOrigin() );

		if( !coplanar ) {
//			// Use the plane-plane intersection method
//			CadOrientation orientationB = a.getOrientation();
//			CadOrientation orientationB = b.getOrientation();
//			double[][] line = intersectPlanePlane( orientationA.getOrigin(), orientationA.getNormal(),orientationB.getOrigin(), orientationB.getNormal() );
//			return line;
		}

		Intersection2D xn = Intersection2D.intersectEllipseEllipse(
			asPoint( a.getOrigin() ),
			a.getXRadius(),
			a.getYRadius(),
			Math.toRadians( a.calcRotate() ),
			asPoint( b.getOrigin() ),
			b.getXRadius(),
			b.getYRadius(),
			Math.toRadians( b.calcRotate() )
		);

		return toFxPoints( xn.getPoints() );
	}

	public static List<Point3D> intersectEllipseCurve( DesignEllipse a, DesignCurve b ) {
		// TODO CadIntersections.intersectEllipseCurve()
		return List.of();
	}

	public static List<Point3D> intersectCurveCurve( DesignCurve a, DesignCurve b ) {
		// TODO CadIntersections.intersectCurveCurve()
		return List.of();
	}

	public static List<Point3D> intersectLinePlane( DesignLine line, Point3D planeOrigin, Point3D planeNormal ) {
		Intersection3D intersection = Intersection3D.intersectionLinePlane( asPoint( line.getOrigin() ), asPoint( line.getPoint() ), asPoint( planeOrigin ), asPoint( planeNormal ) );
		return toFxPoints( intersection.getPoints() );
	}

	private static List<Point3D> intersectLineCircle( DesignLine a, DesignEllipse b ) {
		return toFxPoints( Intersection2D.intersectLineCircle( asPoint( a.getOrigin() ), asPoint( a.getPoint() ), b.getRadius() ).getPoints() );
	}

}
