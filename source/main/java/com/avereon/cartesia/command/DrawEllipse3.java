package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawEllipse3 extends DrawCommand {

	private DesignLine previewLine;

	private DesignEllipse previewEllipse;

	private Point3D origin;

	private Point3D xPoint;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1 - Prompt for the origin
		if( parameters.length < 1 ) {
			addPreview( tool, previewLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "center" );
			return INCOMPLETE;
		}

		// Step 2 - Get center, prompt for x-radius
		if( parameters.length < 2 ) {
			origin = asPoint( context, parameters[ 0 ] );
			previewLine.setOrigin( origin );
			previewLine.setPoint( origin );
			addPreview( tool, previewEllipse = new DesignEllipse( origin, 0.0 ) );
			promptForNumber( context, tool, "radius" );
			return INCOMPLETE;
		}

		// Step 3 - Get x-point, prompt for y-radius
		if( parameters.length < 3 ) {
			xPoint = asPoint( context, parameters[ 1 ] );
			previewEllipse.setXRadius( CadGeometry.distance( previewEllipse.getOrigin(), xPoint ) );
			previewEllipse.setRotate( CadGeometry.angle360( xPoint.subtract( previewEllipse.getOrigin() ) ) );
			promptForNumber( context, tool, "radius" );
			return INCOMPLETE;
		}

		origin = asPoint( context, parameters[ 0 ] );
		xPoint = asPoint( context, parameters[ 1 ] );
		Point3D yPoint = asPoint( context, parameters[ 2 ] );

		previewEllipse.setOrigin( asPoint( context, parameters[ 0 ] ) );
		previewEllipse.setXRadius( asDouble( previewEllipse.getOrigin(), parameters[ 1 ] ) );
		previewEllipse.setYRadius( getYRadius( origin, xPoint, yPoint ) );
		previewEllipse.setRotate( CadGeometry.angle360( xPoint.subtract( previewEllipse.getOrigin() ) ) );

		removePreview( tool, previewLine );
		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );

			switch( getStep() ) {
				case 1 -> {
					previewLine.setOrigin( point );
					previewLine.setPoint( point );
				}
				case 2 -> {
					previewLine.setPoint( point );
					previewEllipse.setXRadius( point.distance( previewEllipse.getOrigin() ) );
					previewEllipse.setRotate( CadGeometry.angle360( point.subtract( previewEllipse.getOrigin() ) ) );
				}
				case 3 -> {
					previewLine.setPoint( point );
					previewEllipse.setYRadius( getYRadius( origin, xPoint, point ) );
				}
			}
		}
	}

}
