package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.Trim;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

/**
 * Select a shape to extend/trim then select the shape to be the extend/trim edge.
 */
public class ExtendTrimCommand extends Command {

	private static final System.Logger log = Log.get();

	private DesignShape trim;

	private Point3D trimMouse;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForPoint( context, tool, "select-trim-shape" );
			return incomplete();
		}

		if( parameters.length < 2 ) {
			trimMouse = context.getScreenMouse();
			trim = selectNearestShapeAtMouse( tool, trimMouse );
			if( trim == DesignShape.NONE ) return invalid();
			promptForPoint( context, tool, "select-trim-edge" );
			return incomplete();
		}

		Point3D edgeMouse = context.getScreenMouse();
		DesignShape edge = selectNearestShapeAtMouse( tool, edgeMouse );
		if( edge == DesignShape.NONE ) return invalid();

		tool.clearSelected();
		Trim.trim( tool, trim, edge, trimMouse, edgeMouse );

		return complete();
	}

}
