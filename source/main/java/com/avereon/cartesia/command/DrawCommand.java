package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import javafx.geometry.Point3D;

public abstract class DrawCommand extends Command {

	protected double getStart( DesignArc arc, Point3D point ) {
		double rotate = arc.getRotate() == null ? 0.0 : arc.getRotate();
		double angle = -CadGeometry.angle360( point.subtract( arc.getOrigin() ) ) - rotate;

		return angle;
	}

	protected double getExtent( DesignArc arc, Point3D point, double spin ) {
		Point3D startPoint = CadGeometry.polarToCartesian360( new Point3D( arc.getRadius(), arc.getStart(), 0 ) );
		double angle = -CadGeometry.angle360( startPoint, point.subtract( arc.getOrigin() ) );

		if( angle < 0 && spin > 0 ) angle += 360;
		if( angle > 0 && spin < 0 ) angle -= 360;

		return angle;
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
	protected static double getExtentSpin( DesignArc arc, Point3D lastPoint, Point3D nextPoint, double priorSpin ) {
		if( arc == null || lastPoint == null || nextPoint == null ) return priorSpin;

		// Use the arc information to create a transform to test the points
		Point3D transformRotate = CadGeometry.polarToCartesian360( new Point3D( arc.getRadius(), arc.getStart() + 90, 0 ) );
		CadTransform transform = CadTransform.localTransform( arc.getOrigin(), CadPoints.UNIT_Z, transformRotate );

		Point3D lp = transform.apply( lastPoint );
		Point3D np = transform.apply( nextPoint );

		if( lp.getX() > 0 & np.getX() > 0 ) {
			if( np.getY() > 0 & (lp.getY() < 0 || priorSpin == 0) ) return 1.0;
			if( np.getY() < 0 & (lp.getY() > 0 || priorSpin == 0) ) return -1.0;
		}

		return priorSpin;
	}

}
