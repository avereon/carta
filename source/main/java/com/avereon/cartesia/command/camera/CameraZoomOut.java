package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.tool.BaseDesignTool;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

/**
 * Zoom out from the design. This has the effect of making the design smaller or
 * moving away from the design.
 *
 * <p>This command does not require any parameters. If no parameters are
 * provided it assumes the zoom will be centered around the current view point
 * and will use {@link BaseDesignTool#ZOOM_OUT_FACTOR} as the zoom factor.</p>
 */
public class CameraZoomOut extends CameraZoom {

	@Override
	public Object execute( CommandTask task ) {
		zoomByFactor( task.getTool(), task.getTool().getViewCenter(), BaseDesignTool.ZOOM_OUT_FACTOR );
		return SUCCESS;
	}

}
