package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.tool.DesignTool;

public class LayerHideCommand extends Command {

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		tool.setLayerVisible( tool.getCurrentLayer(), false );
	}

}
