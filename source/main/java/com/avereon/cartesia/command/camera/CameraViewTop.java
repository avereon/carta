package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;
import static com.avereon.cartesia.command.Command.Result.*;

public class CameraViewTop extends CameraCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		context.getTool().setViewRotate( 0 );
		return SUCCESS;
	}

}

