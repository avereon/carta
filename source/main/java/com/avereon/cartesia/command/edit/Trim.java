package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import lombok.CustomLog;
import static com.avereon.cartesia.command.Command.Result.*;

/**
 * Select a shape to extend/trim then select the shape to be the extend/trim edge.
 */
@CustomLog
public class Trim extends EditCommand {

	private Point3D trimMouse;

	private DesignShape trimShape;

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		if( parameters.length < 1 ) {
			promptForShape( context, "select-trim-shape" );
			return INCOMPLETE;
		}

		if( parameters.length < 2 ) {
			trimMouse = context.getScreenMouse();
			trimShape = selectNearestShapeAtMouse( context, trimMouse );
			if( trimShape == DesignShape.NONE ) return INVALID;
			promptForShape( context, "select-trim-edge" );
			return INCOMPLETE;
		}

		Point3D edgeMouse = context.getScreenMouse();
		DesignShape edgeShape = findNearestShapeAtMouse( context, edgeMouse );
		if( edgeShape == DesignShape.NONE ) return INVALID;

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		com.avereon.cartesia.math.Trim.trim( context.getTool(), trimShape, edgeShape, trimMouse, edgeMouse );

		return SUCCESS;
	}

}
