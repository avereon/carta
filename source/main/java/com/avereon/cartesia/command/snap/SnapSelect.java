package com.avereon.cartesia.command.snap;

import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.tool.CommandContext;
import javafx.geometry.Point3D;
import lombok.CustomLog;

@CustomLog
public class SnapSelect extends SnapCommand {

	private Snap snap;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) return COMPLETE;

		if( parameters.length < 2 ) {
			snap = (Snap)parameters[ 0 ];
			promptForShape( context, "select-snap-shape" );
			return INCOMPLETE;
		}

		Point3D point = asPoint( context.getWorldAnchor(), parameters[ 1 ] );
		Point3D snapPoint = snap.snap( context.getTool(), point );
		return snapPoint != CadPoints.NONE ? snapPoint : COMPLETE;
	}

	@Override
	public String toString() {
		return snap == null ? super.toString() : snap.getClass().getSimpleName();
	}

}
