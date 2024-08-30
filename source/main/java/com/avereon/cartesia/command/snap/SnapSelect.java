package com.avereon.cartesia.command.snap;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.snap.Snap;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class SnapSelect extends SnapCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		if( task.getParameterCount() == 0 ) throw new InvalidInputException( this, "snap", null );

		if( task.getParameterCount() == 1 ) {
			if( !(task.getParameter( 0 ) instanceof Snap) ) throw new InvalidInputException( this, "snap", null );
			promptForShape( task, "select-snap-shape" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 1 ) ) {
			Snap snap = (Snap)task.getParameter( 0 );
			Point3D point = asPoint( task, "select-snap-shape", 1 );
			Point3D snapPoint = snap.snap( task.getTool(), point );
			return snapPoint != CadPoints.NONE ? snapPoint : FAILURE;
		}

		return FAILURE;
	}

}
