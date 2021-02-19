package com.avereon.cartesia.command;

import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

public class SnapSelectCommand extends SnapCommand {

	private static final System.Logger log = Log.get();

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) return INVALID;

		if( parameters.length < 2 ) {
			promptForShape( context, tool, "select-snap-shape" );
			return incomplete();
		}

		try {
			Snap snap = (Snap)parameters[ 0 ];
			Point3D point = asPoint( tool, parameters[ 1 ], context.getAnchor() );
			return snap.snap( tool, point );
		} finally {
			tool.clearSelected();
		}
	}

}
