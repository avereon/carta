package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class DrawLine2 extends DrawCommand {

	private DesignLine reference;

	private DesignLine preview;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		// Step 1
		if( task.getParameterCount() == 0 ) {
			if( reference == null ) reference = createReferenceLine( task );
			promptForPoint( task, "start-point" );
			return INCOMPLETE;
		}

		// Step 2
		if( task.getParameterCount() == 1 ) {
			Point3D origin = asPoint( task, "start-point", 0 );

			if( reference == null ) reference = createReferenceLine( task );
			reference.setPoint( origin ).setOrigin( origin );

			if( preview == null ) preview = createPreviewLine( task );
			preview.setOrigin( origin );

			promptForPoint( task, "end-point" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 1 ) ) {
			removeReference( task, reference );
			setCaptureUndoChanges( task, true );

			Point3D origin = asPoint( task, "start-point", 0 );
			Point3D point = asPoint( task, "end-point", 1 );

			// Start an undo multi-change
			task.getTool().getCurrentLayer().addShape( new DesignLine( origin, point ) );
			// Done with undo multi-change
			return SUCCESS;
		}

		return FAILURE;
	}

	@Override
	public void handle( CommandTask task, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D point = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );
			int step = getStep();
			if( reference != null ) {
				if( step == 1 ) reference.setPoint( point ).setOrigin( point );
			}
			if( preview != null ) {
				if( step == 2 ) preview.setPoint( point );
			}
		}
	}

}
