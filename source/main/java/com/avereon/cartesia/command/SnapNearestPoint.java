package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.DesignTool;
import javafx.geometry.Point3D;

public class SnapNearestPoint extends Command {

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		processor.pushValue( tool, findNearest( tool, tool.getMousePoint() ) );
	}

	private Point3D findNearest( DesignTool tool, Point3D point ) {
		// TODO Find the nearest construction point to the specified point

		return point;
	}

}
