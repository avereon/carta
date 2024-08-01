package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import lombok.CustomLog;

@CustomLog
public class Join extends EditCommand {

	private DesignShape trim;

	private Point3D trimMouse;

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForShape( context, "select-meet-shape" );
			return INCOMPLETE;
		}

		if( parameters.length < 2 ) {
			trimMouse = context.getScreenMouse();
			trim = selectNearestShapeAtMouse( context, trimMouse );
			if( trim == DesignShape.NONE ) return INVALID;
			promptForShape( context, "select-meet-shape" );
			return INCOMPLETE;
		}

		Point3D edgeMouse = context.getScreenMouse();
		DesignShape edge = findNearestShapeAtMouse( context, edgeMouse );
		if( edge == DesignShape.NONE ) return INVALID;

		try {
			com.avereon.cartesia.math.Meet.meet( context.getTool(), trim, edge, trimMouse, edgeMouse );
		} catch( Exception exception ) {
			log.atWarn( exception ).log( "Error meeting objects" );
			return FAIL;
		}

		return COMPLETE;
	}

}
