package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignCurve;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

import java.util.Collection;
import java.util.List;

//   | L | A | C | P |
// L | ✓ | ✓ |   |   |
// A | - |   |   |   |
// C | - | - |   |   |
// P | - | - | - |   |

public class Meet {

	private static final System.Logger log = Log.get();

	public static void meet( DesignTool tool, DesignShape trim, DesignShape edge, Point3D trimPoint, Point3D edgePoint ) {
		List<Point3D> intersections = CadIntersection.getIntersections( trim, edge );

		if( trim instanceof DesignLine ) {
			if( edge instanceof DesignLine ) {
				update( tool, (DesignLine)trim, (DesignLine)edge, trimPoint, edgePoint, intersections );
			}
		} else if( trim instanceof DesignEllipse ) {
			//			arcToLine( tool, (DesignEllipse)trim, (DesignLine)edge, trimPoint, edgePoint );
		} else if( trim instanceof DesignCurve ) {
			//			curveToLine( tool, (DesignCurve)trim, (DesignLine)edge, trimPoint, edgePoint );
		}
	}

	private static void update( DesignTool tool, DesignLine line, DesignLine edge, Point3D trimPoint, Point3D edgePoint, Collection<Point3D> xns ) {
		Point3D point = CadPoints.getNearestOnScreen( tool, edgePoint, xns );
		updateLine( tool, line, trimPoint, point );
		updateLine( tool, edge, edgePoint, point );
	}

	private static void updateLine( DesignTool tool, DesignLine line, Point3D trimPoint, Point3D point ) {
		if( point == null ) return;

		Point3D o = line.getOrigin();
		Point3D p = line.getPoint();
		Point3D n = CadPoints.getNearestOnScreen( tool, trimPoint, o, p );
		if( n == o ) {
			line.setOrigin( point );
		} else {
			line.setPoint( point );
		}
	}

//	/**
//	 * Make two lines meet by moving the specified points to the intersection.
//	 *
//	 * @param boundary
//	 */
//	public static void lineToLine( Point3D point, Point3D point2, DesignLine line, DesignLine boundary ) {
//		List<Point3D> intersections = CadIntersection.getIntersections( line, boundary );
//		if( intersections.isEmpty() ) return;
//
//		Point3D intersection = intersections.get( 0 );
//		if( point == line.getOrigin() ) {
//			line.setOrigin( intersection );
//		} else {
//			line.setPoint( intersection );
//		}
//
//		if( point2 == boundary.getOrigin() ) {
//			boundary.setOrigin( intersection );
//		} else {
//			boundary.setPoint( intersection );
//		}
//	}
//
//	/**
//	 * Make two lines meet by moving the points on the lines nearest the
//	 * intersection to the intersection.
//	 *
//	 * @param lineA The first line
//	 * @param lineB The other line
//	 */
//	public static void lineToLine( DesignLine lineA, DesignLine lineB ) {
//		List<Point3D> intersections = CadIntersection.getIntersections( lineA, lineB );
//		if( intersections.isEmpty() ) return;
//
//		Point3D intersection = intersections.get( 0 );
//		if( CadGeometry.distance( lineA.getOrigin(), intersection ) < CadGeometry.distance( lineA.getPoint(), intersection ) ) {
//			lineA.setOrigin( intersection );
//		} else {
//			lineA.setPoint( intersection );
//		}
//		if( CadGeometry.distance( lineB.getOrigin(), intersection ) < CadGeometry.distance( lineB.getPoint(), intersection ) ) {
//			lineB.setOrigin( intersection );
//		} else {
//			lineB.setPoint( intersection );
//		}
//	}

}
