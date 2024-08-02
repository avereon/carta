package com.avereon.cartesia.command.snap;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import lombok.CustomLog;
import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class SnapSelect extends SnapCommand {

	private Snap snap;

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) return SUCCESS;

		if( parameters.length < 2 ) {
			snap = (Snap)parameters[ 0 ];
			promptForShape( context, "select-snap-shape" );
			return INCOMPLETE;
		}

		Point3D point = asPoint( context.getWorldAnchor(), parameters[ 1 ] );
		Point3D snapPoint = snap.snap( context.getTool(), point );
		return snapPoint != CadPoints.NONE ? snapPoint : SUCCESS;
	}

	@Override
	public String toString() {
		return snap == null ? super.toString() : snap.getClass().getSimpleName();
	}

}
