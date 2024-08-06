package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.command.Value;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.CommandTask;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class CameraMove extends CameraCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		int paramCount = task.getParameters().length;
		InputEvent event = task.getEvent();
		boolean noEvent = event == null;
		boolean hasEvent = !noEvent;

		if( task.getParameterCount() == 0 & noEvent ) {
			promptForPoint( task, "pan-point" );
			return INCOMPLETE;
		}

		if( paramCount == 0 & hasEvent && event.getEventType() == MouseEvent.DRAG_DETECTED ) {
			// Submit a Value command to pass the anchor back to this command
			task.getContext().submit( task.getTool(), new Value(), task.getContext().getWorldAnchor() );
			return INCOMPLETE;
		}

		if( paramCount == 1 & noEvent ) {
			Point3D worldPoint = asPoint( task, task.getParameter( 0 ) );
			if( worldPoint != null ) {
				task.getContext().setScreenAnchor( task.getTool().worldToScreen( worldPoint ) );
				task.getContext().setWorldAnchor( worldPoint );
				promptForPoint( task, "pan-point" );
				return INCOMPLETE;
			}
		}

		// The situation of one parameter and an event should not occur

		if( paramCount == 2 & noEvent ) {
			Point3D worldAnchor = asPoint( task, task.getParameter( 0 ) );
			Point3D worldCorner = asPointFromEventOrParameter( task, event, task.getParameter( 1 ) );
			if( worldAnchor != null && worldCorner != null ) {
				task.getTool().pan( worldAnchor, worldCorner, task.getContext().getScreenMouse() );
				return SUCCESS;
			}
		}

		return FAILURE;
	}

	@Override
	public void handle( DesignCommandContext context, MouseEvent event ) {
		BaseDesignTool tool = (BaseDesignTool)event.getSource();
		Point3D anchor = context.getScreenMouse();
		Point3D mouse = new Point3D( event.getX(), event.getY(), event.getZ() );

		if( event.getEventType().equals( MouseEvent.MOUSE_DRAGGED ) ) {
			tool.pan( context.getWorldAnchor(), anchor, mouse );
		} else if( event.getEventType().equals( MouseEvent.MOUSE_RELEASED ) ) {
			// Submit a Value command to pass the point back to this command
			tool.getCommandContext().submit( tool, new Value(), tool.screenToWorld( mouse ) );
		}
	}

}
