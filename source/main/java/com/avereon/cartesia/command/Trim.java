package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

/**
 * Select a shape to extend/trim then select the shape to be the extend/trim edge.
 */
public class Trim extends Command {

	private static final System.Logger log = Log.get();

	private DesignShape trimShape;

	private Point3D trimMouse;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
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

		// FIXME Why is the trime shape the same as the first time?
		// And why does a clear work ... sometimes?
		System.out.println( "trim=" + trimShape );

		com.avereon.cartesia.math.Trim.trim( context.getTool(), trimShape, edgeShape, trimMouse, edgeMouse );

		return COMPLETE;
	}

}
