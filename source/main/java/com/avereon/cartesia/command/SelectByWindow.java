package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.FAILURE;
import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;

@CustomLog
public class SelectByWindow extends SelectCommand {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		// Should the trigger and the triggering event be part of the execute parameters?

		// FIXME The anchor is not sent to the execute method

		if( parameters.length < 1 ) {
			// Select window anchor
			promptForWindow( context, "select-window-anchor" );
			return INCOMPLETE;
		}

		if( parameters.length < 2 ) {
			// Select window point
			promptForWindow( context, "select-window-point" );
			return INCOMPLETE;
		}

		//		if( parameters[ 0 ] instanceof MouseEvent event ) {
		//			if( event.getEventType() == MouseEvent.DRAG_DETECTED ) {
		//				// This command is not complete until the mouse is released
		//				return INCOMPLETE;
		//			} else if( event.getEventType() == MouseEvent.MOUSE_RELEASED ) {
		//				// The command is complete when the handle method submits the command again with the mouse released event
		//				BaseDesignTool tool = context.getTool();
		//				Point3D anchor = context.getWorldAnchor();
		//				Point3D mouse = tool.screenToWorld( event.getX(), event.getY(), 0 );
		//
		//				if( context.getCommandStackDepth() == 1 ) {
		//					tool.worldWindowSelect( anchor, mouse, isSelectByIntersect( event ), false );
		//					return COMPLETE;
		//				} else {
		//					return mouse;
		//				}
		//			}
		//		}

		return FAILURE;
	}

	@Override
	public void handle( DesignCommandContext context, MouseEvent event ) {
		BaseDesignTool tool = (BaseDesignTool)event.getSource();
		Point3D anchor = context.getScreenMouse();
		Point3D mouse = new Point3D( event.getX(), event.getY(), event.getZ() );

		if( event.getEventType().equals( MouseEvent.MOUSE_DRAGGED ) ) {
			tool.setSelectAperture( anchor, mouse );
		} else if( event.getEventType().equals( MouseEvent.MOUSE_RELEASED ) ) {
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
