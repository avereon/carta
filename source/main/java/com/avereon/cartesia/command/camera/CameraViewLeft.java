package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class CameraViewLeft extends CameraCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		context.getTool().setViewRotate( 90 );
		return SUCCESS;
	}

}
