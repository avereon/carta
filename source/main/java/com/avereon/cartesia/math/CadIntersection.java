package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignCurve;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.curve.math.Intersection;
import com.avereon.curve.math.Intersection2D;
import com.avereon.curve.math.Intersection3D;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.avereon.cartesia.math.CadPoints.*;

//   | L | A | C | P |
// L | ✓ | ✓ | ✓ |   |
// A | - | ✓ |   |   |
// C | - | - |   |   |
// P | - | - | - |   |

@CustomLog
public class CadIntersection {

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
			log.atWarning().log( "Lines are too far apart to intersect: %s > separation limit(%s)", skewDistance, CadConstants.RESOLUTION_LENGTH );
			return List.of();
		}

		// Check for parallel lines
		double parallelAngle = CadGeometry.lineLineAngle( p1, p2, p3, p4 );
		if( parallelAngle < CadConstants.RESOLUTION_ANGLE ) {
			log.atWarning().log( "Lines are too parallel to intersect: %s < separation angle(%s)", parallelAngle, CadConstants.RESOLUTION_ANGLE );
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

		Point3D p1 = line.getOrigin();
		Point3D p2 = line.getPoint();
		Point3D o = ellipse.getOrigin();
		double rx = ellipse.getXRadius();
		double ry = ellipse.getYRadius();

		return toFxPoints( Intersection2D.intersectLineEllipse( asPoint( p1 ), asPoint( p2 ), asPoint( o ), rx, ry ).getPoints() );
	}

	public static List<Point3D> intersectLineCurve( DesignLine a, DesignCurve b ) {
		Intersection2D xn = Intersection2D.intersectLineBezier3( asPoint( a.getOrigin() ),
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
		boolean coplanar = CadGeometry.areCoplanar( a.getOrientation(), b.getOrigin() );

		if( !coplanar ) {
			// Use the plane-plane intersection method
			CadOrientation orientationA = a.getOrientation();
			CadOrientation orientationB = b.getOrientation();
//			double[][] line = intersectPlanePlane( orientationA.getOrigin(), orientationA.getNormal(), orientationB.getOrigin(), orientationB.getNormal() );
//			return line;
			intersectPlanePlane( orientationA.getOrigin(), orientationA.getNormal(), orientationB.getOrigin(), orientationB.getNormal() ).stream().findAny().get();
		}

		Intersection2D xn = Intersection2D.intersectEllipseEllipse( asPoint( a.getOrigin() ),
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
		Intersection2D xn = Intersection2D.intersectEllipseBezier3( asPoint( a.getOrigin() ),
			a.getXRadius(),
			a.getYRadius(),
			Math.toRadians( a.calcRotate() ),
			asPoint( b.getOrigin() ),
			asPoint( b.getOriginControl() ),
			asPoint( b.getPointControl() ),
			asPoint( b.getPoint() )
		);
		return toFxPoints( xn.getPoints() );
	}

	public static List<Point3D> intersectCurveCurve( DesignCurve a, DesignCurve b ) {
		Intersection2D xn = Intersection2D.intersectBezier3Bezier3( asPoint( a.getOrigin() ),
			asPoint( a.getOriginControl() ),
			asPoint( a.getPointControl() ),
			asPoint( a.getPoint() ),
			asPoint( b.getOrigin() ),
			asPoint( b.getOriginControl() ),
			asPoint( b.getPointControl() ),
			asPoint( b.getPoint() )
		);
		return toFxPoints( xn.getPoints() );
	}

	public static List<Point3D> intersectLinePlane( DesignLine line, Point3D planeOrigin, Point3D planeNormal ) {
		Intersection3D intersection = Intersection3D.intersectLinePlane( asPoint( line.getOrigin() ), asPoint( line.getPoint() ), asPoint( planeOrigin ), asPoint( planeNormal ) );
		return toFxPoints( intersection.getPoints() );
	}

	public static Collection<DesignLine> intersectPlanePlane( Point3D originA, Point3D normalA, Point3D originB, Point3D normalB ) {
		Intersection3D intersection = Intersection3D.intersectPlanePlane( asPoint( originA ), asPoint( normalA ), asPoint( originB ), asPoint( normalB ) );
		if( intersection.getType() != Intersection.Type.INTERSECTION ) return Set.of();
		List<Point3D> points = toFxPoints( intersection.getPoints() );
		return Set.of( new DesignLine( points.get( 0 ), points.get( 1 ) ) );
	}

	private static List<Point3D> intersectLineCircle( DesignLine a, DesignEllipse b ) {
		return toFxPoints( Intersection2D.intersectLineCircle( asPoint( a.getOrigin() ), asPoint( a.getPoint() ), b.getXRadius() ).getPoints() );
	}

}
