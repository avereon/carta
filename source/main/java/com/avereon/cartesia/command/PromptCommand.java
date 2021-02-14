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
		// FIXME This breaks auto-commands
		// This command should probably not set input command because it does not
		// know the scope of the command that called it. This command should
		// probably just return the current command context input mode.
		return true;
	}

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
		if( parameters.length == 0 ) {
			tool.getDesignContext().getCommandPrompt().setPrompt( prompt );
			return incomplete();
		}

		tool.getDesignContext().getCommandPrompt().clear();
		tool.setCursor( null );

		return parameters[ 0 ];
	}

}
