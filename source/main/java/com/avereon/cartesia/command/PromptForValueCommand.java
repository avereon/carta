package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.tool.DesignTool;

public class PromptForValueCommand extends PromptCommand {

	public PromptForValueCommand( DesignTool tool, String key ) {
		super( tool, key );
	}

	@Override
	public boolean isAutoCommandSafe() {
		return false;
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		super.evaluate( processor, tool );
	}

}
