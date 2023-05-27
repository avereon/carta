package com.avereon.cartesia.snap;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadIntersection;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.view.DesignShapeView;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;
import lombok.CustomLog;

import java.util.List;

@CustomLog
public class SnapIntersection implements Snap {

	@Override
	public String getPromptKey() {
		return "snap-to-intersection";
	}

	@Override
	public Point3D snap( BaseDesignTool tool, Point3D point ) {
		if( point == null ) return CadPoints.NONE;

		Point3D mouse = tool.worldToScreen( point );
		List<Shape> shapes = tool.screenPointFindAllAndWait( mouse );
		if( shapes.size() < 2 ) return CadPoints.NONE;

		DesignShape shape1 = DesignShapeView.getDesignData( shapes.get( 0 ) );
		DesignShape shape2 = DesignShapeView.getDesignData( shapes.get( 1 ) );
		List<Point3D> points = CadIntersection.getIntersections( shape1, shape2 );

		// Find the closest intersection point
		Point3D nearest = CadPoints.getNearest( point, points );

		return nearest == null ? CadPoints.NONE : nearest;
	}

}
