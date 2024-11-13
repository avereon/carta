package com.avereon.cartesia.command;

import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class SelectByPoint extends SelectCommand {

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

		if( hasEvent && event instanceof MouseEvent mouseEvent && task.getTrigger().matches( mouseEvent ) ) {
			// Otherwise, if there is an event, use that
			Point3D screenPoint = new Point3D( mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getZ() );
			event.consume();

			if( task.getContext().getCommandStackDepth() < 2 ) {
				task.getTool().screenPointSelect( screenPoint, toggle );
				return SUCCESS;
			} else {
				return task.getTool().screenToWorkplane( screenPoint );
			}
		}

		if( task.hasParameter( 0 ) ) {
			// If there is a parameter, use that
			Point3D worldPoint = asPointWithoutSnap( task, "select-point", 0 );

			if( task.getContext().getCommandStackDepth() < 2 ) {
				task.getTool().worldPointSelect( worldPoint, toggle );
				return SUCCESS;
			} else {
				return worldPoint;
			}
		}

		return FAILURE;
	}

}
