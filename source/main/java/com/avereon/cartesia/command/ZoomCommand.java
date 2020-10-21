package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;

public class ZoomCommand extends Command {

	@Override
	public void execute( CommandContext context, DesignTool tool, Object... parameters ) {
		// TODO Implement ZoomCommand.execute()

		// This command requires exactly one value
		if( parameters.length < 1 ) {
			// Get the zoom value
		}

		// FIXME These parameters might be expressions
		tool.setZoom( (Double)parameters[0] );
	}

	protected void zoomByFactor( DesignTool tool, double factor, Object... parameters ) {
		Point3D viewpoint = tool.getViewPoint();
		double x = viewpoint.getX();
		double y = viewpoint.getY();
		double z = viewpoint.getZ();

		// FIXME These parameters might be expressions
		switch( parameters.length ) {
			case 2 -> {
				x = (Double)parameters[ 0 ];
				y = (Double)parameters[ 1 ];
			}
			case 3 -> {
				x = (Double)parameters[ 0 ];
				y = (Double)parameters[ 1 ];
				z = (Double)parameters[ 2 ];
			}
		}

		tool.zoom( x, y, z, factor );
	}

}
