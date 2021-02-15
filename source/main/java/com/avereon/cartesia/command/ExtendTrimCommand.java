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
public class ExtendTrimCommand extends Command {

	private static final System.Logger log = Log.get();

	private DesignShape shape;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 && tool.selectedShapes().size() < 1 ) {
			promptForPoint( context, tool, "select-trim-shape" );
			return incomplete();
		}

		if( parameters.length < 2 && tool.selectedShapes().size() < 2) {
			tool.mouseSelect( (Point3D)parameters[0] );
			//shape = selectNearestShapeAtMouse( context, tool );
			//if( shape == DesignShape.NONE ) return invalid();
			promptForPoint( context, tool, "select-trim-edge" );
			return incomplete();
		}

		tool.mouseSelect( context.getMouse() );
//		DesignShape edge = selectNearestShapeAtMouse( context, tool );
//		if( edge == DesignShape.NONE ) return invalid();

		// TODO Extend/trim the shape to the edge

		return complete();
	}

}
