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
			// TODO Create an undo command
		} catch( ParseException exception ) {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-zoom", exception.getMessage() );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return complete();
	}

	protected void zoomByFactor( DesignTool tool, double factor, Object... parameters ) throws Exception {
		Point3D viewpoint = tool.getViewPoint();
		double x = viewpoint.getX();
		double y = viewpoint.getY();
		double z = viewpoint.getZ();

		switch( parameters.length ) {
			case 2 -> {
				x = asDouble( parameters[ 0 ] );
				y = asDouble( parameters[ 1 ] );
			}
			case 3 -> {
				x = asDouble( parameters[ 0 ] );
				y = asDouble( parameters[ 1 ] );
				z = asDouble( parameters[ 2 ] );
			}
		}

		tool.zoom( x, y, z, factor );
	}

}
