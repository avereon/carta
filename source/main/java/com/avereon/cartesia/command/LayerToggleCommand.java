package com.avereon.cartesia.command;

import com.avereon.cartesia.OldCommand;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.DesignTool;

public class LayerToggleCommand extends OldCommand {

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		DesignLayer layer = tool.getCurrentLayer();
		tool.setLayerVisible( layer, !tool.isLayerVisible( layer ) );
	}

}
