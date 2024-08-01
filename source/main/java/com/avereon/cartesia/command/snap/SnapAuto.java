package com.avereon.cartesia.command.snap;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

public class SnapAuto extends SnapCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		Snap snap = (Snap)parameters[ 0 ];
		if( snap == null ) return null;

		BaseDesignTool tool = context.getTool();
		Point3D point = context.getWorldMouse();
		MouseEvent event = (MouseEvent)parameters[ 1 ];
		if( event != null ) point = tool.screenToWorld( event.getX(), event.getY(), event.getZ() );
		return snap.snap( tool, point );
	}

}
