package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.zarra.javafx.FxUtil;
import javafx.geometry.Bounds;
import lombok.CustomLog;

import java.util.List;

@CustomLog
public abstract class CameraCommand extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	protected Object zoomShapes( CommandContext context, List<DesignShape> shapes ) {
		if( shapes.isEmpty() ) return COMPLETE;

		// Get the merged bounds of all the shapes
		Bounds bounds = null;
		for( DesignShape shape : shapes ) {
			bounds = FxUtil.merge( bounds, shape.getBounds() );
		}
		if( bounds == null ) return FAIL;

		// Convert the bounds from world to screen coordinates
		bounds = context.getTool().worldToScreen( bounds );

		// Set the viewport to the bounds
		context.getTool().setScreenViewport( bounds );
		return COMPLETE;
	}

}
