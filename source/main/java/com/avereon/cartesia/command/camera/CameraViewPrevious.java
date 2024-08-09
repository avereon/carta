package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.tool.CommandTask;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class CameraViewPrevious extends CameraCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		task.getTool().setView( task.getTool().getPriorPortal() );
		return SUCCESS;
	}

}
