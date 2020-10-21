package com.avereon.cartesia.command;

import com.avereon.cartesia.OldCommand;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.tool.DesignTool;

public class LayerShowCommand extends OldCommand {

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		tool.setLayerVisible( tool.getCurrentLayer(), true );
	}

}
