package com.avereon.cartesia.snap;

import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.tool.view.DesignShapeView;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

import java.util.List;

public class SnapCenter  implements Snap{

	@Override
	public String getPromptKey() {
		return "snap-to-center";
	}

	@Override
	public Point3D snap( DesignTool tool, Point3D point ) {
		if( point == null ) return null;

		Point3D mouse = tool.worldToScreen( point );
		List<Shape> shapes = tool.screenPointFindOneAndWait( mouse );
		if( shapes.isEmpty() ) return CadPoints.NONE;

		DesignShape shape = DesignShapeView.getDesignData( shapes.get( 0 ) );
		if( shape instanceof DesignLine ) {
			DesignLine line = (DesignLine)shape;
			return CadGeometry.midpoint( line.getOrigin(), line.getPoint() );
		} else if( shape instanceof DesignEllipse ) {
			DesignEllipse ellipse = (DesignEllipse)shape;
			return ellipse.getOrigin();
		}

		return CadPoints.NONE;
	}
}
