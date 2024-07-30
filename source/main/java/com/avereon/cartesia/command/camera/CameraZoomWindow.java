package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.tool.CommandContext;

public class CameraZoomWindow extends CameraCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		if( parameters.length < 1 ) {
			promptForWindow( context, "zoom-window" );
			return INCOMPLETE;
		}

		// FIXME Zoom window works differently now that we have Anchor and Select commands

//		if( parameters.length < 2 ) {
//			return INCOMPLETE;
//		}
//
//		clearReferenceAndPreview( context );
//		setCaptureUndoChanges( context, true );
//
//		Point3D anchor = context.getScreenAnchor();
//		Point3D point = context.getTool().worldToScreen( asPoint( context, parameters[ 0 ] ) );
//		Bounds bounds = FxUtil.bounds( anchor, point );
//
//		context.getTool().setScreenViewport( bounds );

		return COMPLETE;
	}

}
