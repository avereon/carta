package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawLineCommand extends DrawCommand {

	private static final System.Logger log = Log.get();

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1
		if( parameters.length < 1 ) {
			promptForPoint( context, tool, "start-point" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			setPreview( tool, new DesignLine( asPoint( tool, parameters[ 0 ], context.getAnchor() ), context.getWorldMouse() ) );
			promptForPoint( context, tool, "end-point" );
			return INCOMPLETE;
		}

		((DesignLine)getPreview()).setPoint( asPoint( tool, parameters[ 1 ], context.getAnchor() ) );
		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignLine preview = getPreview();
			if ( preview != null ) {
				DesignTool tool = (DesignTool)event.getSource();
				Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
				switch( getStep() ) {
					case 2 -> preview.setPoint( point );
				}
			}
		}
	}

}
