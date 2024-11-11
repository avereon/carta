package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignMarker;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class DrawMarker extends DrawCommand {

	private DesignMarker preview;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		if( task.getParameterCount() == 0 ) {
			if( preview == null ) preview = createPreviewMarker( task );
			promptForPoint( task, "select-point" );
			return INCOMPLETE;
		}

		// FIXME If the parameter came from a snap, it should not snap to the grid
		if( task.hasParameter( 0 ) ) {
			setCaptureUndoChanges( task, true );

			Point3D point = asPoint( task, "select-point", 0 );
			task.getTool().getCurrentLayer().addShape( new DesignMarker( point ) );

			return SUCCESS;
		}

		return FAILURE;
	}

	@Override
	public void handle( CommandTask task, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D mouse = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );
			if( getStep() == 1 ) preview.setOrigin( mouse );
		}
	}

}
