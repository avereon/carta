package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class Rotate extends EditCommand {

	private DesignLine referenceLine;

	private Point3D center;

	private Point3D anchor;

	private double angle;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( tool.selectedShapes().isEmpty() ) return COMPLETE;

		setCaptureUndoChanges( context, false );

		// Ask for a center point
		if( parameters.length < 1 ) {
			addReference( context, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "center" );
			return INCOMPLETE;
		}

		// Ask for a start point
		if( parameters.length < 2 ) {
			center = asPoint( context, parameters[ 0 ] );
			referenceLine.setPoint( center ).setOrigin( center );
			promptForPoint( context, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a target point
		if( parameters.length < 3 ) {
			anchor = asPoint( context, parameters[ 1 ] );
			referenceLine.setPoint( anchor ).setOrigin( center );
			addPreview( context, tool.getSelectedShapes() );
			promptForPoint( context, "target" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );

		// Move the selected shapes
		setCaptureUndoChanges( context, true );

		try {
			center = asPoint( context, parameters[ 0 ] );
			anchor = asPoint( context, parameters[ 1 ] );
			Point3D point = asPoint( context, parameters[ 2 ] );

			// Start an undo multi-change
			rotateShapes( tool.getSelectedShapes(), center, anchor, point );
			// Done with undo multi-change
		} catch( Exception exception ) {
			// Cancel multi-change
		}

		return COMPLETE;
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 1 -> referenceLine.setPoint( point ).setOrigin( point );
				case 2 -> referenceLine.setPoint( point );
				case 3 -> {
					double oldAngle = angle;
					referenceLine.setPoint( point );
					angle = CadGeometry.pointAngle360( anchor, center, point );
					rotateShapes( getPreview(), center, angle - oldAngle );
				}
			}
		}
	}

}
