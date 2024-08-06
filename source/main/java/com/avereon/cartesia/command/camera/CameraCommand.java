package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.zarra.javafx.FxUtil;
import javafx.geometry.Bounds;
import lombok.CustomLog;

import java.util.List;

import static com.avereon.cartesia.command.Command.Result.FAILURE;
import static com.avereon.cartesia.command.Command.Result.SUCCESS;

@CustomLog
public abstract class CameraCommand extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public boolean clearReferenceAndPreviewWhenComplete() {
		return false;
	}

	protected Object zoomShapes( DesignCommandContext context, List<DesignShape> shapes ) {
		if( shapes.isEmpty() ) return SUCCESS;

		// Get the merged bounds of all the shapes
		Bounds bounds = null;
		for( DesignShape shape : shapes ) {
			bounds = FxUtil.merge( bounds, shape.getBounds() );
		}
		if( bounds == null ) return FAILURE;

		// Convert the bounds from world to screen coordinates
		bounds = context.getTool().worldToScreen( bounds );

		// Set the viewport to the bounds
		context.getTool().setScreenViewport( bounds );
		return SUCCESS;
	}

}
