package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.CommandTask;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

import static com.avereon.cartesia.command.Command.Result.*;

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
			Point3D origin = asPoint( task, 0 );
			if( origin == null ) return INVALID;

			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			referenceLine.setOrigin( origin );
			referenceLine.setPoint( origin );

			if( previewArc == null ) previewArc = createPreviewArc( task, origin );
			promptForPoint( task, "start" );
			return INCOMPLETE;
		}

		// Step 3 - Get start, prompt for extent
		if( task.getParameterCount() == 2 ) {
			Point3D origin = asPoint( task, 0 );
			if( origin == null ) return INVALID;
			Point3D startPoint = asPoint( task, 1 );
			if( startPoint == null ) return INVALID;

			if( previewArc == null ) addPreview( task, previewArc = createPreviewArc( task, origin ) );
			previewArc.setRadius( CadGeometry.distance( previewArc.getOrigin(), startPoint ) );
			previewArc.setStart( deriveStart( previewArc.getOrigin(), previewArc.getXRadius(), previewArc.getYRadius(), previewArc.calcRotate(), startPoint ) );
			previewArc.setExtent( 0.0 );
			spinAnchor = startPoint;

			promptForPoint( task, "extent" );
			return INCOMPLETE;
		}

		// NEXT Make up my mind how I want to handle exceptions
		if( task.hasParameter( 3 ) ) spin = asDouble( task.getParameter( 3 ) );

		if( task.hasParameter( 2 ) ) {
			clearReferenceAndPreview( task );
			setCaptureUndoChanges( task, true );

			try {
				Point3D origin = asPoint( task, 0 );
				if( origin == null ) return INVALID;
				Point3D startPoint = asPoint( task, 1 );
				if( startPoint == null ) return INVALID;
				Point3D extentPoint = asPoint( task, 2 );
				if( extentPoint == null ) return INVALID;
				double radius = CadGeometry.distance( origin, startPoint );
				double start = deriveStart( origin, radius, radius, 0.0, startPoint );
				double extent = deriveExtent( origin, radius, radius, 0.0, start, extentPoint, spin );

				task.getTool().getCurrentLayer().addShape( new DesignArc( origin, radius, start, extent, DesignArc.Type.OPEN ) );
			} catch( ParseException exception ) {
				String title = Rb.text( RbKey.NOTICE, "command-error" );
				String message = Rb.text( RbKey.NOTICE, "unable-to-create-shape", exception );
				if( task.getContext().isInteractive() ) task.getContext().getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
			}

			return SUCCESS;
		}

		return FAILURE;
	}

	@Override
	public void handle( DesignCommandContext context, MouseEvent event ) {
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
					previewArc.setExtent( deriveExtent( previewArc.getOrigin(), previewArc.getXRadius(), previewArc.getYRadius(), previewArc.calcRotate(), previewArc.getStart(), point, spin ) );
					spinAnchor = point;
				}
			}
		}
	}

}
