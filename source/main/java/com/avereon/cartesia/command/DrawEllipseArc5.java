package com.avereon.cartesia.command;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

public class DrawEllipseArc5 extends DrawCommand {

	private DesignLine previewLine;

	private DesignArc referenceArc;

	private Point3D origin;

	private Point3D xPoint;

	private Point3D yPoint;

	private Point3D spinAnchor;

	private double spin;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		// Step 1 - Prompt for the origin
		if( parameters.length < 1 ) {
			addPreview( context, previewLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "center" );
			return INCOMPLETE;
		}

		// Step 2 - Get the origin, prompt for the x-radius
		if( parameters.length < 2 ) {
			origin = asPoint( context, parameters[ 0 ] );
			previewLine.setOrigin( origin );
			previewLine.setPoint( origin );
			addPreview( context, referenceArc = new DesignArc( origin, 0.0, 0.0, 360.0, DesignArc.Type.OPEN ) );
			promptForNumber( context, "radius" );
			return INCOMPLETE;
		}

		// Step 3 - Get the x-point and rotate angle, prompt for the y-radius
		if( parameters.length < 3 ) {
			xPoint = asPoint( context, parameters[ 1 ] );
			referenceArc.setXRadius( CadGeometry.distance( origin, xPoint ) );
			referenceArc.setRotate( deriveRotate( origin, xPoint ) );
			promptForNumber( context, "radius" );
			return INCOMPLETE;
		}

		// Step 4 - Get the second radius, prompt for the start angle
		if( parameters.length < 4 ) {
			yPoint = asPoint( context, parameters[ 2 ] );
			referenceArc.setYRadius( deriveYRadius( origin, xPoint, yPoint ) );
			addPreview( context, referenceArc );
			promptForPoint( context, "start" );
			return INCOMPLETE;
		}

		// Step 5 - Get the start angle, prompt for the extent angle
		if( parameters.length < 5 ) {
			Point3D start = asPoint( context, parameters[ 3 ] );
			referenceArc.setStart( deriveStart( referenceArc.getOrigin(), referenceArc.getXRadius(), referenceArc.getYRadius(), referenceArc.calcRotate(), start ) );
			referenceArc.setExtent( 0.0 );
			spinAnchor = start;
			promptForPoint( context, "extent" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			origin = asPoint( context, parameters[ 0 ] );
			xPoint = asPoint( context, parameters[ 1 ] );
			Point3D yPoint = asPoint( context, parameters[ 2 ] );
			Point3D startPoint = asPoint( context, parameters[ 3 ] );
			Point3D extentPoint = asPoint( context, parameters[ 4 ] );
			if( parameters.length > 5 ) spin = asDouble( parameters[5] );

			double xRadius = asDouble( origin, xPoint );
			double yRadius = deriveYRadius( origin, xPoint, yPoint );
			double rotate = deriveRotate( origin, xPoint );
			double start = deriveStart( origin, xRadius, yRadius, rotate, asPoint( context, startPoint ) );
			double extent = deriveExtent( origin, xRadius, yRadius, rotate, start, asPoint( context, extentPoint ), spin );

			context.getTool().getCurrentLayer().addShape( new DesignArc( origin, xRadius, yRadius, rotate, start, extent, DesignArc.Type.OPEN ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-create-shape", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );

			switch( getStep() ) {
				case 1 -> {
					// Arc origin
					previewLine.setOrigin( point );
					previewLine.setPoint( point );
				}
				case 2 -> {
					// Arc X radius and rotate
					previewLine.setPoint( point );
					referenceArc.setXRadius( point.distance( referenceArc.getOrigin() ) );
					referenceArc.setRotate( deriveRotate( origin, point ) );
				}
				case 3 -> {
					// Arc Y radius
					previewLine.setPoint( point );
					referenceArc.setYRadius( deriveYRadius( origin, xPoint, point ) );
				}
				case 4 -> {
					// Arc start
					previewLine.setPoint( point );
					referenceArc.setStart( deriveStart( referenceArc.getOrigin(), referenceArc.getXRadius(), referenceArc.getYRadius(), referenceArc.calcRotate(), point ) );
				}
				case 5 -> {
					// Arc extent
					previewLine.setPoint( point );
					spin = getExtentSpin( referenceArc.getOrigin(), referenceArc.getXRadius(), referenceArc.getYRadius(), referenceArc.calcRotate(), referenceArc.getStart(), spinAnchor, point, spin );
					referenceArc.setExtent( deriveExtent( referenceArc.getOrigin(), referenceArc.getXRadius(), referenceArc.getYRadius(), referenceArc.calcRotate(), referenceArc.getStart(), point, spin ) );
					spinAnchor = point;
				}
			}
		}
	}

}
