package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public abstract class SelectByWindow extends SelectCommand {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	protected Object execute( CommandTask task, boolean intersect ) throws Exception {
		int paramCount = task.getParameters().length;
		InputEvent event = task.getEvent();
		boolean noEvent = event == null;
		boolean hasEvent = !noEvent;

		// Nothing to do but prompt for the anchor point
		if( paramCount == 0 & noEvent ) {
			// Select window anchor
			promptForWindow( task, "select-window-anchor" );
			return INCOMPLETE;
		}

		// If there is an event, but no parameters, use the world anchor as the first parameter
		if( paramCount == 0 & hasEvent && event.getEventType() == MouseEvent.DRAG_DETECTED ) {
			// Submit a Value command to pass the anchor back to this command
			task.getContext().submit( task.getTool(), new Value(), task.getContext().getWorldAnchor() );
			return INCOMPLETE;
		}

		// Get the world anchor point from the first parameter
		if( paramCount == 1 & noEvent ) {
			Point3D worldPoint = asPoint( task, "select-window-anchor", 0 );
			if( worldPoint != null ) {
				promptForWindow( task, "select-window-point" );
				return INCOMPLETE;
			}
		}

		// The situation of one parameter and an event should not occur

		// Get the world point from the event or the second parameter
		if( paramCount == 2 ) {
			Point3D worldAnchor = asPoint( task, "select-window-anchor", 0 );
			Point3D worldCorner = asPoint( task, "select-window-point", 1 );
			if( worldAnchor != null && worldCorner != null ) {
				if( task.getContext().getCommandStackDepth() < 2 ) {
					task.getTool().worldWindowSelect( worldAnchor, worldCorner, intersect, false );
					return SUCCESS;
				} else {
					return new Point3D[]{ worldAnchor, worldCorner };
				}
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
			tool.setSelectAperture( anchor, mouse );
			event.consume();
		} else if( getStep() == 2 && event.getEventType().equals( MouseEvent.MOUSE_MOVED ) ) {
			tool.setSelectAperture( anchor, mouse );
			event.consume();
		} else if( event.getEventType().equals( MouseEvent.MOUSE_RELEASED ) ) {
			// Submit a Value command to pass the point back to this command
			tool.getCommandContext().submit( tool, new Value(), tool.screenToWorld( mouse ) );
			event.consume();
		}
	}

}
