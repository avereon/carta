package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

import java.util.List;

public class CadIntersection {

	private static final System.Logger log = Log.get();

	public static List<Point3D> getIntersections( DesignShape a, DesignShape b ) {
		if( a instanceof DesignLine ) {
			if( b instanceof DesignLine ) {
				return intersectLineLine( (DesignLine)a, (DesignLine)b );
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
		p1 = transform.times( p1 );
		p2 = transform.times( p2 );
		p3 = transform.times( p3 );
		p4 = transform.times( p4 );

		double uan = (p4.getX() - p3.getX()) * (p1.getY() - p3.getY()) - (p4.getY() - p3.getY()) * (p1.getX() - p3.getX());
		double uad = (p4.getY() - p3.getY()) * (p2.getX() - p1.getX()) - (p4.getX() - p3.getX()) * (p2.getY() - p1.getY());
		double ua = uan / uad;

		// Determine the intersection point
		Point3D x = new Point3D( p1.getX() + ua * (p2.getX() - p1.getX()), p1.getY() + ua * (p2.getY() - p1.getY()), 0 );

		// Transform the intersection point back to target orientation
		x = orientation.getLocalToTargetTransform().times( x );

		return List.of( x );
	}

}
