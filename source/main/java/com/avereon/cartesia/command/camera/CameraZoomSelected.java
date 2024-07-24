package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.tool.CommandContext;

public class CameraZoomSelected extends CameraCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		return zoomShapes( context, context.getTool().selectedFxShapes() );
	}

}
