package com.avereon.cartesia.command;

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
		context.getTool().setViewport( getShapeBounds( shapes ) );
		return COMPLETE;
	}

}
