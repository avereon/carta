package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawCircle3 extends DrawCommand {

	private DesignLine previewLine;

	private DesignEllipse previewEllipse;

	private Point3D start;

	private Point3D mid;

	private Point3D lastAnchor;

	private double spin;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1
		if( parameters.length < 1 ) {
			addPreview( tool, previewLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "start-point" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			start = asPoint( context, parameters[ 0 ] );
			previewLine.setOrigin( start );
			promptForPoint( context, tool, "mid-point" );
			return INCOMPLETE;
		}

		// Step 3
		if( parameters.length < 3 ) {
			removePreview( tool, previewLine );

			mid = asPoint( context, parameters[ 1 ] );
			addPreview( tool, previewEllipse = CadGeometry.circleFromThreePoints( start, mid, mid ) );

			promptForPoint( context, tool, "end-point" );
			return INCOMPLETE;
		}

		DesignEllipse ellipse = CadGeometry.circleFromThreePoints( start, mid, asPoint( context, parameters[ 2 ] ) );
		if( ellipse != null ) {
			previewEllipse.setOrigin( ellipse.getOrigin() );
			previewEllipse.setRadius( ellipse.getRadius() );
		}

		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );

			switch( getStep() ) {
				case 1 -> {
					previewLine.setOrigin( point );
					previewLine.setPoint( point );
				}
				case 2 -> previewLine.setPoint( point );
				case 3 -> {
					DesignArc next = CadGeometry.arcFromThreePoints( start, mid, point );
					if( next != null ) {
						previewEllipse.setOrigin( next.getOrigin() );
						previewEllipse.setRadius( next.getRadius() );
					}
				}
			}
		}
	}

}
