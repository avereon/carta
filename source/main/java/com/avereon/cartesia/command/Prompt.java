package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandTask;
import com.avereon.cartesia.tool.DesignCommandContext;

import static com.avereon.cartesia.command.Command.Result.FAILURE;
import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;

public class Prompt extends Command {

	private final String prompt;

	private final DesignCommandContext.Input mode;

	public Prompt( String prompt, DesignCommandContext.Input mode ) {
		this.prompt = prompt;
		this.mode = mode;
	}

	@Override
	public DesignCommandContext.Input getInputMode() {
		return mode;
	}

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandTask task ) throws Exception {

		if( task.getParameterCount() == 0 ) {
			task.getContext().getCommandPrompt().setPrompt( prompt );
			return INCOMPLETE;
		}

		if( task.hasParameter( 0 ) ) {
			task.getContext().getCommandPrompt().clear();
			task.getTool().setCursor( null );
			return task.getParameter( 0 );
		}

		return FAILURE;
	}

}
