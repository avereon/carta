package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignPane;
import com.avereon.cartesia.tool.DesignTool;

/**
 * Zoom out from the design. This has the effect of making the design smaller or
 * moving away from the design.
 *
 * <p>This command does not require any parameters. If no parameters are
 * provided it assumes the zoom will be centered around the current view point
 * and will use {@link DesignPane#ZOOM_OUT_FACTOR} as the zoom factor.</p>
 */
public class CameraZoomOutCommand extends CameraZoomCommand {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
		zoomByFactor( tool, DesignPane.ZOOM_OUT_FACTOR, parameters );
		return complete();
	}

}