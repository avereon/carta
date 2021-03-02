package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;

public class CameraViewRotateLeft extends CameraCommand {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		double angle = tool.getViewRotate() + 45;
		if( angle > 180 ) angle -= 360;
		tool.setViewRotate( angle );

		return COMPLETE;
	}

}
