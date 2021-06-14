package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;

public class Prompt extends Command {

	private final String prompt;

	private final CommandContext.Input mode;

	public Prompt( String prompt, CommandContext.Input mode ) {
		this.prompt = prompt;
		this.mode = mode;
	}

	@Override
	public CommandContext.Input getInputMode() {
		return mode;
	}

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		DesignTool tool = context.getTool();

		if( tool == null ) return INVALID;

		if( parameters.length == 0 ) {
			tool.getDesignContext().getCommandPrompt().setPrompt( prompt );
			return INCOMPLETE;
		}

		tool.getDesignContext().getCommandPrompt().clear();
		tool.setCursor( null );

		return parameters[ 0 ];
	}

}
