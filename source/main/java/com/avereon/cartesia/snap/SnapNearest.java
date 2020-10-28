package com.avereon.cartesia.snap;

import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.cartesia.tool.DesignShapeView;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

import java.util.Collection;
import java.util.List;

public class SnapNearest implements Snap{

	@Override
	public String getPromptKey() {
		return "snap-to-nearest";
	}

	@Override
	public Point3D snap( DesignTool tool, Point3D mouse ) {
		if( mouse == null ) return null;

		Point3D cursor = tool.worldToMouse( mouse ).add( 0.5, 0.5, 0 );

		// Go through all the reference points, convert them to screen coordinates and find the nearest
		double distance;
		double lastDistance = Double.MAX_VALUE;
		Point3D nearest = null;

		Collection<Shape> forms = tool.getVisibleShapes();
		for( Shape form : forms ) {
			List<ConstructionPoint> cps = DesignShapeView.getConstructionPoints( form );
			for( ConstructionPoint cp : cps ) {
				distance = cursor.distance( tool.worldToMouse( cp.getLayoutX(), cp.getLayoutY(), 0) );
				if( distance < lastDistance ) {
					nearest = cp.getLocation();
					lastDistance = distance;
				}
			}
		}

		return nearest;
	}

}
