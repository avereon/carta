package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.DesignCommandContext;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;

@CustomLog
public class Prompt extends Command {

	private final String prompt;

	private final DesignCommandContext.Input mode;

	public Prompt( String prompt, DesignCommandContext.Input mode ) {
		this.prompt = prompt;
		this.mode = mode;
	}

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public boolean clearReferenceAndPreviewWhenComplete() {
		return false;
	}

	@Override
	public DesignCommandContext.Input getInputMode() {
		return mode;
	}

	@Override
	public Object execute( CommandTask task ) throws Exception {
		if( task.getParameterCount() == 0 ) {
			task.getContext().getCommandPrompt().setPrompt( prompt );
			return INCOMPLETE;
		} else {
			task.getContext().getCommandPrompt().clear();
			task.getTool().setCursor( null );
			return task.getParameters();
		}
	}

}
