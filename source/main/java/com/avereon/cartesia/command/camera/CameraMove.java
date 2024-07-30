package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.CommandContext;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

@CustomLog
public class CameraMove extends CameraCommand {

	private Point3D viewAnchor;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForPoint( context, "pan-point" );
			return INCOMPLETE;
		}

		if( parameters[ 0 ] instanceof MouseEvent event ) {
			if( event.getEventType() == MouseEvent.DRAG_DETECTED ) {
				viewAnchor = context.getTool().getViewPoint();
				return INCOMPLETE;
			} else if( event.getEventType() == MouseEvent.MOUSE_RELEASED ) {
				return COMPLETE;
			}
		}

		return FAIL;
	}

	@Override
	public void handle( CommandContext context, MouseEvent event ) {
		BaseDesignTool tool = (BaseDesignTool)event.getSource();
		Point3D anchor = context.getScreenMouse();
		Point3D mouse = new Point3D( event.getX(), event.getY(), event.getZ() );

		if( event.getEventType().equals( MouseEvent.MOUSE_DRAGGED ) ) {
			tool.pan( viewAnchor, anchor, mouse );
		} else if( event.getEventType().equals( MouseEvent.MOUSE_RELEASED ) ) {
			// The command needs to be submitted again with this event to complete
			tool.getCommandContext().submit( tool, this, event );
		}
	}

}
