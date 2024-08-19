package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import static com.avereon.cartesia.command.Command.Result.*;

public class DrawEllipseArc5 extends DrawCommand {

	private DesignLine referenceLine;

	private DesignArc previewArc;

	private Point3D origin;

	private Point3D xPoint;

	private Point3D yPoint;

	private Point3D spinAnchor;

	private double spin;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		// Step 1 - Prompt for the origin
		if( task.getParameterCount() == 0 ) {
			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			promptForPoint( task, "center" );
			return INCOMPLETE;
		}

		// Step 2 - Get the origin, prompt for the x-radius
		if( task.getParameterCount() == 1 ) {
			origin = asPoint( task, "center", 0 );

			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			referenceLine.setOrigin( origin );
			referenceLine.setPoint( origin );

			promptForPoint( task, "radius" );
			return INCOMPLETE;
		}

		// Step 3 - Get the x-point and rotate angle, prompt for the y-radius
		if( task.getParameterCount() == 2 ) {
			origin = asPoint( task, "center", 0 );
			xPoint = asPoint( task, "radius", 1 );

			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			if( previewArc == null ) previewArc = createPreviewArc( task, origin );
			previewArc.setRadii( new Point3D( CadGeometry.distance( origin, xPoint ), 0, 0 ) );
			previewArc.setRotate( String.valueOf( deriveRotate( origin, xPoint ) ) );
			promptForPoint( task, "radius" );
			return INCOMPLETE;
		}

		// Step 4 - Get the second radius, prompt for the start angle
		if( task.getParameterCount() == 3 ) {
			origin = asPoint( task, "center", 0 );
			xPoint = asPoint( task, "radius", 1 );
			yPoint = asPoint( task, "radius", 2 );

			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			if( previewArc == null ) previewArc = createPreviewArc( task, origin );
			previewArc.setRadii( new Point3D( previewArc.getXRadius(), deriveYRadius( origin, xPoint, yPoint ), 0 ) );
			promptForPoint( task, "start" );
			return INCOMPLETE;
		}

		// Step 5 - Get the start angle, prompt for the extent angle
		if( task.getParameterCount() == 4 ) {
			origin = asPoint( task, "center", 0 );
			xPoint = asPoint( task, "radius", 1 );
			yPoint = asPoint( task, "radius", 2 );
			Point3D start = asPoint( task, "start", 3 );

			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			if( previewArc == null ) previewArc = createPreviewArc( task, origin );
			previewArc.setStart( deriveStart( previewArc.getOrigin(), previewArc.getXRadius(), previewArc.getYRadius(), previewArc.calcRotate(), start ) );
			previewArc.setExtent( 0.0 );
			spinAnchor = start;
			promptForPoint( task, "extent" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 5 ) ) spin = asDouble( task, "spin", 3 );

		if( task.hasParameter( 4 ) ) {
			setCaptureUndoChanges( task, true );

			origin = asPoint( task, "center", 0 );
			xPoint = asPoint( task, "radius", 1 );
			Point3D yPoint = asPoint( task, "radius", 2 );
			Point3D startPoint = asPoint( task, "start", 3 );
			Point3D extentPoint = asPoint( task, "extent", 4 );
			if( task.hasParameter( 5 ) ) spin = asDouble( task, "spin", 5 );

			double xRadius = deriveXRadius( origin, xPoint );
			double yRadius = deriveYRadius( origin, xPoint, yPoint );
			double rotate = deriveRotate( origin, xPoint );
			double start = deriveStart( origin, xRadius, yRadius, rotate, asPoint( task, "start", startPoint ) );
			double extent = deriveExtent( origin, xRadius, yRadius, rotate, start, asPoint( task, "extent", extentPoint ), spin );

			task.getTool().getCurrentLayer().addShape( new DesignArc( origin, xRadius, yRadius, rotate, start, extent, DesignArc.Type.OPEN ) );

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
					// Arc X radius and rotate
					referenceLine.setPoint( point );
					previewArc.setRadii( new Point3D( point.distance( previewArc.getOrigin() ), 0, 0 ) );
					previewArc.setRotate( String.valueOf( deriveRotate( origin, point ) ) );
				}
				case 3 -> {
					// Arc Y radius
					referenceLine.setPoint( point );
					previewArc.setRadii( new Point3D( previewArc.getXRadius(), deriveYRadius( origin, xPoint, point ), 0 ) );
				}
				case 4 -> {
					// Arc start
					referenceLine.setPoint( point );
					previewArc.setStart( deriveStart( previewArc.getOrigin(), previewArc.getXRadius(), previewArc.getYRadius(), previewArc.calcRotate(), point ) );
				}
				case 5 -> {
					// Arc extent
					referenceLine.setPoint( point );
					spin = getExtentSpin( previewArc.getOrigin(), previewArc.getXRadius(), previewArc.getYRadius(), previewArc.calcRotate(), previewArc.getStart(), spinAnchor, point, spin );
					previewArc.setExtent( deriveExtent( previewArc.getOrigin(), previewArc.getXRadius(), previewArc.getYRadius(), previewArc.calcRotate(), previewArc.getStart(), point, spin ) );
					spinAnchor = point;
				}
			}
		}
	}

}
