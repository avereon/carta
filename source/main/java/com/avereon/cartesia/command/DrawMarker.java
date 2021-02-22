package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignMarker;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawMarker extends DrawCommand {

	private static final System.Logger log = Log.get();

	private DesignMarker preview;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			// Need to start with the point at ZERO until it is added
			addPreview( tool, preview = new DesignMarker( Point3D.ZERO ) );
			preview.setOrigin( context.getWorldMouse() );
			promptForPoint( context, tool, "select-point" );
			return INCOMPLETE;
		}

		preview.setOrigin( asPoint( tool, parameters[ 0 ], context.getAnchor() ) );
		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D mouse = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			if( getStep() == 1 ) preview.setOrigin( mouse );
		}
	}

}
