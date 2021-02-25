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

		// Step 2 - Get the origin, prompt for the first radius
		if( parameters.length < 2 ) {
			Point3D origin = asPoint( context, parameters[ 0 ] );
			previewLine.setOrigin( origin );
			previewLine.setPoint( origin );
			addPreview( tool, previewArc = new DesignArc( origin, 0.0, 0.0, 360.0, DesignArc.Type.OPEN ) );
			promptForNumber( context, tool, "radius" );
			return INCOMPLETE;
		}

		// Step 3 - Get the first radius and rotate angle, prompt for the second radius
		if( parameters.length < 3 ) {
			Point3D point = asPoint( context, parameters[ 1 ] );
			previewArc.setXRadius( CadGeometry.distance( previewArc.getOrigin(), point ) );
			previewArc.setRotate( CadGeometry.angle360( point.subtract( previewArc.getOrigin() ) ) );
			promptForNumber( context, tool, "radius" );
			return INCOMPLETE;
		}

		// Step 4 - Get the second radius, prompt for the start angle
		if( parameters.length < 4 ) {
			Point3D point = asPoint( context, parameters[ 2 ] );
			previewArc.setYRadius( CadGeometry.distance( previewArc.getOrigin(), point ) );
			addPreview( tool, previewArc );
			promptForPoint( context, tool, "start" );
			return INCOMPLETE;
		}

		// Step 5 - Get the start angle, prompt for the extent angle
		if( parameters.length < 5 ) {
			Point3D start = asPoint( context, parameters[ 3 ] );
			spinAnchor = start;
			//previewArc.setRadius( CadGeometry.distance( previewArc.getOrigin(), start ) );
			previewArc.setStart( CadGeometry.angle360( start.subtract( previewArc.getOrigin() ) ) );
			previewArc.setExtent( 0.0 );
			promptForPoint( context, tool, "extent" );
			return INCOMPLETE;
		}

		previewArc.setOrigin( asPoint( context, parameters[ 0 ] ) );
		previewArc.setXRadius( asDouble( previewArc.getOrigin(), parameters[ 1 ] ) );
		// TODO Y-radius should be measured orthogonal to the X-radius
		previewArc.setYRadius( asDouble( previewArc.getOrigin(), parameters[ 2 ] ) );
		previewArc.setRotate( CadGeometry.angle360( asPoint( context, parameters[ 1 ] ).subtract( previewArc.getOrigin() ) ) );
		previewArc.setStart( getStart( previewArc, asPoint( context, parameters[ 3 ] ) ) );
		previewArc.setExtent( getExtent( previewArc, asPoint( context, parameters[ 4 ] ), spin ) );

		removePreview( tool, previewLine );
		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			spin = getExtentSpin( previewArc, spinAnchor, point, spin );

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
					previewArc.setRotate( CadGeometry.angle360( point.subtract( previewArc.getOrigin() ) ) );
				}
				case 3 -> {
					// Arc Y radius
					previewLine.setPoint( point );
					previewArc.setYRadius( point.distance( previewArc.getOrigin() ) );
				}
				case 4 -> {
					// Arc start
					previewLine.setPoint( point );
					previewArc.setStart( getStart( previewArc, point ) );
				}
				case 5 -> {
					// Arc extent
					previewLine.setPoint( point );
					previewArc.setExtent( getExtent( previewArc, point, spin ) );
				}
			}
		}
	}

}
