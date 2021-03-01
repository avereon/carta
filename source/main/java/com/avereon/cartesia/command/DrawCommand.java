package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import javafx.geometry.Point3D;

public abstract class DrawCommand extends Command {

	protected double deriveRotate( Point3D origin, Point3D point ) {
		return CadGeometry.angle360( point.subtract( origin ) );
	}

	protected double deriveYRadius( Point3D origin, Point3D xPoint, Point3D yPoint ) {
		// This is the origin y-point distance
		//return origin.distance( yPoint );

		// This is the y-point distance perpendicular to the origin x-point line
		return CadGeometry.linePointDistance( origin, xPoint, yPoint );
	}

	protected double deriveStart( DesignArc arc, Point3D point ) {
		return deriveRotatedArcAngle( arc, point );
	}

	protected double deriveExtent( DesignArc arc, Point3D point, double spin ) {
		double angle = deriveRotatedArcAngle( arc, point ) - arc.getStart();

		if( angle < 0 && spin > 0 ) angle += 360;
		if( angle > 0 && spin < 0 ) angle -= 360;

		return angle % 360;
	}

	/**
	 * Get the spin from the arc origin, through the last point to the next point.
	 * This will return 1.0 for a left-hand(CCW) spin or -1.0 for right-hand(CW)
	 * spin. If the spin cannot be determined or the points are collinear the
	 * prior spin is returned
	 *
	 * @param arc The arc to use
	 * @param lastPoint The last point
	 * @param nextPoint The next point
	 * @param priorSpin The prior spin
	 * @return 1.0 for CCW spin, -1.0 for CW spin or the prior spin
	 */
	protected double getExtentSpin( DesignArc arc, Point3D lastPoint, Point3D nextPoint, double priorSpin ) {
		if( arc == null || lastPoint == null || nextPoint == null ) return priorSpin;

		// NOTE Rotate does not have eccentricity applied
		// NOTE Start does have eccentricity applied
		// This special transform takes into account the rotation and start angle
		double e = arc.getXRadius() / arc.getYRadius();
		CadTransform transform = CadTransform.rotation( Point3D.ZERO, CadPoints.UNIT_Z, -arc.getStart() ).combine( CadTransform.scale( 1, e, 1 ) ).combine( CadTransform.rotation( Point3D.ZERO, CadPoints.UNIT_Z, -arc.calcRotate() ) ).combine( CadTransform.translation( arc.getOrigin().multiply( -1 ) ) );

		Point3D lp = transform.apply( lastPoint );
		Point3D np = transform.apply( nextPoint );

		double spin = priorSpin;
		if( lp.getX() > 0 & np.getX() > 0 ) {
			if( np.getY() > 0 & (lp.getY() <= 0 || priorSpin == 0) ) spin = 1.0;
			if( np.getY() < 0 & (lp.getY() >= 0 || priorSpin == 0) ) spin = -1.0;
		}

		return spin;
	}

	private double deriveRotatedArcAngle( DesignArc arc, Point3D point ) {
		CadTransform t = arc.getLocalTransform();

		double angle = CadGeometry.angle360( t.apply( point ) );
		if( angle <= -180 ) angle += 360;
		if( angle > 180 ) angle -= 360;

		return angle;
	}

}
