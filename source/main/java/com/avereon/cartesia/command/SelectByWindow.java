package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.CommandTask;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public abstract class SelectByWindow extends SelectCommand {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	protected Object execute( CommandTask task, boolean toggle ) throws Exception {
		System.out.println( "trigger=" +task.getTrigger() + " event=" + CommandTrigger.from( task.getEvent() ) );
		if( task.getParameters().length == 0 ) {
			if( task.getEvent() == null ) {
				// Select window anchor
				promptForWindow( task.getContext(), "select-window-anchor" );
				return INCOMPLETE;
			} else if( task.getTrigger().matches( task.getEvent() ) && task.getEvent() instanceof MouseEvent event && event.getEventType() == MouseEvent.DRAG_DETECTED ) {
				// The mouse event indicates that the mouse was dragged
				// and the command can use the world anchor as the first parameter

				System.out.println( "Setting anchor from drag detected event" );

				// This pushes a Value command to the stack, which will return the
				// world anchor as the first parameter to this command.
				task.getContext().submit( task.getTool(), new Value(), task.getContext().getWorldAnchor() );
				return INCOMPLETE;
			} else {
				System.out.println("Nothing matched up :-(");
			}
		}

		if( task.getParameters().length == 1 ) {
			if( task.getEvent() == null ) {
				// If there is a parameter, use that as the first parameter
				Point3D worldPoint = asPoint( task, task.getParameters()[ 0 ] );
				if( worldPoint != null ) {
					task.getContext().setScreenAnchor( task.getTool().worldToScreen( worldPoint ) );
					task.getContext().setWorldAnchor( worldPoint );
					promptForWindow( task.getContext(), "select-window-point" );
					return INCOMPLETE;
				}
			} else if( task.getTrigger().matches( task.getEvent() ) && task.getEvent() instanceof MouseEvent event && event.getEventType() == MouseEvent.MOUSE_RELEASED ) {
				// The mouse event indicates that the mouse was released
				// and the command can use the mouse point as the second parameter
				Point3D screenPoint = new Point3D( event.getX(), event.getY(), event.getZ() );
				Point3D worldPoint = task.getTool().screenToWorld( screenPoint );

				// With both points, the command can now select the objects
				task.getTool().worldWindowSelect( (Point3D)task.getParameters()[0], worldPoint, false, toggle );

				return SUCCESS;
			}

		}

//		if( task.getParameters().length < 3 || task.getEvent() != null ) {
//			// TODO Might consider collapsing this logic if it is duplicated in other commands
//			if( task.getParameters().length == 2 ) {
//			} else if( task.getTrigger().matches( task.getEvent() ) && task.getEvent() instanceof MouseEvent event ) {
//
//			}
//		}

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

}
