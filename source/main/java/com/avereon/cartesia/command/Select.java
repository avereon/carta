package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.CommandContext;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

@CustomLog
public class Select extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) return COMPLETE;

		BaseDesignTool tool = context.getTool();

		if( parameters[ 0 ] instanceof MouseEvent event ) {
			Point3D mouse = new Point3D( event.getX(), event.getY(), event.getZ() );

			if( event.getEventType() == MouseEvent.MOUSE_RELEASED ) {
				tool.screenPointSelect( mouse, isSelectToggle( event ) );
				return COMPLETE;
			}
		}

		return FAIL;
	}

	private boolean isSelectToggle( MouseEvent event ) {
		// This needs to match the mouse flag in the trigger
		return event.isControlDown();
	}

}
