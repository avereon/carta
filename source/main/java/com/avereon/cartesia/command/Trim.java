package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

/**
 * Select a shape to extend/trim then select the shape to be the extend/trim edge.
 */
public class Trim extends Command {

	private static final System.Logger log = Log.get();

	private DesignShape trim;

	private Point3D trimMouse;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForShape( context, "select-trim-shape" );
			return INCOMPLETE;
		}

		if( parameters.length < 2 ) {
			trimMouse = context.getScreenMouse();
			trim = selectNearestShapeAtMouse( context, trimMouse );
			if( trim == DesignShape.NONE ) return INVALID;
			promptForShape( context, "select-trim-edge" );
			return INCOMPLETE;
		}

		Point3D edgeMouse = context.getScreenMouse();
		DesignShape edge = findNearestShapeAtMouse( context, edgeMouse );
		if( edge == DesignShape.NONE ) return INVALID;

		// Start an undo multi-change
		com.avereon.cartesia.math.Trim.trim( tool, trim, edge, trimMouse, edgeMouse );
		// Done with undo multi-change

		return COMPLETE;
	}

}
