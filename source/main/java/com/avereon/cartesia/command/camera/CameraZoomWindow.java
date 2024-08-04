package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.tool.CommandTask;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static com.avereon.cartesia.command.Command.Result.SUCCESS;

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
	public Object execute( CommandTask task) throws Exception {
		setCaptureUndoChanges( task.getContext(), false );

		if( task.getParameters().length < 1 ) {
			// Zoom window anchor
			promptForWindow( task, "zoom-window" );
			//promptForWindow( task.getContext(), "zoom-window-anchor" );
			return INCOMPLETE;
		}

		if( task.getParameters().length < 2 ) {
			// Zoom window point
			//promptForWindow( task.getContext(), "zoom-window-point" );
			return INCOMPLETE;
		}

		Point3D anchor = asPoint( task.getContext(), task.getParameter( 0 ) );
		Point3D mouse = asPoint( task.getContext(), task.getParameter( 1 ) );

		// FIXME Because I changed the behavior of Anchor not to return a point, this is broken
		//task.getTool().setWorldViewport( FxUtil.bounds( anchor, mouse ) );

		return SUCCESS;
	}

}
