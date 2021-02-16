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
	public static void lineToLine( Point3D point, Point3D point2, DesignLine line, DesignLine boundary ) {
		List<Point3D> intersections = CadIntersection.getIntersections( line, boundary );
		if( intersections.isEmpty() ) return;

		Point3D intersection = intersections.get( 0 );
		if( point == line.getOrigin() ) {
			line.setOrigin( intersection );
		} else {
			line.setPoint( intersection );
		}

		if( point2 == boundary.getOrigin() ) {
			boundary.setOrigin( intersection );
		} else {
			boundary.setPoint( intersection );
		}
	}

	/**
	 * Make two lines meet by moving the points on the lines nearest the
	 * intersection to the intersection.
	 *
	 * @param lineA The first line
	 * @param lineB The other line
	 */
	public static void lineToLine( DesignLine lineA, DesignLine lineB ) {
		List<Point3D> intersections = CadIntersection.getIntersections( lineA, lineB );
		if( intersections.isEmpty() ) return;

		Point3D intersection = intersections.get( 0 );
		if( CadGeometry.distance( lineA.getOrigin(), intersection ) < CadGeometry.distance( lineA.getPoint(), intersection ) ) {
			lineA.setOrigin( intersection );
		} else {
			lineA.setPoint( intersection );
		}
		if( CadGeometry.distance( lineB.getOrigin(), intersection ) < CadGeometry.distance( lineB.getPoint(), intersection ) ) {
			lineB.setOrigin( intersection );
		} else {
			lineB.setPoint( intersection );
		}
	}

}
