package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;

import java.text.ParseException;

public class CameraZoomCommand extends CameraCommand {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// This command requires one value as the zoom value
		if( parameters.length < 1 ) {
			promptForValue( context, tool, BundleKey.PROMPT, "zoom" );
			return incomplete();
		}

		try {
			tool.setZoom( asDouble( parameters[ 0 ] ) );
		} catch( ParseException exception ) {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-zoom", exception.getMessage() );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return complete();
	}

	protected void zoomByFactor( DesignTool tool, Point3D point, double factor ) {
		tool.zoom( point, factor );
	}

}
