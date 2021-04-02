package com.avereon.cartesia.command;

import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

public class SnapSelect extends SnapCommand {

	private static final System.Logger log = Log.get();

	private Snap snap;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) return INVALID;

		if( parameters.length < 2 ) {
			snap = (Snap)parameters[ 0 ];
			promptForShape( context, "select-snap-shape" );
			return INCOMPLETE;
		}

		Point3D point = asPoint( context.getAnchor(), parameters[ 1 ] );
		Point3D snapPoint = snap.snap( context.getTool(), point );
		return snapPoint != CadPoints.NONE ? snapPoint : INVALID;
	}

	@Override
	public String toString() {
		return snap == null ? super.toString() : snap.getClass().getSimpleName();
	}

}
