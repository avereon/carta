package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignLine;
import javafx.geometry.Point3D;

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
	 * @param boundary
	 */
	public static final void lineToLine( DesignLine line, DesignLine boundary ) {
//		Point3D x = Intersection.getIntersection( line, boundary );
//		if( x == null ) return;
//		if( Geometry.getDistance( line.getP1(), x ) < Geometry.getDistance( line.getP2(), x ) ) {
//			line.setP1( x );
//		} else {
//			line.setP2( x );
//		}
	}


}
