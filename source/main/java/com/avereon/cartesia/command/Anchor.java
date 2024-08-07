package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import static com.avereon.cartesia.command.Command.Result.*;

public class Anchor extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	public Object execute( CommandTask task ) throws Exception {
		if( task.getParameters().length < 1 && task.getEvent() == null ) {
			promptForPoint( task, "select-point" );
			return INCOMPLETE;
		}

		if( task.getEvent() instanceof MouseEvent event && task.getTrigger().matches( event ) ) {
			// Otherwise, if there is an event, use that
			Point3D screenPoint = new Point3D( event.getX(), event.getY(), event.getZ() );
			Point3D worldPoint = task.getTool().screenToWorld( screenPoint );
			task.getContext().setScreenAnchor( screenPoint );
			task.getContext().setWorldAnchor( worldPoint );
			event.consume();
			return SUCCESS;
		}

		if( task.hasParameter( 0 ) ) {
			Point3D worldPoint = asPoint( task, 0 );
			Point3D screenPoint = task.getTool().worldToScreen( worldPoint );
			if( worldPoint != null ) {
				task.getContext().setScreenAnchor( screenPoint );
				task.getContext().setWorldAnchor( worldPoint );
				return SUCCESS;
			}
		}

		return FAILURE;
	}

}
