package com.avereon.cartesia.tool;

import com.avereon.cartesia.math.Geometry;
import com.avereon.math.Arithmetic;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

import java.util.List;

public class CoordinateSystemPolar implements CoordinateSystem {

	@Override
	public Point3D getNearest( Workplane workplane, Point3D point ) {
		// This can be determined by calculating the nearest point
		// and then converting from polar to cartesian coordinates
		point = point.subtract( workplane.getOrigin() );
		point = Geometry.cartesianToPolar( point );

		point = new Point3D(
			Arithmetic.nearest( point.getX(), workplane.getSnapSpacingX() ),
			Arithmetic.nearest( point.getY(), workplane.getSnapSpacingY() ),
			Arithmetic.nearest( point.getZ(), workplane.getSnapSpacingZ() )
		);
		System.err.println( "polar=" + point );

		point = Geometry.polarToCartesian( point );
		point = point.add( workplane.getOrigin() );

		return point;
	}

	@Override
	public List<Shape> getGridDots( Workplane workplane) {
		return List.of();
	}

	@Override
	public List<Shape> getGridLines( Workplane workplane) {
		// The x spacing will be radius
		// The y spacing will be angle in degrees


		return List.of();
	}

}
