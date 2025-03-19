package com.avereon.cartesia.command;

import com.avereon.cartesia.command.base.Prompt;
import com.avereon.cartesia.tool.DesignCommandContext;
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

			if( task.getContext().isSelectMode() ) {
				task.getTool().screenPointSelect( screenPoint, toggle );
				return SUCCESS;
			} else {
				if( isSelectingShape( task ) ) {
					return task.getTool().screenToWorld( screenPoint );
				} else {
					return task.getTool().screenToWorkplane( screenPoint );
				}
			}
		}

		if( task.hasParameter( 0 ) ) {
			// If there is a parameter, use that
			Point3D worldPoint = asPoint( task, "select-point", 0 );

			if( task.getContext().isSelectMode() ) {
				task.getTool().worldPointSelect( worldPoint, toggle );
				return SUCCESS;
			} else {
				return worldPoint;
			}
		}

		return FAILURE;
	}

	private boolean isSelectingShape( CommandTask task ) {
		CommandTask priorTask = task.getPrior();
		Command priorCommand = priorTask == null ? null : priorTask.getCommand();
		return priorCommand instanceof Prompt && priorCommand.getInputMode() == DesignCommandContext.Input.SHAPE;
	}

}
