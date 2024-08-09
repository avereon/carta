package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.tool.CommandTask;

public class CameraZoomSelected extends CameraCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		return zoomShapes( task.getContext(), task.getTool().getSelectedShapes() );
	}

}
