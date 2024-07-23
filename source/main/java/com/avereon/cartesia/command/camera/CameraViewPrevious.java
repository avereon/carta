package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.tool.CommandContext;

public class CameraViewPrevious extends CameraCommand {


	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		//context.getTool().setView( context.getPreviousViewpoint(), context.getPreviousZoom(), context.getPreviousRotate() );
		context.getTool().setView( context.getTool().getPriorPortal() );
		return COMPLETE;
	}


}
