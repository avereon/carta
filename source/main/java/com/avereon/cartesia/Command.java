package com.avereon.cartesia;

import com.avereon.cartesia.tool.DesignTool;

import java.util.List;

public abstract class Command {

	public List<Command> getPreSteps( DesignTool tool) {
		return List.of();
	}

	public void evaluate( CommandProcessor processor, DesignTool tool ) throws CommandException {}

}
