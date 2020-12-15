package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;

/**
 * Select a shape to extend/trim then select the shape to be the extend/trim edge.
 */
public class ExtendTrimCommand extends Command {

	private static final System.Logger log = Log.get();

	private DesignShape shape;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			return incomplete();
		}

		if( parameters.length < 2 ) {
			shape = selectNearestShapeAtMouse( context, tool );
			return incomplete();
		}

		DesignShape edge = selectNearestShapeAtMouse( context, tool );

		// TODO Extend/trim the shape to the edge

		return complete();
	}

}
