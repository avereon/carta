package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class DrawCircleDiameter2 extends DrawCommand {

	private DesignEllipse previewEllipse;

	private DesignLine previewLine;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		// Step 1
		if( task.getParameterCount() == 0 ) {
			if( previewLine == null ) previewLine = createReferenceLine( task );
			promptForPoint( task, "start-point" );
			return INCOMPLETE;
		}

		// Step 2
		if( task.getParameterCount() == 1 ) {
			Point3D origin = asPoint( task, "start-point", 0 );
			if( previewEllipse == null ) previewEllipse = createPreviewEllipse( task, origin );

			if( previewLine == null ) previewLine = createReferenceLine( task );
			previewLine.setOrigin( origin );
			previewLine.setPoint( origin );
			promptForNumber( task, "diameter" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 1 ) ) {
			setCaptureUndoChanges( task, true );

			Point3D a = asPoint( task, "start-point", 0 );
			Point3D b = asPoint( task, "diameter", 1 );
			Point3D origin = CadGeometry.midpoint( a, b );
			double radius = 0.5 * CadGeometry.distance( a, b );
			task.getTool().getCurrentLayer().addShape( new DesignEllipse( origin, radius ) );

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
					previewLine.setOrigin( point );
					previewLine.setPoint( point );
				}
				case 2 -> {
					previewLine.setPoint( point );
					previewEllipse.setOrigin( CadGeometry.midpoint( previewLine.getOrigin(), point ) );
					previewEllipse.setRadius( point.distance( previewEllipse.getOrigin() ) );
				}
			}
		}
	}

}
