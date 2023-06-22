package com.avereon.cartesia.snap;

import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.tool.Grid;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;

public class SnapGrid implements Snap {

	@Override
	public String getPromptKey() {
		return "snap-to-grid";
	}

	@Override
	public Point3D snap( BaseDesignTool tool, Point3D mouse ) {
		if( mouse == null ) return CadPoints.NONE;

		// NOTE The mouse point is in world coordinates
		Grid system = tool.getCoordinateSystem();
		return system.getNearest( tool.getWorkplane(), mouse );
	}

}
