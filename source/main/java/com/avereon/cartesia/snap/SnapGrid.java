package com.avereon.cartesia.snap;

import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.tool.CoordinateSystem;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;

public class SnapGrid implements Snap {

	@Override
	public String getPromptKey() {
		return "snap-to-grid";
	}

	@Override
	public Point3D snap( DesignTool tool, Point3D mouse ) {
		if( mouse == null ) return CadPoints.NONE;

		// NOTE The mouse point is in world coordinates
		CoordinateSystem system = tool.getDesignContext().getCoordinateSystem();
		return system.getNearest( tool.getDesignContext().getWorkplane(), mouse );
	}

}
