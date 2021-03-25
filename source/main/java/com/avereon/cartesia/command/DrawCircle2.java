package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

public class DrawCircle2 extends DrawCommand {

	private static final System.Logger log = Log.get();

	private DesignEllipse previewEllipse;

	private DesignLine previewLine;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		setCaptureUndoChanges( tool, false );

		// Step 1
		if( parameters.length < 1 ) {
			addPreview( tool, previewLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "center" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			Point3D origin = asPoint( context, parameters[ 0 ] );
			addPreview( tool, previewEllipse = new DesignEllipse( origin, 0.0 ) );
			previewLine.setOrigin( origin );
			previewLine.setPoint( origin );
			promptForNumber( context, tool, "radius" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( tool );
		setCaptureUndoChanges( tool, true );

		try {
			Point3D origin = asPoint( context, parameters[ 0 ] );
			double radius = asDouble( previewEllipse.getOrigin(), parameters[ 1 ] );
			tool.getCurrentLayer().addShape( new DesignEllipse( origin, radius ) );
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
				case 2 -> {
					previewLine.setPoint( point );
					previewEllipse.setRadius( point.distance( previewEllipse.getOrigin() ) );
				}
			}
		}
	}

}
