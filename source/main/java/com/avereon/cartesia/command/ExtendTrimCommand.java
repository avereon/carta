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

	private DesignShape trim;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForPoint( context, tool, "select-trim-shape" );
			return incomplete();
		}

		if( parameters.length < 2 ) {
			trim = selectNearestShapeAtPoint( tool, (Point3D)parameters[ 0 ] );
			if( trim == DesignShape.NONE ) return invalid();
			promptForPoint( context, tool, "select-trim-edge" );
			return incomplete();
		}

		tool.mousePointSelect( context.getMouse() );
		DesignShape edge = selectNearestShapeAtPoint( tool, (Point3D)parameters[ 1 ] );
		if( edge == DesignShape.NONE ) return invalid();

		log.log( Log.WARN, "shape-to-trim=" + trim );
		log.log( Log.WARN, "shape-as-edge=" + edge );

		// NEXT Extend/trim the shape to the edge

		tool.clearSelected();

		return complete();
	}

}
