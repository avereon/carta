package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignLine;
import javafx.geometry.Point3D;

import java.util.List;

public class Meet {

	/**
	 * Make two lines meet by moving the specified points to the intersection.
	 *
	 * @param boundary
	 */
	public static final void lineToLine( Point3D point, Point3D point2, DesignLine line, DesignLine boundary ) {
		//		Vector intersection = Intersection.getIntersection( line, boundary );
		//		if( intersection == null ) return;
		//
		//		if( point == line.getP1() ) {
		//			line.setP1( intersection );
		//		} else {
		//			line.setP2( intersection );
		//		}
		//
		//		if( point2 == boundary.getP1() ) {
		//			boundary.setP1( intersection );
		//		} else {
		//			boundary.setP2( intersection );
		//		}
	}

	/**
	 * Make two lines meet by moving the points on the lines nearest the
	 * intersection to the intersection.
	 *
	 * @param line
	 * @param edge
	 */
	public static void lineToLine( DesignLine line, DesignLine edge ) {
		List<Point3D> intersections = CadIntersection.getIntersections( line, edge );
		if( intersections.isEmpty() ) return;

		Point3D intersection = intersections.get( 0 );
		if( CadGeometry.distance( line.getOrigin(), intersection ) < CadGeometry.distance( line.getPoint(), intersection ) ) {
			line.setOrigin( intersection );
		} else {
			line.setPoint( intersection );
		}
		if( CadGeometry.distance( edge.getOrigin(), intersection ) < CadGeometry.distance( edge.getPoint(), intersection ) ) {
			edge.setOrigin( intersection );
		} else {
			edge.setPoint( intersection );
		}
	}

}
