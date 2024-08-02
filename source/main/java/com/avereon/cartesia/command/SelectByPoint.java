package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandTrigger;
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
		if( task.getParameters().length < 1 && task.getEvent() == null ) {
			// Select window anchor
			promptForPoint( task.getContext(), "select-point" );
			return INCOMPLETE;
		}

		if( task.getParameters().length < 2 || task.getEvent() != null ) {
			// TODO Might consider collapsing this logic if it is duplicated in other commands
			if( task.getParameters().length == 1 ) {
				// If there is a parameter, use that
				Point3D worldPoint = asPoint( task, task.getParameters()[ 0 ] );
				task.getTool().worldPointSelect( worldPoint, isSelectToggle( task.getTrigger(), task.getEvent() ) );
				return SUCCESS;
			} else if( task.getTrigger().matches( task.getEvent() ) && task.getEvent() instanceof MouseEvent event ) {
				// Otherwise, if there is an event, use that
				Point3D screenPoint = new Point3D( event.getX(), event.getY(), event.getZ() );
				task.getTool().screenPointSelect( screenPoint, isSelectToggle( task.getTrigger(), task.getEvent() ) );
				return SUCCESS;
			}
		}

		return FAILURE;
	}

	private boolean isSelectToggle( CommandTrigger trigger, InputEvent event ) {
		// FIXME This needs to match the modifiers in the trigger
		if( event instanceof MouseEvent mouseEvent ) {
			return mouseEvent.isControlDown();
		}
		return false;
	}

}
