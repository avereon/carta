package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;

public class DrawPath extends DrawCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		// This one will be fun because it can be very powerful
		// Once started it can take SVG-like parameters to draw a path
		// Of particular interest is that it can be open or closed

		return SUCCESS;
	}

}
