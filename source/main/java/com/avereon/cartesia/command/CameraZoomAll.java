package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.zerra.javafx.Fx;
import com.avereon.zerra.javafx.FxUtil;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

public class CameraZoomAll extends CameraCommand {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		Bounds toolBounds = tool.getLayoutBounds();

		Bounds shapeBounds = new BoundingBox( 0, 0, 0, 0 );
		for( Shape s : context.getTool().getVisibleShapes() ) {
			shapeBounds = FxUtil.merge( shapeBounds, s.getBoundsInParent() );
		}

		Point3D worldCenter = new Point3D( shapeBounds.getCenterX(), shapeBounds.getCenterY(), shapeBounds.getCenterZ() );
		shapeBounds = tool.worldToScreen( shapeBounds );

		double xZoom = toolBounds.getWidth() / shapeBounds.getWidth();
		double yZoom = toolBounds.getHeight() / shapeBounds.getHeight();
		double zoom = Math.min( xZoom, yZoom );

		// FIXME This logic works fine, just doesn't repaint correctly
		Fx.run( () -> {
			tool.setViewPoint( worldCenter );
			tool.setZoom( 0.9 * zoom * tool.getZoom() );
		} );

		return COMPLETE;
	}

}
