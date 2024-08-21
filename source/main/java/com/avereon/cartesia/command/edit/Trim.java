package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignShape;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

/**
 * Select a shape to extend/trim then select the shape to be the extend/trim edge.
 */
@CustomLog
public class Trim extends EditCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		if( task.getParameterCount() == 0 ) {
			promptForShape( task, "select-trim-shape" );
			return INCOMPLETE;
		}

		if( task.getParameterCount() == 1 ) {
			Point3D trimPoint = asPoint( task, "select-trim-shape", 0, false );
			DesignShape trimShape = selectNearestShapeAtPoint( task, trimPoint );
			if( trimShape == DesignShape.NONE ) return SUCCESS;

			promptForShape( task, "select-trim-edge" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 1 ) ) {
			Point3D trimPoint = asPoint( task, "select-trim-shape", 0, false );
			Point3D edgePoint = asPoint( task, "select-trim-edge", 1, false );

			DesignShape trimShape = selectNearestShapeAtPoint( task, trimPoint );
			DesignShape edgeShape = findNearestShapeAtPoint( task, edgePoint );

			Point3D trimMouse = task.getTool().worldToScreen( trimPoint );
			Point3D edgeMouse = task.getTool().worldToScreen( edgePoint );

			setCaptureUndoChanges( task, true );

			com.avereon.cartesia.math.Trim.trim( task.getTool(), trimShape, edgeShape, trimMouse, edgeMouse );

			return SUCCESS;
		}

		return FAILURE;
	}

}
