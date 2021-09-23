package com.avereon.cartesia.snap;

import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;

public class SnapNearestCp implements Snap {

	@Override
	public String getPromptKey() {
		return "snap-to-nearest";
	}

	@Override
	public Point3D snap( DesignTool tool, Point3D point ) {
		if( point == null ) return CadPoints.NONE;
		return tool.nearestCp( tool.getVisibleShapes(), point );
	}

}
