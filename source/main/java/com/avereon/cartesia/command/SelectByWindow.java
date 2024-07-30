package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.CommandContext;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

@CustomLog
public class SelectByWindow extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) return COMPLETE;

		if( parameters[ 0 ] instanceof MouseEvent event ) {
			if( event.getEventType() == MouseEvent.DRAG_DETECTED ) {
				// This command is not complete until the mouse is released
				return INCOMPLETE;
			} else if( event.getEventType() == MouseEvent.MOUSE_RELEASED ) {
				// The command is complete when the handle method submits the command again with the mouse released event
				return COMPLETE;
			}
		}

		return FAIL;
	}

	@Override
	public void handle( CommandContext context, MouseEvent event ) {
		BaseDesignTool tool = (BaseDesignTool)event.getSource();
		Point3D anchor = context.getScreenMouse();
		Point3D mouse = new Point3D( event.getX(), event.getY(), event.getZ() );

		if( event.getEventType().equals( MouseEvent.MOUSE_DRAGGED ) ) {
			tool.setSelectAperture( anchor, mouse );
		} else if( event.getEventType().equals( MouseEvent.MOUSE_RELEASED ) ) {
			tool.screenWindowSelect( anchor, mouse, isSelectByIntersect( event ), false );
			tool.setSelectAperture( mouse, mouse );
			// The command needs to be submitted again with this event to complete
			tool.getCommandContext().submit( tool, this, event );
		}
	}

	private boolean isSelectByIntersect( MouseEvent event ) {
		// This needs to match the mouse flag in the trigger
		return event.isShiftDown();
	}

}
