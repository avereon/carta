package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignBox;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class DrawBox2 extends DrawCommand {

	private DesignLine reference;

	private DesignBox preview;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		// Step 1
		if( task.getParameterCount() == 0 ) {
			if( reference == null ) reference = createReferenceLine( task );
			promptForPoint( task, "anchor" );
			return INCOMPLETE;
		}

		// Step 2
		if( task.getParameterCount() == 1 ) {
			if( preview == null ) preview = createPreviewBox( task );
			Point3D origin = asPoint( task, "anchor", 0 );
			preview.setOrigin( origin );

			if( reference == null ) reference = createReferenceLine( task );
			reference.setPoint( origin ).setOrigin( origin );

			promptForPoint( task, "corner" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 1 ) ) {
			removeReference( task, reference );
			setCaptureUndoChanges( task, true );

			Point3D origin = asPoint( task, "anchor", 0 );
			Point3D point = asPoint( task, "corner", 1 );
			Point3D size = new Point3D( point.getX() - origin.getX(), point.getY() - origin.getY(), point.getZ() - origin.getZ() );

			// Start an undo multi-change
			task.getTool().getCurrentLayer().addShape( new DesignBox( origin, size ) );
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
				Point3D origin = preview.getOrigin();
				Point3D size = new Point3D( point.getX() - origin.getX(), point.getY() - origin.getY(), point.getZ() - origin.getZ() );
				if( step == 2 ) preview.setSize( size );
			}
		}
	}

}
