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
			addPreview( task.getContext(), preview = new DesignLine( task.getContext().getWorldMouse(), task.getContext().getWorldMouse() ) );
			promptForPoint( task, "start-point" );
			return INCOMPLETE;
		}

		// Step 2
		if( task.getParameterCount() < 2 ) {
			preview.setOrigin( asPoint( task, 0 ) );
			promptForPoint( task, "end-point" );
			return INCOMPLETE;
		}

		if( task.getParameterCount() == 2 ) {
			clearReferenceAndPreview( task );
			setCaptureUndoChanges( task, true );

			Point3D origin = asPoint( task, 0 );
			Point3D point = asPoint( task, 1 );

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

	/**
	 * For testing purposes only.
	 *
	 * @param preview The preview line
	 */
	void setPreview( DesignLine preview ) {
		this.preview = preview;
	}

}
