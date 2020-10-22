package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;

public class PromptCommand extends Command {

	private final String prompt;

	public PromptCommand( String prompt ) {
		this.prompt = prompt;
	}

	@Override
	public boolean isInputCommand() {
		return true;
	}

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
		if( parameters.length == 0 ) {
			context.getDesignContext().getCommandPrompt().setPrompt( prompt );
			return incomplete();
		}
		return parameters[ 0 ];
	}

}
