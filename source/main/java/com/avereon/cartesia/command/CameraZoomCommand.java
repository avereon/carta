package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;

public class CameraZoomCommand extends CameraCommand {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// This command requires one value as the zoom value
		if( parameters.length < 1 ) {
			promptForValue( context, tool, BundleKey.PROMPT, "zoom", this );
			return incomplete();
		}

		tool.setZoom( asDouble( parameters[ 0 ] ) );
		return setComplete();
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
