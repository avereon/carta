package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.CommandTask;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.zarra.javafx.FxUtil;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

/**
 * Zoom the view to the window defined by two points. The order of the points is
 * not important. The view will be zoomed to the window defined by the points.
 * </p>
 *
 * <h2>Interactive Usage</h2>
 * <p>
 * In interactive mode, the user is prompted to define the window by choosing
 * an anchor point and dragging the window to a second point.
 * </p>
 *
 * <h2>Script Usage</h2>
 * <p>
 * In script mode the user specifies both points on the command line. Example
 * command:
 * </p>
 * <p>
 * <code>ZW -4,3 2,7</code>
 * </p>
 * <p>
 * This command zooms the view to the window defined by the two points (-4,3) and (2,7).
 * </p>
 */
@CustomLog
public class CameraZoomWindow extends CameraCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		doNotCaptureUndoChanges( task );

		int paramCount = task.getParameters().length;
		InputEvent event = task.getEvent();
		boolean noEvent = event == null;
		boolean hasEvent = !noEvent;

		if( paramCount == 0 ) {
			// Zoom window anchor
			promptForWindow( task, "zoom-window-anchor" );
			return INCOMPLETE;
		}

		// Get the world anchor point from the first parameter
		if( paramCount == 1 ) {
			Point3D worldPoint = asPoint( task, task.getParameter( 0 ) );
			if( worldPoint != null ) {
				task.getContext().setScreenAnchor( task.getTool().worldToScreen( worldPoint ) );
				task.getContext().setWorldAnchor( worldPoint );
				promptForWindow( task, "zoom-window-corner" );
				return INCOMPLETE;
			}
		}

		// Get the world point from the second parameter
		if( paramCount == 2 ) {
			Point3D worldAnchor = asPoint( task, 0 );
			Point3D worldCorner = asPoint( task, 1 );
			if( worldAnchor != null && worldCorner != null ) {
				task.getTool().setWorldViewport( FxUtil.bounds( worldAnchor, worldCorner ) );
				return SUCCESS;
			}
		}

		return FAILURE;
	}

	@Override
	public void handle( DesignCommandContext context, MouseEvent event ) {
		BaseDesignTool tool = (BaseDesignTool)event.getSource();
		Point3D anchor = context.getScreenAnchor();
		Point3D mouse = new Point3D( event.getX(), event.getY(), event.getZ() );

		//		if( event.getEventType().equals( MouseEvent.MOUSE_DRAGGED ) ) {
		//			tool.setSelectAperture( anchor, mouse );
		//		} else

		if( getStep() == 2 && event.getEventType().equals( MouseEvent.MOUSE_MOVED ) ) {
			tool.setSelectAperture( anchor, mouse );
		}

		//			else if( event.getEventType().equals( MouseEvent.MOUSE_RELEASED ) ) {
		//			// Submit a Value command to pass the point back to this command
		//			tool.getCommandContext().submit( tool, new Value(), tool.screenToWorld( mouse ) );
		//		}
	}

}
