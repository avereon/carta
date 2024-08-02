package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class Anchor extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	public Object execute( CommandTask task ) throws Exception {
		if( task.getParameters().length < 1 && task.getEvent() == null ) {
			// Prompt user for anchor point
			return INCOMPLETE;
		}

		if( task.getParameters().length < 2 || task.getEvent() != null ) {
			// TODO Might consider collapsing this logic if it is duplicated in other commands
			if( task.getParameters().length == 1 ) {
				// If there is a parameter, use that
				Point3D worldPoint = asPoint( task, task.getParameters()[ 0 ] );
				task.getContext().setScreenAnchor( task.getTool().worldToScreen( worldPoint ) );
				task.getContext().setWorldAnchor( worldPoint );
				return SUCCESS;
			} else if( task.getEvent() instanceof MouseEvent event ) {
				Point3D screenPoint = new Point3D( event.getX(), event.getY(), event.getZ() );
				Point3D worldPoint = task.getTool().screenToWorld( screenPoint );
				task.getContext().setScreenAnchor( screenPoint );
				task.getContext().setWorldAnchor( worldPoint );
				return SUCCESS;
			}
		}

		return FAILURE;
	}

}
