package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignBox;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import static com.avereon.cartesia.command.Command.Result.*;

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

			promptForPoint( task, "point" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 1 ) ) {
			removeReference( task, reference );
			setCaptureUndoChanges( task, true );

			Point3D origin = asPoint( task, "anchor", 0 );
			Point3D point = asPoint( task, "point", 1 );
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
		if( preview != null && event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D origin = preview.getOrigin();
			Point3D point = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );
			Point3D size = new Point3D( point.getX() - origin.getX(), point.getY() - origin.getY(), point.getZ() - origin.getZ() );
			switch( getStep() ) {
				case 1 -> reference.setPoint( point ).setOrigin( point );
				case 2 -> preview.setSize( size );
			}
		}
	}

}
