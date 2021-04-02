package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.zerra.javafx.FxUtil;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Shape;

public class CameraZoomAll extends CameraCommand {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		return execute( context, parameters );
	}

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		Bounds shapeBounds = new BoundingBox( 0, 0, 0, 0 );
		for( Shape s : context.getTool().getVisibleShapes() ) {
			shapeBounds = FxUtil.merge( shapeBounds, s.getBoundsInParent() );
		}

		context.getTool().setViewport( shapeBounds );

		return COMPLETE;
	}

}
