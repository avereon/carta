package com.avereon.cartesia.tool;

import com.avereon.math.Arithmetic;
import javafx.geometry.Point3D;
import javafx.scene.shape.Line;

import java.util.Set;

public class CoordinateSystemOrthographic implements CoordinateSystem {

	@Override
	public Point3D getNearest( Workplane workplane, Point3D point ) {
		point = point.subtract( workplane.getOrigin() );
		point = new Point3D(
			Arithmetic.nearest( point.getX(), workplane.getSnapSpacingX() ),
			Arithmetic.nearest( point.getY(), workplane.getSnapSpacingY() ),
			Arithmetic.nearest( point.getZ(), workplane.getSnapSpacingZ() ) );
		point = point.add( workplane.getOrigin() );
		return point;
	}

	@Override
	public Set<Line> getGridLines( Workplane workplane ) {
		return null;
	}

}
