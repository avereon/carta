package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawLine2 extends DrawCommand {

	private static final System.Logger log = Log.get();

	private DesignLine preview;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1
		if( parameters.length < 1 ) {
			addPreview( tool, preview = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "start-point" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			preview.setOrigin( asPoint( context, parameters[ 0 ] ) );
			promptForPoint( context, tool, "end-point" );
			return INCOMPLETE;
		}

		preview.setPoint( asPoint( context, parameters[ 1 ] ) );
		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 1 -> {
					preview.setOrigin( point );
					preview.setPoint( point );
				}
				case 2 -> preview.setPoint( point );
			}
		}
	}

}
