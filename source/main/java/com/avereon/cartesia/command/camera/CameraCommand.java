package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.zerra.javafx.FxUtil;
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

	protected Object zoomShapes( CommandContext context, List<DesignShape> shapes ) {
		if( shapes.isEmpty() ) return SUCCESS;

		// Get the merged bounds of all the shapes
		Bounds bounds = null;
		for( DesignShape shape : shapes ) {
			bounds = FxUtil.merge( bounds, shape.getBounds() );
		}
		if( bounds == null ) return FAILURE;

		// Set the viewport to the bounds
		context.getTool().setWorldViewport( bounds );
		return SUCCESS;
	}

}
