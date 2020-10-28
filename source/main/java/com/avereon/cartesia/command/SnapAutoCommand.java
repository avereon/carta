package com.avereon.cartesia.command;

import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.scene.input.MouseEvent;

public class SnapAutoCommand extends SnapCommand {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
		Snap snap = (Snap)parameters[0];
		if( snap == null ) return null;
		MouseEvent event = (MouseEvent)parameters[1];
		return snap.snap( tool, tool.mouseToWorld( event.getX(), event.getY(), event.getZ() ) );
	}

}
