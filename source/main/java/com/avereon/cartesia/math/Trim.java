package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignCurve;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Trim {

	private static final System.Logger log = Log.get();

	public static void trim( DesignTool tool, DesignShape trim, DesignShape edge, Point3D trimPoint, Point3D edgePoint ) {
		List<Point3D> intersections = CadIntersection.getIntersections( trim, edge );

		// FIXME I think I can get rid of this 'if' tree
		if( trim instanceof DesignLine ) {
			update( tool, (DesignLine)trim, trimPoint, edgePoint, intersections );
		} else if( trim instanceof DesignEllipse ) {
			//			arcToLine( tool, (DesignEllipse)trim, (DesignLine)edge, trimPoint, edgePoint );
		} else if( trim instanceof DesignCurve ) {
			//			curveToLine( tool, (DesignCurve)trim, (DesignLine)edge, trimPoint, edgePoint );
		}
	}

	private static void update( DesignTool tool, DesignLine line, Point3D trimPoint, Point3D edgePoint, Collection<Point3D> xns ) {
		updateLine( tool, line, trimPoint, getNearestOnScreen( tool, edgePoint, xns ) );
	}

	private static void updateLine( DesignTool tool, DesignLine line, Point3D trimPoint, Point3D point ) {
		if( point == null ) return;

		Point3D o = line.getOrigin();
		Point3D p = line.getPoint();
		Point3D n = getNearestOnScreen( tool, trimPoint, o, p );
		if( n == o ) {
			line.setOrigin( point );
		} else {
			line.setPoint( point );
		}
	}

	public static void updateEllipse( DesignTool tool, DesignEllipse ellipse, Point3D trimPoint, Point3D point ) {
		// TODO Trim.arcToLine()
	}

	public static void updateCurve( DesignTool tool, DesignCurve curve, Point3D trimPoint, Point3D point ) {
		// TODO Trim.curveToLine()
	}

	private static Point3D getNearestOnScreen( DesignTool tool, Point3D screenPoint, Point3D... points ) {
		return getNearestOnScreen( tool, screenPoint, Arrays.asList( points ) );
	}

	private static Point3D getNearestOnScreen( DesignTool tool, Point3D screenPoint, Collection<Point3D> intersections ) {
		double delta;
		double distance = Double.MAX_VALUE;
		Point3D nearest = null;
		for( Point3D test : intersections ) {
			delta = CadGeometry.distance( tool.worldToScreen( test ), screenPoint );
			if( delta < distance ) {
				distance = delta;
				nearest = test;
			}
		}
		return nearest;
	}

}
