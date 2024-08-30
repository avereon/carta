package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignShape;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class Split extends EditCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		if( task.getParameters().length == 0 ) {
			promptForShape( task, "select-split-shape" );
			return INCOMPLETE;
		}

		if( task.getParameters().length == 1 ) {
			Point3D shapePoint = asPoint( task, "select-split-shape", 0, false );
			DesignShape shape = selectNearestShapeAtPoint( task, shapePoint );
			if( shape == null ) return SUCCESS;
			promptForPoint( task, "select-split-point" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 1 ) ) {
			setCaptureUndoChanges( task, true );

			Point3D shapePoint = asPoint( task, "select-split-shape", 0, false );
			Point3D splitPoint = asPoint( task, "select-split-point", 1 );
			DesignShape shape = selectNearestShapeAtPoint( task, shapePoint );

			com.avereon.cartesia.math.Split.split( shape, splitPoint );

			return SUCCESS;
		}

		return FAILURE;
	}

}
