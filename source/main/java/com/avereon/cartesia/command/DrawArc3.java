package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawArc3 extends DrawCommand {

	private DesignLine referenceLine;

	private DesignArc previewArc;

	private Point3D start;

	private Point3D mid;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1 - Prompt for start
		if( parameters.length < 1 ) {
			addReference( tool, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "start-point" );
			return INCOMPLETE;
		}

		// Step 2 - Get start, prompt for mid-point
		if( parameters.length < 2 ) {
			start = asPoint( context, parameters[ 0 ] );
			referenceLine.setOrigin( start );
			promptForPoint( context, tool, "mid-point" );
			return INCOMPLETE;
		}

		// Step 3 - Get mid point, prompt for end
		if( parameters.length < 3 ) {
			removeReference( tool, referenceLine );

			mid = asPoint( context, parameters[ 1 ] );
			addPreview( tool, previewArc = CadGeometry.arcFromThreePoints( start, mid, mid ) );

			promptForPoint( context, tool, "end-point" );
			return INCOMPLETE;
		}

		reset( tool );

		setCaptureUndoChanges( tool, true );
		tool.getCurrentLayer().addShape( CadGeometry.arcFromThreePoints( start, mid, asPoint( context, parameters[ 2 ] ) ) );
		setCaptureUndoChanges( tool, false );

		return COMPLETE;
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );

			switch( getStep() ) {
				case 1 -> {
					referenceLine.setOrigin( point );
					referenceLine.setPoint( point );
				}
				case 2 -> referenceLine.setPoint( point );
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
