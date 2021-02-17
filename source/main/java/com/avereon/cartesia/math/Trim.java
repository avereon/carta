package com.avereon.cartesia.math;

import com.avereon.cartesia.data.*;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;

import java.util.List;

public class Trim {

	public static void trim( DesignTool tool, DesignShape trim, DesignShape edge, Point3D trimPoint, Point3D edgePoint ) {
		if( trim instanceof DesignLine ) {
			if( edge instanceof DesignLine ) {
				lineToLine( tool, (DesignLine)trim, (DesignLine)edge, trimPoint );
			} else if( edge instanceof DesignEllipse ) {
				lineToArc( tool, (DesignLine)trim, (DesignEllipse)edge, trimPoint, edgePoint );
			} else if( edge instanceof DesignCurve ) {
				lineToCurve( tool, (DesignLine)trim, (DesignCurve)edge, trimPoint, edgePoint );
			}
		} else if( trim instanceof DesignEllipse ) {
			arcToLine( tool, (DesignEllipse)trim, (DesignLine)edge, trimPoint, edgePoint );
		} else if( trim instanceof DesignCurve ) {
			curveToLine( tool, (DesignCurve)trim, (DesignLine)edge, trimPoint, edgePoint );
		}
	}

	public static void lineToLine( DesignTool tool, DesignLine trim, DesignLine edge, Point3D trimPoint ) {
		List<Point3D> xns = CadIntersection.intersectLineLine( trim, edge );
		if( xns.isEmpty() ) return;

		Point3D preference = getNearestOnScreen( tool, trim, trimPoint );
		if( preference == trim.getOrigin() ) {
			trim.setOrigin( xns.get( 0 ) );
		} else {
			trim.setPoint( xns.get( 0 ) );
		}
	}

	public static void lineToArc( DesignTool tool, DesignLine trim, DesignEllipse edge, Point3D trimPoint, Point3D edgePoint ) {
		// TODO Trim.lineToArc()
	}

	public static void lineToCurve( DesignTool tool, DesignLine trim, DesignCurve edge, Point3D trimPoint, Point3D edgePoint ) {
		// TODO Trim.lineToCurve()
	}

	public static void arcToLine( DesignTool tool, DesignEllipse trim, DesignLine edge, Point3D trimPoint, Point3D edgePoint ) {
		// TODO Trim.arcToLine()
	}

	public static void curveToLine( DesignTool tool, DesignCurve trim, DesignLine edge, Point3D trimPoint, Point3D edgePoint ) {
		// TODO Trim.curveToLine()
	}

	private static Point3D getNearestOnScreen( DesignTool tool, DesignLine line, Point3D screenPoint ) {
		double d1 = CadGeometry.distance( tool.worldToScreen( line.getOrigin() ), screenPoint );
		double d2 = CadGeometry.distance( tool.worldToScreen( line.getPoint() ), screenPoint );
		return d1 < d2 ? line.getOrigin() : line.getPoint();
	}

	//	private static final Vector getNearestOnScreen( ModelEditor editor, Vector[] intersections, Vector point ) {
	//		Vector nearest = null;
	//		double delta = 0.0;
	//		double distance = Double.MAX_VALUE;
	//		for( Vector test : intersections ) {
	//			delta = Geometry.getDistance( editor.convertWorldToScreen( test ), point );
	//			if( delta < distance ) {
	//				distance = delta;
	//				nearest = test;
	//			}
	//		}
	//		return nearest;
	//	}

}
