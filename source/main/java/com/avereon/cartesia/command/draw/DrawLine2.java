package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.CommandTask;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class DrawLine2 extends DrawCommand {

	private DesignLine preview;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		// Step 1
		if( task.getParameterCount() == 0 ) {
			if( preview == null ) preview = createPreviewLine( task );
			promptForPoint( task, "start-point" );
			return INCOMPLETE;
		}

		// Step 2
		if( task.getParameterCount() == 1 ) {
			if( preview == null ) preview = createPreviewLine( task );
			Point3D origin = asPoint( task, 0 );
			if( origin == null ) return INVALID;
			preview.setOrigin( origin );
			promptForPoint( task, "end-point" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 1 ) ) {
			clearReferenceAndPreview( task );
			setCaptureUndoChanges( task, true );

			Point3D origin = asPoint( task, 0 );
			if( origin == null ) return INVALID;
			Point3D point = asPoint( task, 1 );
			if( point == null ) return INVALID;

			// Start an undo multi-change
			task.getTool().getCurrentLayer().addShape( new DesignLine( origin, point ) );
			// Done with undo multi-change
			return SUCCESS;
		}

		return FAILURE;
	}

	@Override
	public void handle( DesignCommandContext context, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D point = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 1 -> preview.setPoint( point ).setOrigin( point );
				case 2 -> preview.setPoint( point );
			}
		}
	}

	private DesignLine createPreviewLine( CommandTask task ) {
		DesignLine line = new DesignLine( task.getContext().getWorldMouse(), task.getContext().getWorldMouse() );
		addPreview( task, setAttributesFromLayer( line, task.getTool().getCurrentLayer() ) );
		return line;
	}

}
