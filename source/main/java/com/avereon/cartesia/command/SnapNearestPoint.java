package com.avereon.cartesia.command;

import com.avereon.cartesia.OldCommand;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;

public class SnapNearestPoint extends OldCommand {

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		//processor.pushValue( tool, findNearest( tool, tool.getWorldPointAtMouse() ) );
	}

	private Point3D findNearest( DesignTool tool, Point3D point ) {
		// TODO Find the nearest construction point to the specified point

		return point;
	}

}
