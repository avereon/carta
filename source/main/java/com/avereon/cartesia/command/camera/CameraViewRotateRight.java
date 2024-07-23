package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.tool.CommandContext;

public class CameraViewRotateRight extends CameraCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		double angle = context.getTool().getViewRotate() - 45;
		if( angle < 180 ) angle += 360;
		context.getTool().setViewRotate( angle );

		return COMPLETE;
	}

}
