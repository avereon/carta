package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.DesignTool;

import java.util.List;

public class LineCommand extends Command {

	@Override
	public List<Command> getPreSteps(DesignTool tool) {
		return List.of( new InputCommand( tool, "start-point" ), new InputCommand( tool, "end-point" ) );
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {

	}

}
