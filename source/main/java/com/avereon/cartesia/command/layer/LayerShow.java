package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.command.CommandTask;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class LayerShow extends LayerCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		task.getTool().setLayerVisible( task.getTool().getSelectedLayer(), true );
		return SUCCESS;
	}

}
