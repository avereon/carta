package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignCurve;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

import java.util.Collection;
import java.util.List;

//   | L | A | C | P |
// L | ✓ | ✓ | ✓ |   |
// A | ✓ | ✓ |   |   |
// C | ✓ |   |   |   |
// P |   |   |   |   |

public class Trim {

	private static final System.Logger log = Log.get();

	public static void trim( DesignTool tool, DesignShape trim, DesignShape edge, Point3D trimPoint, Point3D edgePoint ) {
		List<Point3D> intersections = CadIntersection.getIntersections( trim, edge );

		if( trim instanceof DesignLine ) {
			update( tool, (DesignLine)trim, trimPoint, edgePoint, intersections );
		} else if( trim instanceof DesignArc ) {
			update( tool, (DesignArc)trim, trimPoint, edgePoint, intersections );
		} else if( trim instanceof DesignCurve ) {
			update( tool, (DesignCurve)trim, trimPoint, edgePoint, intersections );
		}
	}

	private static void update( DesignTool tool, DesignLine line, Point3D trimPoint, Point3D edgePoint, Collection<Point3D> xns ) {
		updateLine( tool, line, trimPoint, CadPoints.getNearestOnScreen( tool, edgePoint, xns ) );
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

	private static void update( DesignTool tool, DesignArc arc, Point3D trimPoint, Point3D edgePoint, Collection<Point3D> xns ) {
		updateArc( tool, arc, trimPoint, CadPoints.getNearestOnScreen( tool, edgePoint, xns ) );
	}

	public static void updateArc( DesignTool tool, DesignArc arc, Point3D trimPoint, Point3D point ) {
		// Determine the start point
		Point3D startPoint = CadGeometry.ellipsePoint360( arc, arc.getStart() );
		// Determine the extent point
		Point3D extentPoint = CadGeometry.ellipsePoint360( arc, arc.getStart() + arc.getExtent() );
		// Determine if we are moving the start point or the extent point
		Point3D n = CadPoints.getNearestOnScreen( tool, trimPoint, startPoint, extentPoint );
		double theta = CadGeometry.ellipseAngle360( arc, point );

		try( Txn ignore = Txn.create() ) {
			if( n == startPoint ) {
				double delta = arc.getStart() - theta;
				arc.setStart( theta );
				arc.setExtent( arc.getExtent() + delta );
			} else {
				double extent = theta - arc.getStart();
				arc.setExtent( extent % 360 );
			}
		} catch( TxnException exception ) {
			log.log( Log.ERROR, "Unable to trim arc" );
		}
	}

	private static void update( DesignTool tool, DesignCurve curve, Point3D trimPoint, Point3D edgePoint, Collection<Point3D> xns ) {
		updateCurve( tool, curve, trimPoint, CadPoints.getNearestOnScreen( tool, edgePoint, xns ) );
	}

	public static void updateCurve( DesignTool tool, DesignCurve curve, Point3D trimPoint, Point3D point ) {
		if( point == null ) return;

		List<DesignCurve> curves = CadGeometry.curveSubdivide( curve, CadGeometry.getCurveParametricValue( curve, point ) );
		if( curves.size() < 1 ) return;

		// Now we have the two curves, we need to determine which one to use
		double da = CadGeometry.distance(  tool.worldToScreen( curve.getOrigin() ), trimPoint );
		double dd = CadGeometry.distance( tool.worldToScreen( curve.getPoint() ), trimPoint );

		curve.updateFrom( curves.get( dd < da ? 0 : 1 ) );
	}

}
