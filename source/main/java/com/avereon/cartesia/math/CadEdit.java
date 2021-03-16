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

import java.util.List;

public class CadEdit {

	private static final System.Logger log = Log.get();

	protected static void update( DesignTool tool, DesignShape shape, Point3D shapePoint, Point3D point ) {
		if( shape instanceof DesignLine ) {
			updateLine( tool, (DesignLine)shape, shapePoint, point );
		} else if( shape instanceof DesignArc ) {
			updateArc( tool, (DesignArc)shape, shapePoint, point );
		} else if( shape instanceof DesignCurve ) {
			updateCurve( tool, (DesignCurve)shape, shapePoint, point );
		}
	}

	protected static void updateLine( DesignTool tool, DesignLine line, Point3D linePoint, Point3D point ) {
		if( point == null ) return;

		Point3D o = line.getOrigin();
		Point3D p = line.getPoint();
		Point3D n = CadPoints.getNearestOnScreen( tool, linePoint, o, p );

		// FIXME Move to DesignLine.updateFrom
		if( n == o ) {
			line.setOrigin( point );
		} else {
			line.setPoint( point );
		}
	}

	protected static void updateArc( DesignTool tool, DesignArc arc, Point3D trimPoint, Point3D point ) {
		if( point == null ) return;

		// Determine the start point
		Point3D startPoint = CadGeometry.ellipsePoint360( arc, arc.getStart() );
		// Determine the extent point
		Point3D extentPoint = CadGeometry.ellipsePoint360( arc, arc.getStart() + arc.getExtent() );
		// Determine if we are moving the start point or the extent point
		Point3D n = CadPoints.getNearestOnScreen( tool, trimPoint, startPoint, extentPoint );
		double theta = CadGeometry.ellipseAngle360( arc, point );

		// FIXME Move to DesignEllipse.updateFrom
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

	protected static void updateCurve( DesignTool tool, DesignCurve curve, Point3D trimPoint, Point3D point ) {
		if( point == null ) return;

		// FIXME This does not give an accurate result
		double t = CadGeometry.getCurveParametricValue( curve, point );
		System.out.println( "t="+t);

		List<DesignCurve> curves = CadGeometry.curveSubdivide( curve, t );
		if( curves.size() < 1 ) return;

		// Now we have the two curves, we need to determine which one to use
		double da = CadGeometry.distance(  tool.worldToScreen( curve.getOrigin() ), trimPoint );
		double dd = CadGeometry.distance( tool.worldToScreen( curve.getPoint() ), trimPoint );

		curve.updateFrom( curves.get( dd < da ? 0 : 1 ) );
	}

}
