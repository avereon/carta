package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;

public class SnapNearestPoint extends SnapCommand {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		return findNearest( tool, context.getMouse() );
	}

	private Point3D findNearest( DesignTool tool, Point3D point ) {
		// TODO Find the nearest construction point to the specified point

		return point;
	}

}
