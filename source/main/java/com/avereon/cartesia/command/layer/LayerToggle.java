package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.DesignTool;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class LayerToggle extends LayerCommand {

	@Override
	public Object execute( CommandTask task) throws Exception {
		DesignTool tool = task.getTool();
		DesignLayer layer = tool.getSelectedLayer();
		tool.setLayerVisible( layer, !tool.isLayerVisible( layer ) );
		return SUCCESS;
	}

}
