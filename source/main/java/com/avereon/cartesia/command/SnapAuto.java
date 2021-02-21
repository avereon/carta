package com.avereon.cartesia.command;

import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class SnapAuto extends SnapCommand {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
		Snap snap = (Snap)parameters[ 0 ];
		if( snap == null ) return null;

		Point3D point = context.getWorldMouse();
		MouseEvent event = (MouseEvent)parameters[ 1 ];
		if( event != null ) point = tool.mouseToWorld( event.getX(), event.getY(), event.getZ() );
		return snap.snap( tool, point );
	}

}
