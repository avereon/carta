package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.base.Value;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class CameraMove extends CameraCommand {

	private Point3D originalViewPoint;

	private Transform originalTransform;

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

			originalViewPoint = task.getTool().getViewpoint();
			originalTransform = task.getTool().getScreenToWorldTransform().clone();

			// Submit a Value command to pass the anchor back to this command
			task.getContext().submit( task.getTool(), new Value(), task.getContext().getWorldAnchor() );
			return INCOMPLETE;
		}

		if( paramCount == 1 & noEvent ) {
			Point3D worldAnchor = asPoint( task, "pan-anchor", 0 );
			if( worldAnchor != null ) {
				promptForPoint( task, "pan-target" );
				return INCOMPLETE;
			}
		}

		// The situation of one parameter and an event should not occur

		if( paramCount == 2 & noEvent ) {
			Point3D worldAnchor = asPoint( task, "pan-anchor", 0 );
			Point3D worldCorner = asPoint( task, "pan-target", 1 );
			if( worldAnchor != null && worldCorner != null ) {
				Point3D worldOffset = worldAnchor.subtract( worldCorner );
				task.getTool().setViewpoint( originalViewPoint.add( worldOffset ) );
				return SUCCESS;
			}
		}

		return FAILURE;
	}

	@Override
	public void handle( CommandTask task, MouseEvent event ) {
		if( originalViewPoint == null ) return;
		if( originalTransform == null ) return;

		BaseDesignTool tool = (BaseDesignTool)event.getSource();
		Point3D anchor = task.getContext().getWorldAnchor();
		Point3D corner = originalTransform.transform( new Point3D( event.getX(), event.getY(), event.getZ() ) );

		if( event.getEventType().equals( MouseEvent.MOUSE_DRAGGED ) ) {
			Point3D worldOffset = anchor.subtract( corner );
			tool.setViewpoint( originalViewPoint.add( worldOffset ) );
			event.consume();
		} else if( event.getEventType().equals( MouseEvent.MOUSE_RELEASED ) ) {
			Point3D worldOffset = anchor.subtract( corner );
			task.getContext().submit( tool, new Value(), task.getContext().getWorldAnchor().subtract( worldOffset ) );
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
