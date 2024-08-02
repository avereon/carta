package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignCubic;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.List;

@CustomLog
public class CadEdit {

	protected static void update( DesignTool tool, DesignShape shape, Point3D shapePoint, Point3D target ) {
		if( shape instanceof DesignLine ) {
			updateLine( tool, (DesignLine)shape, shapePoint, target );
		} else if( shape instanceof DesignArc ) {
			updateArc( tool, (DesignArc)shape, shapePoint, target );
		} else if( shape instanceof DesignCubic ) {
			updateCurve( tool, (DesignCubic)shape, shapePoint, target );
		}
	}

	protected static void updateLine( DesignTool tool, DesignLine line, Point3D linePoint, Point3D target ) {
		if( target == null ) return;
		Point3D source = CadPoints.getNearestOnScreen( tool, linePoint, line.getOrigin(), line.getPoint() );
		line.moveEndpoint( source, target );
	}

	protected static void updateArc( DesignTool tool, DesignArc arc, Point3D mousePoint, Point3D target ) {
		// Determine the start point
		Point3D startPoint = CadGeometry.ellipsePoint360( arc, arc.getStart() );
		// Determine the extent point
		Point3D extentPoint = CadGeometry.ellipsePoint360( arc, arc.getStart() + arc.getExtent() );
		// Determine if we are moving the start point or the extent point
		Point3D source = CadPoints.getNearestOnScreen( tool, mousePoint, startPoint, extentPoint );

		arc.moveEndpoint( source, target );
	}

	protected static void updateCurve( DesignTool tool, DesignCubic curve, Point3D trimPoint, Point3D target ) {
		if( target == null ) return;

		double t = CadGeometry.getCubicParametricValue( curve, target );
		List<DesignCubic> curves = CadGeometry.cubicSubdivide( curve, t );
		if( curves.isEmpty() ) return;

		// Now we have the two curves, we need to determine which one to use
		double da = CadGeometry.distance( tool.worldToScreen( curve.getOrigin() ), trimPoint );
		double dd = CadGeometry.distance( tool.worldToScreen( curve.getPoint() ), trimPoint );

		curve.updateFrom( curves.get( dd < da ? 0 : 1 ) );
	}

}
