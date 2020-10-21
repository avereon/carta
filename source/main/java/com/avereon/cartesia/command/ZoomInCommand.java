package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignPane;
import com.avereon.cartesia.tool.DesignTool;

/**
 * Zoom in to the design. This has the effect of making the design larger or
 * moving toward the design.
 *
 * <p>This command does not require any parameters. If no parameters are
 * provided it assumes the zoom will be centered around the current view point
 * and will use {@link DesignPane#ZOOM_IN_FACTOR} as the zoom factor.</p>
 */
public class ZoomInCommand extends ZoomCommand {

	@Override
	public void execute( CommandContext context, DesignTool tool, Object... parameters ) {
		zoomByFactor( tool, DesignPane.ZOOM_IN_FACTOR, parameters );
	}

}
