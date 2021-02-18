package com.avereon.cartesia.snap;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.cartesia.tool.DesignShapeView;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

import java.util.Collection;
import java.util.List;

public class SnapNearest implements Snap {

	@Override
	public String getPromptKey() {
		return "snap-to-nearest";
	}

	@Override
	public Point3D snap( DesignTool tool, Point3D point ) {
		if( point == null ) return null;

		Point3D cursor = tool.worldToScreen( point );

		// Go through all the reference points, convert them to screen coordinates and find the nearest
		double distance;
		double minDistance = Double.MAX_VALUE;
		Point3D nearest = null;

		Collection<Shape> forms = tool.getVisibleShapes();
		for( Shape shape : forms ) {
			DesignShape data = DesignShapeView.getDesignData( shape );
			if( data == null || data.isPreview() ) continue;
			List<ConstructionPoint> cps = DesignShapeView.getConstructionPoints( shape );
			for( ConstructionPoint cp : cps ) {
				distance = cursor.distance( tool.worldToScreen( cp.getLayoutX(), cp.getLayoutY(), 0 ) );
				if( distance < minDistance ) {
					nearest = cp.getLocation();
					minDistance = distance;
				}
			}
		}

		return nearest;
	}

}
