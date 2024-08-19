package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.command.CommandTask;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class DrawArc2 extends DrawCommand {

	private DesignLine referenceLine;

	private DesignArc previewArc;

	private Point3D spinAnchor;

	private double spin;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		// Step 1 - Prompt for origin
		if( task.getParameterCount() == 0 ) {
			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			promptForPoint( task, "center" );
			return INCOMPLETE;
		}

		// Step 2 - Get origin, prompt for start
		if( task.getParameterCount() == 1 ) {
			Point3D origin = asPoint( task, "center", 0 );

			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			referenceLine.setPoint( origin ).setOrigin( origin );

			if( previewArc == null ) previewArc = createPreviewArc( task, origin );
			promptForPoint( task, "start" );

			return INCOMPLETE;
		}

		// Step 3 - Get start, prompt for extent
		if( task.getParameterCount() == 2 ) {
			Point3D origin = asPoint( task, "center", 0 );
			Point3D startPoint = asPoint( task, "start", 1 );

			if( previewArc == null ) previewArc = createPreviewArc( task, origin );
			double start = deriveStart( previewArc.getOrigin(), previewArc.getXRadius(), previewArc.getYRadius(), previewArc.calcRotate(), startPoint );
			previewArc.setRadius( CadGeometry.distance( previewArc.getOrigin(), startPoint ) );
			previewArc.setStart( start );
			spinAnchor = startPoint;

			promptForPoint( task, "extent" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 3 ) ) spin = asDouble( task, "spin", 3 );

		if( task.hasParameter( 2 ) ) {
			setCaptureUndoChanges( task, true );

			Point3D origin = asPoint( task, "center", 0 );
			Point3D startPoint = asPoint( task, "start", 1 );
			Point3D extentPoint = asPoint( task, "extent", 2 );
			double radius = CadGeometry.distance( origin, startPoint );
			double start = deriveStart( origin, radius, radius, 0.0, startPoint );
			double extent = deriveExtent( origin, radius, radius, 0.0, start, extentPoint, spin );

			task.getTool().getCurrentLayer().addShape( new DesignArc( origin, radius, start, extent, DesignArc.Type.OPEN ) );

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
					// Arc origin
					referenceLine.setOrigin( point );
					referenceLine.setPoint( point );
				}
				case 2 -> {
					// Arc radius and start
					referenceLine.setPoint( point );
					previewArc.setRadius( point.distance( previewArc.getOrigin() ) );
					previewArc.setStart( deriveStart( previewArc.getOrigin(), previewArc.getXRadius(), previewArc.getYRadius(), previewArc.calcRotate(), point ) );
				}
				case 3 -> {
					// Arc extent
					referenceLine.setPoint( point );
					spin = getExtentSpin( previewArc.getOrigin(), previewArc.getXRadius(), previewArc.getYRadius(), previewArc.calcRotate(), previewArc.getStart(), spinAnchor, point, spin );
					double extent = deriveExtent( previewArc.getOrigin(), previewArc.getXRadius(), previewArc.getYRadius(), previewArc.calcRotate(), previewArc.getStart(), point, spin );
					previewArc.setExtent( extent );
					spinAnchor = point;
				}
			}
		}
	}

}
