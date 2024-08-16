package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import static com.avereon.cartesia.command.Command.Result.*;

public class DrawCircle3 extends DrawCommand {

	private DesignLine referenceLine;

	private DesignEllipse previewEllipse;

	private Point3D start;

	private Point3D mid;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		// Step 1
		if( task.getParameterCount() == 0 ) {
			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			promptForPoint( task, "start-point" );
			return INCOMPLETE;
		}

		// Step 2
		if( task.getParameterCount() == 1 ) {
			start = asPoint( task, "start-point", 0 );
			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			referenceLine.setOrigin( start );
			promptForPoint( task, "mid-point" );
			return INCOMPLETE;
		}

		// Step 3
		if( task.getParameterCount() == 2 ) {
			removeReference( task, referenceLine );

			start = asPoint( task, "start-point", 0 );
			mid = asPoint( task, "mid-point", 1 );
			if( previewEllipse == null ) previewEllipse = createPreviewEllipse3( task, start, mid );

			promptForPoint( task, "end-point" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 2 ) ) {
			setCaptureUndoChanges( task, true );

			start = asPoint( task, "start-point", 0 );
			mid = asPoint( task, "mid-point", 1 );
			Point3D end = asPoint( task, "end-point", 2 );
			task.getTool().getCurrentLayer().addShape( CadGeometry.circleFromThreePoints( start, mid, end ) );

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
				case 2 -> referenceLine.setPoint( point );
				case 3 -> {
					DesignArc arc = CadGeometry.arcFromThreePoints( start, mid, point );
					previewEllipse.setOrigin( arc.getOrigin() );
					previewEllipse.setRadius( arc.getRadius() );
				}
			}
		}
	}

}
