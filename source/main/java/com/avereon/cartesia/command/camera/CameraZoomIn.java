package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.cartesia.tool.view.DesignPaneMarea;
import javafx.scene.input.InputEvent;
import static com.avereon.cartesia.command.Command.Result.*;

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
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) {
		zoomByFactor( context.getTool(), context.getTool().getViewPoint(), DesignPaneMarea.ZOOM_IN_FACTOR );
		return SUCCESS;
	}

}
