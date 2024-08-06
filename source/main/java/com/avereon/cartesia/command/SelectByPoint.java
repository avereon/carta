package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class SelectByPoint extends SelectCommand {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandTask task ) throws Exception {
		return execute( task, false );
	}

	protected Object execute( CommandTask task, boolean toggle ) throws Exception {
		int paramCount = task.getParameters().length;
		InputEvent event = task.getEvent();
		boolean noEvent = event == null;
		boolean hasEvent = !noEvent;

		if( paramCount < 1 & noEvent ) {
			// Select window anchor
			promptForPoint( task, "select-point" );
			return INCOMPLETE;
		}

		if( hasEvent && task.getTrigger().matches( event ) && event instanceof MouseEvent mouseEvent ) {
			// Otherwise, if there is an event, use that
			Point3D screenPoint = new Point3D( mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getZ() );

			// Apparently asking for the command stack depth can cause a thread lock
			if( task.getContext().getCommandStackDepth() < 2 ) {
				task.getTool().screenPointSelect( screenPoint, toggle );
			} else {
				return task.getTool().screenToWorld( screenPoint );
			}
			return SUCCESS;
		}

		if( task.hasParameter( 0 ) ) {
			// If there is a parameter, use that
			Point3D worldPoint = asPoint( task, task.getParameter( 0 ) );
			if( worldPoint != null ) {
				// Apparently asking for the command stack depth can cause a thread lock
				if( task.getContext().getCommandStackDepth() < 2 ) {
					task.getTool().worldPointSelect( worldPoint, toggle );
				} else {
					return worldPoint;
				}
				return SUCCESS;
			}
		}

		return FAILURE;
	}

}
