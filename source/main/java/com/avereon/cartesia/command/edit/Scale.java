package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import static com.avereon.cartesia.command.Command.Result.*;

public class Scale extends EditCommand {

	private DesignLine referenceLine;

	private Point3D anchor;

	private Point3D source;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		DesignTool tool = task.getTool();

		if( tool.getSelectedShapes().isEmpty() ) return SUCCESS;

		setCaptureUndoChanges( task, false );

		// Ask for an anchor point
		if( task.getParameterCount() == 0 ) {
			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			promptForPoint( task, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a start point
		if( task.getParameterCount() == 1 ) {
			anchor = asPoint( task, "anchor", 0 );

			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			referenceLine.setPoint( anchor ).setOrigin( anchor );

			promptForPoint( task, "reference" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 1 ) ) {
			double scale = asDoubleOrNan( task, 1 );
			if( !Double.isNaN( scale ) ) {
				Point3D anchor = asPoint( task, "anchor", 0 );
				scaleShapes( tool, anchor, scale );
				return SUCCESS;
			}
		}

		// Ask for a target point
		if( task.getParameterCount() == 2 ) {
			anchor = asPoint( task, "anchor", 0 );
			source = asPoint( task, "reference", 1 );

			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			referenceLine.setPoint( source ).setOrigin( anchor );

			createPreviewShapes( task, task.getTool().getSelectedShapes() );

			promptForPoint( task, "target" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 2 ) ) {
			setCaptureUndoChanges( task, true );

			Point3D anchor = asPoint( task, "anchor", 0 );
			Point3D reference = asPoint( task, "reference", 1 );
			Point3D point = asPoint( task, "target", 2 );

			// Start an undo multi-change
			scaleShapes( tool, anchor, reference, point );
			// Done with undo multi-change

			return SUCCESS;
		}

		return FAILURE;
	}

	@Override
	public void handle( CommandTask task, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D target = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 1 -> referenceLine.setPoint( target ).setOrigin( target );
				case 2 -> referenceLine.setPoint( target ).setOrigin( anchor );
				case 3 -> {
					referenceLine.setPoint( target ).setOrigin( anchor );

					resetPreviewGeometry();
					scaleShapes( getPreview(), anchor, source, target );
				}
			}
		}
	}

}
