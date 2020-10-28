package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class PenDownCommand extends Command {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
		// For this command the incoming parameter is the mouse event that triggered it
		MouseEvent event = (MouseEvent)parameters[0];

		Point3D point = tool.mouseToWorld( event.getX(), event.getY(), event.getZ() );
		context.setAnchor( point );
		return point;
	}

}
