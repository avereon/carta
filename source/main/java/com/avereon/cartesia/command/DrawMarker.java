package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignMarker;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import java.text.ParseException;

@CustomLog
public class DrawMarker extends DrawCommand {

	private DesignMarker preview;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		if( parameters.length < 1 ) {
			// Need to start with the point at ZERO until it is added
			// This is a bit of a fluke with how markers are generated
			addPreview( context, preview = new DesignMarker( context.getWorldMouse() ) );
			preview.setOrigin( context.getWorldMouse() );
			promptForPoint( context, "select-point" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			context.getTool().getCurrentLayer().addShape( new DesignMarker( asPoint( context.getAnchor(), parameters[ 0 ] ) ) );
		} catch( ParseException exception ) {
			String title = Rb.text( BundleKey.NOTICE, "command-error" );
			String message = Rb.text( BundleKey.NOTICE, "unable-to-create-shape", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D mouse = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			if( getStep() == 1 ) preview.setOrigin( mouse );
		}
	}

}
