package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class Rotate extends EditCommand {

	private DesignLine referenceLine;

	private Point3D center;

	private Point3D anchor;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		DesignTool tool = task.getTool();
		if( tool.getSelectedShapes().isEmpty() ) return SUCCESS;

		setCaptureUndoChanges( task, false );

		// Ask for a center point
		if( task.getParameterCount() == 0 ) {
			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			promptForPoint( task, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a start point
		if( task.getParameterCount() == 1 ) {
			center = asPoint( task, "anchor", 0 );

			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			referenceLine.setPoint( center ).setOrigin( center );

			promptForPoint( task, "start-point" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 1 ) ) {
			double angle = asDoubleOrNan( task, 1 );
			if( !Double.isNaN( angle ) ) {
				center = asPoint( task, "anchor", 0 );
				rotateShapes( tool, center, angle );
				return SUCCESS;
			}
		}

		if( task.getParameterCount() == 2 ) {
			anchor = asPoint( task, "start-point", 1 );

			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			referenceLine.setPoint( anchor ).setOrigin( center );

			createPreviewShapes( task, task.getTool().getSelectedShapes() );

			promptForPoint( task, "target" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 2 ) ) {
			setCaptureUndoChanges( task, true );

			Point3D a = asPoint( task, "anchor", 0 );
			Point3D s = asPoint( task, "start-point", 1 );
			Point3D t = asPoint( task, "target", 2 );

			// Start an undo multi-change
			rotateShapes( tool, a, s, t );
			// Done with undo multi-change

			return SUCCESS;
		}

		return FAILURE;
	}

	@Override
	public void handle( CommandTask task, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 1 -> referenceLine.setPoint( point ).setOrigin( point );
				case 2 -> referenceLine.setPoint( point );
				case 3 -> {
					referenceLine.setPoint( point );

					resetPreviewGeometry();
					rotateShapes( getPreview(), center, CadGeometry.pointAngle360( anchor, center, point ) );
				}
			}
		}
	}

}
