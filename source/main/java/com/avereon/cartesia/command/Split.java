package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.CommandContext;
import javafx.geometry.Point3D;
import lombok.CustomLog;

@CustomLog
public class Split extends Command {

	private DesignShape splitShape;

	private Point3D splitMouse;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		if( parameters.length < 1 ) {
			promptForShape( context, "select-split-shape" );
			return INCOMPLETE;
		}

		if( parameters.length < 2 ) {
			Point3D mousePoint = context.getScreenMouse();
			splitShape = selectNearestShapeAtMouse( context, mousePoint );
			if( splitShape == DesignShape.NONE ) return INVALID;
			promptForShape( context, "select-split-point" );
			return INCOMPLETE;
		}

		splitMouse = context.getWorldMouse();

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		com.avereon.cartesia.math.Split.split( context.getTool(), splitShape, splitMouse );

		return COMPLETE;
	}

}

