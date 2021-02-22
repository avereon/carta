package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawArc3 extends DrawCommand {

	private DesignLine previewLine;

	private DesignArc previewArc;

	private Point3D start;

	private Point3D mid;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1
		if( parameters.length < 1 ) {
			promptForPoint( context, tool, "start-point" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			start = asPoint( tool, parameters[ 0 ], context.getAnchor() );
			setPreview( tool, previewLine = new DesignLine( start, start ) );
			promptForPoint( context, tool, "mid-point" );
			return INCOMPLETE;
		}

		// Step 3
		if( parameters.length < 3 ) {
			removePreview( tool, previewLine );

			mid = asPoint( tool, parameters[ 1 ], context.getAnchor() );
			setPreview( tool, previewArc = CadGeometry.arcFromThreePoints( start, mid, mid ) );

			promptForPoint( context, tool, "end-point" );
			return INCOMPLETE;
		}

		DesignArc arc = getPreview();
		//arc.setExtent( getExtent( arc, asPoint( tool, parameters[ 2 ], context.getAnchor() ), extentCcw ) );

		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {

			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );

			switch( getStep() ) {
				case 2 -> previewLine.setPoint( point );
				case 3 -> {
					DesignArc next = CadGeometry.arcFromThreePoints( start, mid, point );
					if( next != null ) {
						previewArc.setOrigin( next.getOrigin() );
						previewArc.setRadius( next.getRadius() );
						previewArc.setStart( next.getStart() );
						previewArc.setExtent( next.getExtent() );
					}
				}
			}
		}
	}

}
