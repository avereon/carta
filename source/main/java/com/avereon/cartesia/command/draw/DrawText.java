package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignText;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class DrawText extends DrawCommand {

	private DesignLine reference;

	private DesignText preview;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		// Ask for an anchor point
		if( task.getParameterCount() == 0 ) {
			if( reference == null ) reference = createReferenceLine( task );
			promptForPoint( task, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a target point
		if( task.getParameterCount() == 1 ) {
			if( preview == null ) preview = createPreviewText( task );
			Point3D anchor = asPoint( task, "anchor", 0 );
			preview.setOrigin( anchor );

			promptForText( task, "text" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 1 ) ) {
			removeReference( task, reference );
			setCaptureUndoChanges( task, true );

			Point3D anchor = asPoint( task, "anchor", 0 );
			String text = asText( task, "text", 1 );
			task.getTool().getCurrentLayer().addShape( new DesignText( anchor, text ) );

			return SUCCESS;
		}

		return FAILURE;
	}

	@Override
	public void handle( CommandTask task, KeyEvent event ) {
		if( this.preview != null ) preview.setText( task.getContext().getCommandPrompt().getCommand() );
	}

	@Override
	public void handle( CommandTask task, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D mouse = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );
			if( getStep() == 1 ) {
				reference.setOrigin( mouse );
				reference.setPoint( mouse );
			}
		}
	}

}
