package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

public class DrawArc2 extends DrawCommand {

	private DesignLine referenceLine;

	private DesignArc previewArc;

	private Point3D spinAnchor;

	private double spin;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		setCaptureUndoChanges( tool, false );

		// Step 1 - Prompt for origin
		if( parameters.length < 1 ) {
			addReference( tool, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "center" );
			return INCOMPLETE;
		}

		// Step 2 - Get origin, prompt for start
		if( parameters.length < 2 ) {
			Point3D origin = asPoint( context, parameters[ 0 ] );
			referenceLine.setOrigin( origin );
			referenceLine.setPoint( origin );
			addPreview( tool, previewArc = new DesignArc( origin, 0.0, 0.0, 360.0, DesignArc.Type.OPEN ) );
			promptForPoint( context, tool, "start" );
			return INCOMPLETE;
		}

		// Step 3 - Get start, prompt for extent
		if( parameters.length < 3 ) {
			Point3D point = asPoint( context, parameters[ 1 ] );
			previewArc.setRadius( CadGeometry.distance( previewArc.getOrigin(), point ) );
			previewArc.setStart( deriveStart( previewArc, point ) );
			previewArc.setExtent( 0.0 );
			spinAnchor = point;
			promptForPoint( context, tool, "extent" );
			return INCOMPLETE;
		}

		if( parameters.length > 3 ) {
			spin = asDouble( parameters[ 3 ] );
		}

		clearReferenceAndPreview( tool );
		setCaptureUndoChanges( tool, true );

		try {
			Point3D origin = asPoint( context, parameters[ 0 ] );
			Point3D startPoint = asPoint( context, parameters[ 1 ] );
			Point3D extentPoint = asPoint( context, parameters[ 2 ] );
			double radius = CadGeometry.distance( origin, startPoint );
			DesignArc arc = new DesignArc( origin, radius, 0.0, 360.0, DesignArc.Type.OPEN );

			// FIXME This implementation depends on state in arc
			double start = deriveStart( arc, startPoint );
			arc.setStart( start );
			// FIXME This implementation depends on state in arc
			double extent = deriveExtent( arc, extentPoint, spin );
			arc.setExtent( extent );

			tool.getCurrentLayer().addShape( arc );
		} catch( ParseException exception ) {
			String title = Rb.text( BundleKey.NOTICE, "command-error" );
			String message = Rb.text( BundleKey.NOTICE, "unable-to-create-shape", exception );
			if( context.isInteractive() ) tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
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
					referenceLine.setOrigin( point );
					referenceLine.setPoint( point );
				}
				case 2 -> {
					// Arc radius and start
					referenceLine.setPoint( point );
					previewArc.setRadius( point.distance( previewArc.getOrigin() ) );
					previewArc.setStart( deriveStart( previewArc, point ) );
				}
				case 3 -> {
					// Arc extent
					referenceLine.setPoint( point );
					previewArc.setExtent( deriveExtent( previewArc, point, spin ) );
					spinAnchor = point;
				}
			}
		}
	}

}
