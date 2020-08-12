package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.xenon.BundleKey;

public class PromptCommand extends Command {

	private final String prompt;

	@Deprecated
	public PromptCommand( String prompt ) {
		this.prompt = prompt;
	}

	public PromptCommand( DesignTool tool, String key ) {
		this( tool.getProduct().rb().text( BundleKey.PROMPT, key ) );
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		tool.getCommandPrompt().setPrompt( prompt );
	}

}
