package com.avereon.cartesia.command.base;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import static com.avereon.cartesia.command.Command.Result.*;

public class Anchor extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public boolean clearReferenceAndPreviewWhenComplete() {
		return false;
	}

	public Object execute( CommandTask task ) throws Exception {
		if( task.getParameters().length < 1 && task.getEvent() == null ) {
			promptForPoint( task, "select-point" );
			return INCOMPLETE;
		}

		if( task.getEvent() instanceof MouseEvent event && task.getTrigger().matches( event ) ) {
			Point3D screenPoint = new Point3D( event.getX(), event.getY(), event.getZ() );
			Point3D worldPoint = task.getTool().screenToWorld( screenPoint );
			task.getContext().setLocalAnchor( screenPoint );
			task.getContext().setWorldAnchor( worldPoint );
			event.consume();
			return SUCCESS;
		}

		if( task.hasParameter( 0 ) ) {
			Point3D worldPoint = asPoint( task, "select-point", 0 );
			Point3D screenPoint = task.getTool().worldToScreen( worldPoint );
			if( worldPoint != null ) {
				task.getContext().setLocalAnchor( screenPoint );
				return SUCCESS;
			}
		}

		return FAILURE;
	}

}
