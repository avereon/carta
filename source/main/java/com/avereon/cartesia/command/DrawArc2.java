package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawArc2 extends DrawCommand {

	private Point3D lastAnchor;

	private double spin;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1
		if( parameters.length < 1 ) {
			promptForPoint( context, tool, "center" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			DesignArc preview = new DesignArc( asPoint( tool, parameters[ 0 ], context.getAnchor() ), 0.0, 0.0, 360.0, DesignArc.Type.OPEN );
			setPreview( tool, preview );
			promptForPoint( context, tool, "start" );
			return INCOMPLETE;
		}

		// Step 3
		if( parameters.length < 3 ) {
			Point3D start = asPoint( tool, parameters[ 1 ], context.getAnchor() );
			lastAnchor = start;
			DesignArc preview = getPreview();
			preview.setRadius( CadGeometry.distance( preview.getOrigin(), start ) );
			preview.setStart( CadGeometry.angle360( start.subtract( preview.getOrigin() ) ) );
			preview.setExtent( 0.0 );
			promptForPoint( context, tool, "extent" );
			return INCOMPLETE;
		}

		DesignArc arc = getPreview();
		arc.setExtent( getExtent( arc, asPoint( tool, parameters[ 2 ], context.getAnchor() ), spin ) );

		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignArc preview = getPreview();
			if( preview != null ) {
				DesignTool tool = (DesignTool)event.getSource();
				Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );

				spin = updateCcwFlag( preview, lastAnchor, point, spin );

				switch( getStep() ) {
					case 2 -> preview.setRadius( CadGeometry.distance( preview.getOrigin(), point ) );
					case 3 -> preview.setExtent( getExtent( preview, point, spin ) );
				}
				lastAnchor = point;
			}
		}
	}

	private double getExtent( DesignArc arc, Point3D point, double spin ) {
		Point3D startPoint = CadGeometry.polarToCartesian360( new Point3D( arc.getRadius(), arc.getStart(), 0 ) );
		double angle = -CadGeometry.angle360( startPoint, point.subtract( arc.getOrigin() ) );

		if( angle < 0 && spin > 0 ) angle += 360;
		if( angle > 0 && spin < 0 ) angle -= 360;

		return angle;
	}

	private static double updateCcwFlag( DesignArc arc, Point3D lastPoint, Point3D newPoint, double spin ) {
		if( arc == null || lastPoint == null || newPoint == null ) return spin;

		// Use the arc information to create a transform to test the points
		Point3D rotate = CadGeometry.polarToCartesian360( new Point3D( arc.getRadius(), arc.getStart() + 90, 0 ) );
		CadTransform transform = CadTransform.localTransform( arc.getOrigin(), CadPoints.UNIT_Z, rotate );

		Point3D lp = transform.times( lastPoint );
		Point3D np = transform.times( newPoint );

		if( lp.getX() > 0 & np.getX() > 0 ) {
			if( np.getY() > 0 & lp.getY() < 0 ) return 1.0;
			if( np.getY() < 0 & lp.getY() > 0 ) return -1.0;
		}

		return spin;
	}

}
