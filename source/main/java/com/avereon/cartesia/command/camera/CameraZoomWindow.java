package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.zarra.javafx.FxUtil;
import javafx.geometry.Point3D;
import lombok.CustomLog;

@CustomLog
public class CameraZoomWindow extends CameraCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		if( parameters.length < 1 ) {
			// Zoom window anchor
			promptForWindow( context, "zoom-window" );
			promptForWindow( context, "zoom-window-anchor" );
			return INCOMPLETE;
		}

		if( parameters.length < 2 ) {
			// Zoom window point
			//promptForWindow( context, "zoom-window-point" );
			return INCOMPLETE;
		}

		Point3D anchor = asPoint( context, parameters[ 0 ] );
		Point3D mouse = asPoint( context, parameters[ 1 ] );
		context.getTool().setWorldViewport( FxUtil.bounds( anchor, mouse ) );

		return COMPLETE;
	}

}
