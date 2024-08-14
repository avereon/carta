package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.command.Value;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class CameraMove extends CameraCommand {

	private Point3D originalViewPoint;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		int paramCount = task.getParameters().length;
		InputEvent event = task.getEvent();
		boolean noEvent = event == null;
		boolean hasEvent = !noEvent;

		if( task.getParameterCount() == 0 & noEvent ) {
			promptForPoint( task, "pan-anchor" );
			return INCOMPLETE;
		}

		if( paramCount == 0 & hasEvent && event.getEventType() == MouseEvent.DRAG_DETECTED ) {
			event.consume();

			originalViewPoint = task.getTool().getViewPoint();

			// Submit a Value command to pass the anchor back to this command
			task.getContext().submit( task.getTool(), new Value(), task.getContext().getWorldAnchor() );
			return INCOMPLETE;
		}

		if( paramCount == 1 & noEvent ) {
			Point3D worldAnchor = asPoint( task,"pan-anchor", 0 );
			if( worldAnchor != null ) {
				promptForPoint( task, "pan-target" );
				return INCOMPLETE;
			}
		}

		// The situation of one parameter and an event should not occur

		if( paramCount == 2 & noEvent ) {
			Point3D worldAnchor = asPoint( task,"pan-anchor", 0 );
			Point3D worldCorner = asPoint( task,"pan-target", 1 );
			if( worldAnchor != null && worldCorner != null ) {
				task.getTool().setViewPoint( originalViewPoint.add( worldAnchor.subtract( worldCorner ) ) );
				return SUCCESS;
			}
		}

		return FAILURE;
	}

	@Override
	public void handle( CommandTask task, MouseEvent event ) {
		BaseDesignTool tool = (BaseDesignTool)event.getSource();
		Point3D anchor = task.getContext().getScreenAnchor();
		Point3D mouse = new Point3D( event.getX(), event.getY(), event.getZ() );

		if( event.getEventType().equals( MouseEvent.MOUSE_DRAGGED ) ) {
			Point3D screenOffset = anchor.subtract( mouse );
			Point3D worldOffset = tool.scaleScreenToWorld( new Point3D( screenOffset.getX(), -1.0 * screenOffset.getY(), 0 ) );
			tool.setViewPoint( originalViewPoint.add( worldOffset ) );
			event.consume();
		} else if( event.getEventType().equals( MouseEvent.MOUSE_RELEASED ) ) {
			Point3D screenOffset = anchor.subtract( mouse );
			Point3D worldOffset = tool.scaleScreenToWorld( new Point3D( screenOffset.getX(), -1.0 * screenOffset.getY(), 0 ) );
			tool.getCommandContext().submit( tool, new Value(), task.getContext().getWorldAnchor().subtract( worldOffset ) );
			event.consume();
		}
	}

	/**
	 * For testing purposes only.
	 *
	 * @param originalViewPoint The original view point
	 */
	void setOriginalViewPoint( Point3D originalViewPoint ) {
		this.originalViewPoint = originalViewPoint;
	}

}
