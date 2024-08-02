package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.DesignCommandContext;
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
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		// Should the trigger and the triggering event be part of the execute parameters?

		// FIXME The anchor is not sent to the execute method

		if( parameters.length < 1 ) {
			// Select window anchor
			promptForPoint( context, "select-point" );
			return INCOMPLETE;
		}

		if( parameters.length < 2 ) {
			Point3D point = asPoint( context, parameters[ 0 ] );
			context.getTool().worldPointSelect( point, isSelectToggle( trigger, triggerEvent ) );
			return SUCCESS;
		}

		return FAILURE;
	}

	private boolean isSelectToggle( CommandTrigger trigger, InputEvent event ) {
		// TODO This needs to match the modifiers in the trigger
		if( event instanceof MouseEvent mouseEvent) {
			return mouseEvent.isControlDown();
		}
		return false;
	}

}
