package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class Copy extends EditCommand{
	private DesignLine previewLine;

	private Point3D anchor;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( tool.selectedShapes().isEmpty() ) return COMPLETE;

		// TODO Implement Copy.execute()

		// Ask for an anchor point
		// Step 1
		if( parameters.length < 1 ) {
			addPreview( tool, previewLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "anchor" );
			return INCOMPLETE;
		}

		// Make a shadow copy of the selected components to show them as a preview

		// Ask for a target point
		if( parameters.length < 2 ) {
			anchor = asPoint( context, parameters[ 0 ] );
			previewLine.setPoint( anchor ).setOrigin( anchor );
			promptForPoint( context, tool, "target" );
			return INCOMPLETE;
		}

		// Clear the preview
		resetPreview( tool );

		// Copy the selected shapes
		copyShapes( tool.getSelectedShapes(), asPoint( context, parameters[ 0 ] ), asPoint( context, parameters[ 1 ] ) );

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
			}
		}
	}

}
