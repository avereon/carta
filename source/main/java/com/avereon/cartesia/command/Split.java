package com.avereon.cartesia.command;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.text.ParseException;

@CustomLog
public class Split extends Command {

	private DesignShape splitShape;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		if( parameters.length < 1 ) {
			promptForShape( context, "select-split-shape" );
			return INCOMPLETE;
		}

		if( parameters.length < 2 ) {
			Point3D mousePoint = context.getScreenMouse();
			splitShape = selectNearestShapeAtMouse( context, mousePoint );
			if( splitShape == DesignShape.NONE ) return INVALID;
			promptForPoint( context, "select-split-point" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			com.avereon.cartesia.math.Split.split( context.getTool(), splitShape, asPoint( context, parameters[ 1 ] ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-move-shapes", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

}

