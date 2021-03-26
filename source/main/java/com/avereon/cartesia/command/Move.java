package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class Move extends EditCommand {

	private DesignLine referenceLine;

	private Point3D anchor;

	private Point3D lastPoint;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( tool.selectedShapes().isEmpty() ) return COMPLETE;

		setCaptureUndoChanges( context, false );

		// Ask for an anchor point
		if( parameters.length < 1 ) {
			addReference( context, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a target point
		if( parameters.length < 2 ) {
			anchor = asPoint( context, parameters[ 0 ] );
			referenceLine.setPoint( anchor ).setOrigin( anchor );
			addPreview( context, context.getTool().getSelectedShapes() );
			promptForPoint( context, "target" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );

		// Move the selected shapes
		setCaptureUndoChanges( context, true );
		// Start an undo multi-change
		moveShapes( tool.getSelectedShapes(), asPoint( context, parameters[ 0 ] ), asPoint( context, parameters[ 1 ] ) );
		// Done with undo multi-change

		return COMPLETE;
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 1 -> referenceLine.setPoint( point ).setOrigin( point );
				case 2 -> {
					if( lastPoint == null ) lastPoint = anchor;
					referenceLine.setPoint( point );
					moveShapes( getPreview(), lastPoint, point );
					lastPoint = point;
				}
			}
		}
	}

}
