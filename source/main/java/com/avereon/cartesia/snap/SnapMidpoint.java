package com.avereon.cartesia.snap;

import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;

import java.util.List;

public class SnapMidpoint implements Snap {

	@Override
	public String getPromptKey() {
		return "snap-to-midpoint";
	}

	@Override
	public Point3D snap( BaseDesignTool tool, Point3D point ) {
		if( point == null ) return CadPoints.NONE;

		Point3D mouse = tool.worldToScreen( point );
		List<DesignShape> shapes = tool.screenPointSyncFindOne( mouse );
		if( shapes.isEmpty() ) return CadPoints.NONE;

		DesignShape shape =  shapes.getFirst();
		if( shape instanceof DesignLine line ) {
			return CadGeometry.midpoint( line.getOrigin(), line.getPoint() );
		} else if( shape instanceof DesignArc arc ) {
			return CadGeometry.midpoint( arc.getOrigin(), arc.getXRadius(), arc.getYRadius(), arc.calcRotate(), arc.getStart(), arc.getExtent() );
		}

		return CadPoints.NONE;
	}
}
