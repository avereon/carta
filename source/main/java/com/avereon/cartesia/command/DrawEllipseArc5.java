package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawEllipseArc5 extends DrawCommand {

	private DesignLine previewLine;

	private DesignArc previewArc;

	private Point3D origin;

	private Point3D xPoint;

	private Point3D yPoint;

	private Point3D spinAnchor;

	private double spin;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1 - Prompt for the origin
		if( parameters.length < 1 ) {
			addPreview( tool, previewLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "center" );
			return INCOMPLETE;
		}

		// Step 2 - Get the origin, prompt for the x-radius
		if( parameters.length < 2 ) {
			origin = asPoint( context, parameters[ 0 ] );
			previewLine.setOrigin( origin );
			previewLine.setPoint( origin );
			addPreview( tool, previewArc = new DesignArc( origin, 0.0, 0.0, 360.0, DesignArc.Type.OPEN ) );
			promptForNumber( context, tool, "radius" );
			return INCOMPLETE;
		}

		// Step 3 - Get the x-point and rotate angle, prompt for the y-radius
		if( parameters.length < 3 ) {
			xPoint = asPoint( context, parameters[ 1 ] );
			previewArc.setXRadius( CadGeometry.distance( origin, xPoint ) );
			previewArc.setRotate( deriveRotate( origin, xPoint ) );
			promptForNumber( context, tool, "radius" );
			return INCOMPLETE;
		}

		// Step 4 - Get the second radius, prompt for the start angle
		if( parameters.length < 4 ) {
			yPoint = asPoint( context, parameters[ 2 ] );
			previewArc.setYRadius( deriveYRadius( origin, xPoint, yPoint ) );
			addPreview( tool, previewArc );
			promptForPoint( context, tool, "start" );
			return INCOMPLETE;
		}

		// Step 5 - Get the start angle, prompt for the extent angle
		if( parameters.length < 5 ) {
			Point3D start = asPoint( context, parameters[ 3 ] );
			previewArc.setStart( deriveStart( previewArc, start ) );
			previewArc.setExtent( 0.0 );
			spinAnchor = start;
			promptForPoint( context, tool, "extent" );
			return INCOMPLETE;
		}

		origin = asPoint( context, parameters[ 0 ] );
		xPoint = asPoint( context, parameters[ 1 ] );
		Point3D yPoint = asPoint( context, parameters[ 2 ] );

		previewArc.setOrigin( origin );
		previewArc.setXRadius( asDouble( origin, xPoint ) );
		previewArc.setYRadius( deriveYRadius( origin, xPoint, yPoint ) );
		previewArc.setRotate( deriveRotate( origin, xPoint ) );
		previewArc.setStart( deriveStart( previewArc, asPoint( context, parameters[ 3 ] ) ) );
		previewArc.setExtent( deriveExtent( previewArc, asPoint( context, parameters[ 4 ] ), spin ) );

		removePreview( tool, previewLine );
		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			spin = getExtentSpin( previewArc, spinAnchor, point, spin );
			System.out.println( "spin=" + spin );

			switch( getStep() ) {
				case 1 -> {
					// Arc origin
					previewLine.setOrigin( point );
					previewLine.setPoint( point );
				}
				case 2 -> {
					// Arc X radius and rotate
					previewLine.setPoint( point );
					previewArc.setXRadius( point.distance( previewArc.getOrigin() ) );
					previewArc.setRotate( deriveRotate( origin, point ) );
				}
				case 3 -> {
					// Arc Y radius
					previewLine.setPoint( point );
					previewArc.setYRadius( deriveYRadius( origin, xPoint, point ) );
				}
				case 4 -> {
					// Arc start
					previewLine.setPoint( point );
					previewArc.setStart( deriveStart( previewArc, point ) );
				}
				case 5 -> {
					// Arc extent
					previewLine.setPoint( point );
					previewArc.setExtent( deriveExtent( previewArc, point, spin ) );
				}
			}
		}
	}

}
