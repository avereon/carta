package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.DesignTool;
import com.avereon.xenon.BundleKey;

public class InputCommand extends Command {

	private final String prompt;

	public InputCommand( String prompt ) {
		this.prompt = prompt;
	}

	public InputCommand( DesignTool tool, String key ) {
		this( tool.getProduct().rb().text( BundleKey.PROMPT, key ) );
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		tool.getCommandPrompt().setPrompt( prompt );
	}

}
