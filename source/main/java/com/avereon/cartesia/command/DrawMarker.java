package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignMarker;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

public class DrawMarker extends DrawCommand {

	private static final System.Logger log = Log.get();

	private DesignMarker preview;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		setCaptureUndoChanges( tool, false );

		if( parameters.length < 1 ) {
			// Need to start with the point at ZERO until it is added
			// This is a bit of a fluke with how markers are generated
			addPreview( tool, preview = new DesignMarker( Point3D.ZERO ) );
			preview.setOrigin( context.getWorldMouse() );
			promptForPoint( context, tool, "select-point" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( tool );
		setCaptureUndoChanges( tool, true );

		try {
			tool.getCurrentLayer().addShape( new DesignMarker( asPoint( context.getAnchor(), parameters[ 0 ] ) ) );
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
			Point3D mouse = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			if( getStep() == 1 ) preview.setOrigin( mouse );
		}
	}

}
