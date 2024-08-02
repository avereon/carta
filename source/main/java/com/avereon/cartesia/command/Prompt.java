package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;

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
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		DesignTool tool = context.getTool();

		if( tool == null ) return INVALID;

		if( parameters.length == 0 ) {
			tool.getDesignContext().getDesignCommandContext().getCommandPrompt().setPrompt( prompt );
			return INCOMPLETE;
		}

		tool.getDesignContext().getDesignCommandContext().getCommandPrompt().clear();
		tool.setCursor( null );

		return parameters[ 0 ];
	}

}
