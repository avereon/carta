package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;

public class CameraZoomWindow extends CameraCommand {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		if( parameters.length < 1 ) {
			promptForWindow( context, "zoom-window" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		context.getTool().setViewport( context.getTool().screenToWorld( asBounds( context, parameters[ 0 ] ) ) );

		return COMPLETE;
	}

}
