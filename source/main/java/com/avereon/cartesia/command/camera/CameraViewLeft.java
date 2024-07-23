package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.tool.CommandContext;

public class CameraViewLeft extends CameraCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		context.getTool().setViewRotate( 90 );
		return COMPLETE;
	}

}
