package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.zerra.javafx.FxUtil;
import javafx.geometry.Bounds;
import javafx.scene.shape.Shape;

import java.util.List;

public abstract class CameraCommand extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	protected Object zoomShapes( CommandContext context, List<Shape> shapes ) {
		if( shapes.isEmpty() ) return COMPLETE;

		Bounds shapeBounds = shapes.get(0).getBoundsInParent();
		for( Shape s : shapes ) {
			shapeBounds = FxUtil.merge( shapeBounds, s.getBoundsInParent() );
		}

		context.getTool().setViewport( shapeBounds );

		return COMPLETE;
	}

}
