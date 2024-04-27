package com.avereon.cartesia.math;

import com.avereon.cartesia.data.*;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.transaction.Txn;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.List;
import java.util.Set;

@CustomLog
public class Split {

	public static void split( BaseDesignTool tool, DesignShape shape, Point3D mousePoint ) {
		Set<DesignShape> shapes = Set.of();

		if( shape instanceof DesignLine ) {
			shapes = splitLine( tool, (DesignLine)shape, mousePoint );
		} else if( shape instanceof DesignEllipse ) {
			if( shape instanceof DesignArc ) {
				shapes = splitArc( tool, (DesignArc)shape, mousePoint );
			} else {
				shapes = splitEllipse( tool, (DesignEllipse)shape, mousePoint );
			}
		} else if( shape instanceof DesignCubic ) {
			shapes = splitCurve( tool, (DesignCubic)shape, mousePoint );
		}

		// Replace the old shape with the new shapes
		if( !shapes.isEmpty() ) {
			DesignLayer y = shape.getLayer();
			final Set<DesignShape> finalShapes = shapes;
			Txn.run( () -> {
				y.removeShape( shape );
				finalShapes.forEach( y::addShape );
			} );
		}
	}

	static Set<DesignShape> splitLine( BaseDesignTool tool, DesignLine line, Point3D point ) {
		// Find the point "on the line"
		Point3D nearest = CadGeometry.nearestBoundLinePoint( line.getOrigin(), line.getPoint(), point );
		if( nearest == null ) return Set.of();

		// Make two new lines
		DesignLine a = new DesignLine( line.getOrigin(), nearest );
		a.copyFrom( line );
		DesignLine b = new DesignLine( nearest, line.getPoint() );
		b.copyFrom( line );

		return Set.of( a, b );
	}

	static Set<DesignShape> splitEllipse( BaseDesignTool tool, DesignEllipse ellipse, Point3D point ) {
		// Find the angle "on the ellipse"
		double theta = CadGeometry.ellipseAngle360( ellipse, point );

		// Make a new arc
		DesignArc arc = new DesignArc( ellipse.getOrigin(), ellipse.getXRadius(), ellipse.getYRadius(), ellipse.calcRotate(), theta, 360.0, DesignArc.Type.OPEN );

		return Set.of( arc );
	}

	static Set<DesignShape> splitArc( BaseDesignTool tool, DesignArc arc, Point3D point ) {
		// Find the point "on the arc" and make to new arcs
		List<Point3D> xns = CadIntersection.getIntersections( arc, new DesignLine( arc.getOrigin(), point ) );
		Point3D nearest = CadPoints.getNearest( point, xns );

		// Find the angle "on the ellipse"
		double theta = CadGeometry.ellipseAngle360( arc, nearest );

		// Make a new arcs
		DesignArc a = new DesignArc( arc.getOrigin(), arc.getXRadius(), arc.getYRadius(), arc.calcRotate(), arc.getStart(), theta - arc.getStart(), DesignArc.Type.OPEN );
		DesignArc b = new DesignArc( arc.getOrigin(), arc.getXRadius(), arc.getYRadius(), arc.calcRotate(), theta, (arc.getStart() + arc.getExtent()) - theta, DesignArc.Type.OPEN );

		return Set.of( a, b );
	}

	static Set<DesignShape> splitCurve( BaseDesignTool tool, DesignCubic curve, Point3D point ) {
		// Fine the parametric value "on the curve"
		double t = CadGeometry.getCubicParametricValueNear( curve, point );
		if( Double.isNaN( t ) ) return Set.of();

		// Make new curves
		List<DesignCubic> curves = CadGeometry.cubicSubdivide( curve, t );
		if( curves.size() < 2 ) return Set.of();

		return Set.of( curves.get( 0 ), curves.get( 1 ) );
	}

}
