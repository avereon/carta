package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.tool.view.DesignPaneMarea;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

/**
 * Zoom in to the design. This has the effect of making the design larger or
 * moving toward the design.
 *
 * <p>This command does not require any parameters. If no parameters are
 * provided it assumes the zoom will be centered around the current view point
 * and will use {@link DesignPaneMarea#ZOOM_IN_FACTOR} as the zoom factor.</p>
 */
public class CameraZoomIn extends CameraZoom {

	@Override
	public Object execute( CommandTask task) {
		zoomByFactor( task.getTool(), task.getTool().getViewpoint(), DesignPaneMarea.ZOOM_IN_FACTOR );
		return SUCCESS;
	}

}
