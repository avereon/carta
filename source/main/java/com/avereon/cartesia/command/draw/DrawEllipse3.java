package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import static com.avereon.cartesia.command.Command.Result.*;

public class DrawEllipse3 extends DrawCommand {

	private DesignLine referenceLine;

	private DesignEllipse previewEllipse;

	private Point3D origin;

	private Point3D xPoint;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		// Step 1 - Prompt for the origin
		if( task.getParameterCount() == 0 ) {
			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			promptForPoint( task, "center" );
			return INCOMPLETE;
		}

		// Step 2 - Get center, prompt for x-radius
		if( task.getParameterCount() == 1 ) {
			removeReference( task, referenceLine );

			origin = asPoint( task, "center", 0 );

			if( previewEllipse == null ) previewEllipse = createPreviewEllipse( task, origin );
			promptForPoint( task, "radius" );
			return INCOMPLETE;
		}

		// Step 3 - Get x-point, prompt for y-radius
		if( task.getParameterCount() == 2 ) {
			origin = asPoint( task, "center", 0 );
			xPoint = asPoint( task, "radius", 1 );

			if( previewEllipse == null ) previewEllipse = createPreviewEllipse( task, origin );
			previewEllipse.setRadii( new Point3D( CadGeometry.distance( previewEllipse.getOrigin(), xPoint ), 0, 0 ) );
			previewEllipse.setRotate( String.valueOf( deriveRotate( origin, xPoint ) ) );
			promptForPoint( task, "radius" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 2 ) ) {
			setCaptureUndoChanges( task, true );

			origin = asPoint( task, "center", 0 );
			xPoint = asPoint( task, "radius", 1 );
			Point3D yPoint = asPoint( task, "radius", 2 );
			double xRadius = deriveXRadius( origin, xPoint );
			double yRadius = deriveYRadius( origin, xPoint, yPoint );
			double rotate = deriveRotate( origin, xPoint );
			task.getTool().getCurrentLayer().addShape( new DesignEllipse( origin, xRadius, yRadius, rotate ) );

			return SUCCESS;
		}

		return FAILURE;
	}

	@Override
	public void handle( CommandTask task, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D point = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );

			switch( getStep() ) {
				case 1 -> {
					referenceLine.setOrigin( point );
					referenceLine.setPoint( point );
				}
				case 2 -> {
					referenceLine.setPoint( point );
					previewEllipse.setRotate( String.valueOf( deriveRotate( origin, point ) ) );
				}
				case 3 -> {
					referenceLine.setPoint( point );
					previewEllipse.setRadii( new Point3D( previewEllipse.getXRadius(), deriveYRadius( origin, xPoint, point ), 0 ) );
				}
			}
		}
	}

}
