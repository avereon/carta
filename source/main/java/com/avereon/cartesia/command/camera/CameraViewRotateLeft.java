package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;

public class CameraViewRotateLeft extends CameraCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		double angle = context.getTool().getViewRotate() + 45;
		if( angle > 180 ) angle -= 360;
		context.getTool().setViewRotate( angle );

		return COMPLETE;
	}

}
