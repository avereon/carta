package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.tool.CommandContext;
import javafx.scene.shape.Shape;

import java.util.List;

public abstract class CameraCommand extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	protected Object zoomShapes( CommandContext context, List<Shape> shapes ) {
		if( shapes.isEmpty() ) return COMPLETE;
		context.getTool().setViewport( getParentShapeBounds( shapes, context.getTool() ) );
		return COMPLETE;
	}

}
