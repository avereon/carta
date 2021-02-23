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

	private Point3D start;

	private Point3D mid;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1
		if( parameters.length < 1 ) {
			addPreview( tool, previewLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "center" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			addPreview( tool, previewEllipse = new DesignEllipse( asPoint( context, parameters[ 0 ] ), 0.0 ) );
			promptForNumber( context, tool, "radius" );
			return INCOMPLETE;
		}

		// Step 3
		if( parameters.length < 3 ) {
			Point3D point = asPoint( context, parameters[ 1 ] );
			previewEllipse.setXRadius( CadGeometry.distance( previewEllipse.getOrigin(), point ) );
			previewEllipse.setRotate( CadGeometry.angle360( point.subtract( previewEllipse.getOrigin() ) ) );
			promptForNumber( context, tool, "radius" );
			return INCOMPLETE;
		}

		// TODO Y-radius should be measured orthogonal to the X-radius

		removePreview( tool, previewLine );
		previewEllipse.setXRadius( asDouble( previewEllipse.getOrigin(), parameters[ 1 ] ) );
		previewEllipse.setYRadius( asDouble( previewEllipse.getOrigin(), parameters[ 2 ] ) );
		previewEllipse.setRotate( CadGeometry.angle360( asPoint( context, parameters[ 1 ] ).subtract( previewEllipse.getOrigin() ) ) );
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
					previewEllipse.setYRadius( point.distance( previewEllipse.getOrigin() ) );
				}
			}
		}
	}

}
