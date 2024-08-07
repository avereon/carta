package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.CommandTask;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public abstract class SelectByWindow extends SelectCommand {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	protected Object execute( CommandTask task, boolean intersect ) throws Exception {
		int paramCount = task.getParameters().length;
		InputEvent event = task.getEvent();
		boolean noEvent = event == null;
		boolean hasEvent = !noEvent;

		// Nothing to do but prompt for the anchor point
		if( paramCount == 0 & noEvent ) {
			// Select window anchor
			promptForWindow( task, "select-window-anchor" );
			return INCOMPLETE;
		}

		// If there is an event, but no parameters, use the world anchor as the first parameter
		if( paramCount == 0 & hasEvent && event.getEventType() == MouseEvent.DRAG_DETECTED ) {
			// Submit a Value command to pass the anchor back to this command
			task.getContext().submit( task.getTool(), new Value(), task.getContext().getWorldAnchor() );
			event.consume();
			return INCOMPLETE;
		}

		// Get the world anchor point from the first parameter
		if( paramCount == 1 & noEvent ) {
			Point3D worldPoint = asPoint( task, task.getParameter( 0 ) );
			if( worldPoint != null ) {
				task.getContext().setScreenAnchor( task.getTool().worldToScreen( worldPoint ) );
				task.getContext().setWorldAnchor( worldPoint );
				promptForWindow( task, "select-window-point" );
				return INCOMPLETE;
			}
		}

		// The situation of one parameter and an event should not occur

		// Get the world point from the event or the second parameter
		if( paramCount == 2 ) {
			Point3D worldAnchor = asPoint( task, 0 );
			Point3D worldCorner = asPoint( task, 1 );

			// NEXT Should this command return an array of points for the window if
			//  the stack is not empty? If so, how should the command be modified?

			if( worldAnchor != null && worldCorner != null ) {
				task.getTool().worldWindowSelect( worldAnchor, worldCorner, intersect, false );
				return SUCCESS;
			}
		}

		return FAILURE;
	}

	@Override
	public void handle( DesignCommandContext context, MouseEvent event ) {
		BaseDesignTool tool = (BaseDesignTool)event.getSource();
		Point3D anchor = context.getScreenAnchor();
		Point3D mouse = new Point3D( event.getX(), event.getY(), event.getZ() );

		if( event.getEventType().equals( MouseEvent.MOUSE_DRAGGED ) ) {
			tool.setSelectAperture( anchor, mouse );
			event.consume();
		} else if( getStep() == 3 && event.getEventType().equals( MouseEvent.MOUSE_MOVED ) ) {
			// FIXME How did we get on step 3? I expected it to be step 2.
			//  Because there is an Anchor command in between the two steps.
			tool.setSelectAperture( anchor, mouse );
			event.consume();
		} else if( event.getEventType().equals( MouseEvent.MOUSE_RELEASED ) ) {
			// Submit a Value command to pass the point back to this command
			tool.getCommandContext().submit( tool, new Value(), tool.screenToWorld( mouse ) );
			event.consume();
		}
	}

}
