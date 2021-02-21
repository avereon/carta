package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawCircle2 extends DrawCommand {

	private static final System.Logger log = Log.get();

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1
		if( parameters.length < 1 ) {
			promptForPoint( context, tool, "center" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			setPreview( tool, new DesignEllipse( asPoint( tool, parameters[ 0 ], context.getAnchor() ), 0.0 ) );
			promptForPoint( context, tool, "radius" );
			return INCOMPLETE;
		}

		((DesignEllipse)getPreview()).setRadius( asDouble( getPreview().getOrigin(), parameters[ 1 ] ) );
		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignEllipse preview = getPreview();
			if( preview != null ) {
				DesignTool tool = (DesignTool)event.getSource();
				Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
				switch( getStep() ) {
					case 2 -> preview.setRadius( point.distance( preview.getOrigin() ) );
				}
			}
		}
	}

}
