package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.DesignTool;

public class LayerDeleteCommand extends Command {

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		DesignLayer layer = tool.getCurrentLayer();
		layer.getParentLayer().removeLayer( layer );
	}

}
