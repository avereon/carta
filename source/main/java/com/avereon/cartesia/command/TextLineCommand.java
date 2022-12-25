package com.avereon.cartesia.command;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignTextLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;

import java.text.ParseException;

public class TextLineCommand extends TextCommand {

	private Point3D anchor;

	private DesignTextLine previewText;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( context.getTool().selectedShapes().isEmpty() ) return COMPLETE;

		setCaptureUndoChanges( context, false );

		// Ask for an anchor point
		if( parameters.length < 1 ) {
			promptForPoint( context, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a target point
		if( parameters.length < 2 ) {
			anchor = asPoint( context, parameters[ 0 ] );
			addPreview( context, previewText = new DesignTextLine( context.getWorldMouse(), "" ) );
			promptForText( context, "text" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			final Point3D anchor = asPoint( context, parameters[ 0 ] );
			final String text = asText( context, parameters[ 1 ] );
			context.getTool().getCurrentLayer().addShape( new DesignTextLine( anchor, text ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-move-shapes", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

	// TODO As the user types text, put it in the preview text

}
