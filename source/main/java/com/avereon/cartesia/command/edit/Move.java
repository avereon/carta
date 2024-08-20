package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLine;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import static com.avereon.cartesia.command.Command.Result.*;

public class Move extends EditCommand {

	private DesignLine referenceLine;

	private Point3D anchor;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		if( task.getTool().getSelectedShapes().isEmpty() ) return SUCCESS;

		setCaptureUndoChanges( task, false );

		// Ask for an anchor point
		if( task.getParameterCount() == 0 ) {
			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			promptForPoint( task, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a target point
		if( task.getParameterCount() == 1 ) {
			anchor = asPoint( task, "anchor", 0 );

			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			referenceLine.setPoint( anchor ).setOrigin( anchor );

			createPreviewShapes( task, task.getTool().getSelectedShapes() );

			promptForPoint( task, "target" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 1 ) ) {
			setCaptureUndoChanges( task, true );

			Point3D anchor = asPoint( task, "anchor", 0 );
			Point3D target = asPoint( task, "target", 1 );

			moveShapes( task.getTool(), anchor, target );

			return SUCCESS;
		}

		return FAILURE;
	}

	@Override
	public void handle( CommandTask task, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			Point3D point = task.getTool().screenToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 1 -> referenceLine.setPoint( point ).setOrigin( point );
				case 2 -> {
					referenceLine.setPoint( point ).setOrigin( anchor );
					resetPreviewGeometry();
					moveShapes( getPreview(), anchor, point );
				}
			}
		}
	}

}
