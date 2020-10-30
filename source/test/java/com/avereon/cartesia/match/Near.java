package com.avereon.cartesia.match;

import com.avereon.zarra.test.PointCloseTo;
import javafx.geometry.Point3D;
import org.hamcrest.Matcher;
import org.hamcrest.number.IsCloseTo;

public class Near {

	// This value is a bit roomier than required. Most tests have deltas smaller
	// than 1e-14. This is set just a bit larger to give some wiggle room.
	public static final double TOLERANCE = 1e-12;

	public static Matcher<Double> near( double operand ) {
		return IsCloseTo.closeTo( operand, TOLERANCE );
	}

	public static Matcher<Double> near( double operand, double error ) {
		return IsCloseTo.closeTo( operand, error );
	}

	public static Matcher<Point3D> near( Point3D operand ) {
		return new PointCloseTo( operand, TOLERANCE );
	}

	public static Matcher<Point3D> near( Point3D operand, double tolerance ) {
		return new PointCloseTo( operand, tolerance );
	}

}
