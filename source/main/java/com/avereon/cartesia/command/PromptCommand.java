package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;

public class PromptCommand extends Command {

	private final String prompt;

	private final boolean isText;

	public PromptCommand( String prompt, boolean isText ) {
		this.prompt = prompt;
		this.isText = isText;
	}

	@Override
	public boolean isInputCommand() {
		return isText;
	}

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
		if( tool == null ) return COMPLETE;

		if( parameters.length == 0 ) {
			tool.getDesignContext().getCommandPrompt().setPrompt( prompt );
			return INCOMPLETE;
		}

		tool.getDesignContext().getCommandPrompt().clear();
		tool.setCursor( null );

		return parameters[ 0 ];
	}

}
