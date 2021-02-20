package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawArcCommand extends DrawCommand {

	private boolean extentCcw;

	private Point3D lastAnchor;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1
		if( parameters.length < 1 ) {
			promptForPoint( context, tool, "center" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			DesignArc preview = new DesignArc( asPoint( tool, parameters[ 0 ], context.getAnchor() ), 0.0, 0.0, 360., DesignArc.Type.OPEN );
			preview.setFillPaint( "#00000000" );
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
			promptForPoint( context, tool, "extent" );
			return INCOMPLETE;
		}

		DesignArc arc = getPreview();
		arc.setExtent( getExtent( arc, asPoint( tool, parameters[ 2 ], context.getAnchor() ), extentCcw ) );

		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignArc preview = getPreview();
			if( preview != null ) {
				DesignTool tool = (DesignTool)event.getSource();
				Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );

				switch( getStep() ) {
					case 2 -> preview.setRadius( CadGeometry.distance( preview.getOrigin(), point ) );
					case 3 -> preview.setExtent( getExtent( preview, point, extentCcw ) );
				}
				lastAnchor = point;
			}
		}
	}

	private double getExtent( DesignArc arc, Point3D point, boolean extentCcw ) {
		Point3D origin = arc.getOrigin();
		Point3D startPoint = CadGeometry.polarToCartesian360( new Point3D( arc.getRadius(), arc.getStart(), 0 ) );
		Point3D vp = point.subtract( origin );

		double angle = CadGeometry.angle360( startPoint, vp );

		// TODO Handle the CCW flag

		return angle;

	}

}
