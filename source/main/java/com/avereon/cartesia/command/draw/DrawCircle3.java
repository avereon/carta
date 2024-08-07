package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class DrawCircle3 extends DrawCommand {

	private DesignLine previewLine;

	private DesignEllipse previewEllipse;

	private Point3D start;

	private Point3D mid;

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		// Step 1
		if( parameters.length < 1 ) {
			addPreview( context, previewLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "start-point" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			start = asPoint( context, parameters[ 0 ] );
			previewLine.setOrigin( start );
			promptForPoint( context, "mid-point" );
			return INCOMPLETE;
		}

		// Step 3
		if( parameters.length < 3 ) {
			removePreview( context, previewLine );

			mid = asPoint( context, parameters[ 1 ] );
			addPreview( context, previewEllipse = CadGeometry.circleFromThreePoints( start, mid, mid ) );

			promptForPoint( context, "end-point" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			start = asPoint( context, parameters[ 0 ] );
			mid = asPoint( context, parameters[ 1 ] );
			Point3D end = asPoint( context, parameters[ 2 ] );
			context.getTool().getCurrentLayer().addShape( CadGeometry.circleFromThreePoints( start, mid, end ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-create-shape", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return SUCCESS;
	}

	@Override
	public void handle( DesignCommandContext context, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D point = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );

			switch( getStep() ) {
				case 1 -> {
					previewLine.setOrigin( point );
					previewLine.setPoint( point );
				}
				case 2 -> previewLine.setPoint( point );
				case 3 -> {
					DesignArc next = CadGeometry.arcFromThreePoints( start, mid, point );
					previewEllipse.setOrigin( next.getOrigin() );
					previewEllipse.setRadius( next.getRadius() );
				}
			}
		}
	}

}
