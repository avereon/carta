package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

public class Anchor extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) return COMPLETE;

		if( parameters[ 0 ] instanceof MouseEvent event && event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
			Point3D screen = new Point3D( event.getX(), event.getY(), event.getZ() );
			Point3D world = context.getTool().screenToWorld( screen );
			Point3D workplane = context.getTool().screenToWorkplane( world );
			context.setScreenAnchor( screen );
			context.setWorldAnchor( world );
			//context.setWorkplaneAnchor( workplane );

			// Intentionally don't return a point here,
			// the select commands will handle that
		}

		return COMPLETE;
	}

}
