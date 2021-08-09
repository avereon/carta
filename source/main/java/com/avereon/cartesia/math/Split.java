package com.avereon.cartesia.math;

import com.avereon.cartesia.data.*;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;

public class Split {

	public static void split( DesignTool tool, DesignShape shape, Point3D mousePoint ) {
		if( shape instanceof DesignLine ) {
			splitLine( tool, (DesignLine)shape, mousePoint );
		} else if( shape instanceof DesignEllipse ) {
			if( shape instanceof DesignArc ) {
				splitArc( tool, (DesignArc)shape, mousePoint );
			} else {
				splitEllipse( tool, (DesignEllipse)shape, mousePoint );
			}
		} else if( shape instanceof DesignCurve ) {
			splitCurve( tool, (DesignCurve)shape, mousePoint );
		}
	}

	private static void splitLine( DesignTool tool, DesignLine line, Point3D point ) {
		// Find the point "on the line" and make two new lines
		Point3D nearest = CadGeometry.nearestBoundLinePoint( line.getOrigin(), line.getPoint(), point );
		if( nearest == null ) return;

		// TODO Split.splitLine()
	}

	private static void splitEllipse( DesignTool tool, DesignEllipse ellipse, Point3D point ) {
		// TODO Split.splitEllipse()
		// Find the point on the ellipse and make a new arc
	}

	private static void splitArc( DesignTool tool, DesignArc arc, Point3D point ) {
		// TODO Split.splitArc()
		// Find the point "on the arc" and make to new arcs
	}

	private static void splitCurve( DesignTool tool, DesignCurve curve, Point3D point ) {
		// TODO Split.splitCurve()
		// Fine the point "on the curve" and make two new curves
	}

}
