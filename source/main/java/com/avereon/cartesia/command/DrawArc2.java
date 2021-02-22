package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawArc2 extends DrawCommand {

	private DesignLine previewLine;

	private DesignArc previewArc;

	private Point3D lastAnchor;

	private double spin;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1
		if( parameters.length < 1 ) {
			previewLine = new DesignLine(context.getWorldMouse(),context.getWorldMouse() );
			setPreview( tool, previewLine );
			promptForPoint( context, tool, "center" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			Point3D origin = asPoint( context, parameters[ 0 ] );
			previewLine.setOrigin( origin );
			previewLine.setPoint( origin );
			previewArc = new DesignArc( origin, 0.0, 0.0, 360.0, DesignArc.Type.OPEN );
			setPreview( tool, previewArc );
			promptForPoint( context, tool, "start" );
			return INCOMPLETE;
		}

		// Step 3
		if( parameters.length < 3 ) {
			Point3D start = asPoint( context, parameters[ 1 ] );
			lastAnchor = start;
			previewArc.setRadius( CadGeometry.distance( previewArc.getOrigin(), start ) );
			previewArc.setStart( CadGeometry.angle360( start.subtract( previewArc.getOrigin() ) ) );
			previewArc.setExtent( 0.0 );
			promptForPoint( context, tool, "extent" );
			return INCOMPLETE;
		}

		previewArc.setExtent( getExtent( previewArc, asPoint( context, parameters[ 2 ] ), spin ) );
		removePreview( tool, previewLine );

		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
				DesignTool tool = (DesignTool)event.getSource();
				Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
				spin = getSpin( previewArc, lastAnchor, point, spin );

				switch( getStep() ) {
					case 1 -> {
						previewLine.setOrigin( point );
						previewLine.setPoint( point );
					}
					case 2 -> {
						previewLine.setPoint( point );
						previewArc.setRadius( CadGeometry.distance( previewArc.getOrigin(), point ) );
					}
					case 3 -> {
						previewLine.setPoint( point );
						previewArc.setExtent( getExtent( previewArc, point, spin ) );
					}
				}
				lastAnchor = point;
		}
	}

	private double getExtent( DesignArc arc, Point3D point, double spin ) {
		Point3D startPoint = CadGeometry.polarToCartesian360( new Point3D( arc.getRadius(), arc.getStart(), 0 ) );
		double angle = -CadGeometry.angle360( startPoint, point.subtract( arc.getOrigin() ) );

		if( angle < 0 && spin > 0 ) angle += 360;
		if( angle > 0 && spin < 0 ) angle -= 360;

		return angle;
	}

	private static double getSpin( DesignArc arc, Point3D lastPoint, Point3D newPoint, double priorSpin ) {
		if( arc == null || lastPoint == null || newPoint == null ) return priorSpin;

		// Use the arc information to create a transform to test the points
		Point3D rotate = CadGeometry.polarToCartesian360( new Point3D( arc.getRadius(), arc.getStart() + 90, 0 ) );
		CadTransform transform = CadTransform.localTransform( arc.getOrigin(), CadPoints.UNIT_Z, rotate );

		Point3D lp = transform.times( lastPoint );
		Point3D np = transform.times( newPoint );

		if( lp.getX() > 0 & np.getX() > 0 ) {
			if( np.getY() > 0 & lp.getY() < 0 ) return 1.0;
			if( np.getY() < 0 & lp.getY() > 0 ) return -1.0;
		}

		return priorSpin;
	}

}
