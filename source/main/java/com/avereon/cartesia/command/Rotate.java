package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class Rotate extends EditCommand {

	private DesignLine previewLine;

	private Point3D center;

	private Point3D anchor;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( tool.selectedShapes().isEmpty() ) return COMPLETE;

		// Ask for a center point
		if( parameters.length < 1 ) {
			addPreview( tool, previewLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "center" );
			return INCOMPLETE;
		}

		// TODO Make a shadow copy of the selected components to show them as a preview

		// Ask for a start point
		if( parameters.length < 2 ) {
			center = asPoint( context, parameters[ 0 ] );
			previewLine.setPoint( center ).setOrigin( center );
			promptForPoint( context, tool, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a target point
		if( parameters.length < 3 ) {
			anchor = asPoint( context, parameters[ 0 ] );
			previewLine.setPoint( anchor ).setOrigin( center );
			promptForPoint( context, tool, "target" );
			return INCOMPLETE;
		}

		// Clear the preview
		clearPreview( tool );

		// Move the selected shapes
		rotateShapes( tool.getSelectedShapes(), asPoint( context, parameters[ 0 ] ), asPoint( context, parameters[ 1 ] ), asPoint( context, parameters[ 2 ] ) );

		return COMPLETE;
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
				case 3 -> previewLine.setPoint( point );
			}
		}
	}

}
