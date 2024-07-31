package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.command.Command;
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

public class DrawArc3 extends DrawCommand {

	private DesignLine referenceLine;

	private DesignArc previewArc;

	private Point3D start;

	private Point3D mid;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		// Step 1 - Prompt for start
		if( parameters.length < 1 ) {
			addReference( context, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "start-point" );
			return Command.INCOMPLETE;
		}

		// Step 2 - Get start, prompt for mid-point
		if( parameters.length < 2 ) {
			start = asPoint( context, parameters[ 0 ] );
			referenceLine.setOrigin( start );
			promptForPoint( context, "mid-point" );
			return Command.INCOMPLETE;
		}

		// Step 3 - Get mid point, prompt for end
		if( parameters.length < 3 ) {
			removeReference( context, referenceLine );

			mid = asPoint( context, parameters[ 1 ] );
			addPreview( context, previewArc = CadGeometry.arcFromThreePoints( start, mid, mid ) );

			promptForPoint( context, "end-point" );
			return Command.INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			start = asPoint( context, parameters[ 0 ] );
			mid = asPoint( context, parameters[ 1 ] );
			Point3D end = asPoint( context, parameters[ 2 ] );
			context.getTool().getCurrentLayer().addShape( CadGeometry.arcFromThreePoints( start, mid, end ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-create-shape", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return Command.COMPLETE;
	}

	@Override
	public void handle( CommandContext context, MouseEvent event ) {
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
					DesignArc next = CadGeometry.arcFromThreePoints( start, mid, point );
					previewArc.setOrigin( next.getOrigin() );
					previewArc.setRadius( next.getRadius() );
					previewArc.setStart( next.getStart() );
					previewArc.setExtent( next.getExtent() );
				}
			}
		}
	}

}
