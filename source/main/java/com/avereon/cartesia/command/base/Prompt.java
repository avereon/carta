package com.avereon.cartesia.command.base;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.tool.CommandContext;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;

@CustomLog
public class Prompt extends Command {

	private final String prompt;

	private final CommandContext.Input mode;

	public Prompt( String prompt, CommandContext.Input mode ) {
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
	public CommandContext.Input getInputMode() {
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
