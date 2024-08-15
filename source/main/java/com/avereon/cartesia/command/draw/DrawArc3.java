package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.CommandTask;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

import static com.avereon.cartesia.command.Command.Result.*;

public class DrawArc3 extends DrawCommand {

	private DesignLine referenceLine;

	private DesignArc previewArc;

	private Point3D start;

	private Point3D mid;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		// Step 1 - Prompt for start
		if( task.getParameterCount() < 1 ) {
			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			promptForPoint( task, "start-point" );
			return INCOMPLETE;
		}

		// Step 2 - Get start, prompt for mid-point
		if( task.getParameterCount() < 2 ) {
			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			start = asPoint( task, "start-point", 0 );
			referenceLine.setOrigin( start );
			promptForPoint( task, "mid-point" );
			return INCOMPLETE;
		}

		// Step 3 - Get mid-point, prompt for end
		if( task.getParameterCount() < 3 ) {
			removeReference( task, referenceLine );

			mid = asPoint( task, "mid-point", 1 );
			if( previewArc == null ) previewArc = createPreviewArc3( task, start, mid );

			promptForPoint( task, "end-point" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 2 ) ) {
			clearReferenceAndPreview( task );
			setCaptureUndoChanges( task, true );

			try {
				start = asPoint( task, "start-point", 0 );
				mid = asPoint( task, "mid-point", 1 );
				Point3D end = asPoint( task, "end-point", 2 );

				task.getTool().getCurrentLayer().addShape( CadGeometry.arcFromThreePoints( start, mid, end ) );
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
					previewArc.setOrigin( arc.getOrigin() );
					previewArc.setRadius( arc.getRadius() );
					previewArc.setStart( arc.getStart() );
					previewArc.setExtent( arc.getExtent() );
				}
			}
		}
	}

}
