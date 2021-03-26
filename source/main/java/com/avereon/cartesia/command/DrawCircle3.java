package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

public class DrawCircle3 extends DrawCommand {

	private DesignLine previewLine;

	private DesignEllipse previewEllipse;

	private Point3D start;

	private Point3D mid;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
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
			tool.getCurrentLayer().addShape( CadGeometry.circleFromThreePoints( start, mid, end ) );
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

			switch( getStep() ) {
				case 1 -> {
					previewLine.setOrigin( point );
					previewLine.setPoint( point );
				}
				case 2 -> previewLine.setPoint( point );
				case 3 -> {
					DesignArc next = CadGeometry.arcFromThreePoints( start, mid, point );
					if( next != null ) {
						previewEllipse.setOrigin( next.getOrigin() );
						previewEllipse.setRadius( next.getRadius() );
					}
				}
			}
		}
	}

}
