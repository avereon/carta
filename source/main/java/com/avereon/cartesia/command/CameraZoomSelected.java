package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.zerra.javafx.FxUtil;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Shape;

public class CameraZoomSelected extends CameraCommand  {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		Bounds shapeBounds = new BoundingBox( 0, 0, 0, 0 );
		for( Shape s : context.getTool().getSelectedShapes() ) {
			shapeBounds = FxUtil.merge( shapeBounds, s.getBoundsInParent() );
		}

		context.getTool().setViewport( shapeBounds );

		return COMPLETE;
	}

}
