package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

public class DrawLine2 extends DrawCommand {

	private static final System.Logger log = Log.get();

	private DesignLine preview;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		setCaptureUndoChanges( tool, false );

		// Step 1
		if( parameters.length < 1 ) {
			addPreview( tool, preview = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "start-point" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			preview.setOrigin( asPoint( context, parameters[ 0 ] ) );
			promptForPoint( context, tool, "end-point" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( tool );
		setCaptureUndoChanges( tool, true );

		try {
			Point3D origin = asPoint( context, parameters[ 0 ] );
			Point3D point = asPoint( context, parameters[ 1 ] );

			// Start an undo multi-change
			tool.getCurrentLayer().addShape( new DesignLine( origin, point ) );
			// Done with undo multi-change
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
				case 1 -> preview.setPoint( point ).setOrigin( point );
				case 2 -> preview.setPoint( point );
			}
		}
	}

}
