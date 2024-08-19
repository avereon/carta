package com.avereon.cartesia.snap;

import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;

public class SnapGrid implements Snap {

	@Override
	public String getPromptKey() {
		return "snap-to-grid";
	}

	@Override
	public Point3D snap( DesignTool tool, Point3D point ) {
		if( point == null || point == CadPoints.NONE ) return CadPoints.NONE;
		return tool.getCoordinateSystem().getNearest( tool.getWorkplane(), point );
	}

}
