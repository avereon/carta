package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class DrawCircle2 extends DrawCommand {

	private DesignEllipse previewEllipse;

	private DesignLine previewLine;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		// Step 1
		if( task.getParameterCount() == 0 ) {
			previewLine = createReferenceLine( task );
			promptForPoint( task, "center" );
			return INCOMPLETE;
		}

		// Step 2
		if( task.getParameterCount() == 1 ) {
			Point3D origin = asPoint( task, "center", 0 );
			if( previewEllipse == null ) previewEllipse = createPreviewEllipse( task, origin, 0.0 );
			previewLine.setOrigin( origin );
			previewLine.setPoint( origin );
			promptForNumber( task, "radius" );
			return INCOMPLETE;
		}

		setCaptureUndoChanges( task, true );

		if( task.hasParameter( 1 ) ) {
			Point3D origin = asPoint( task, "center", 0 );
			double radius = asDouble( task, "radius", origin, 1 );
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
					previewEllipse.setRadius( point.distance( previewEllipse.getOrigin() ) );
				}
			}
		}
	}

}
