package com.avereon.cartesia.test;

import com.avereon.cartesia.math.CadGeometry;
import javafx.geometry.Point3D;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.Arrays;
import java.util.List;

import static com.avereon.cartesia.TestConstants.TOLERANCE;

public class PointListAssert extends AbstractAssert<PointListAssert, List<Point3D>> {

	protected PointListAssert( List<Point3D> actual ) {
		super( actual, PointListAssert.class );
	}

	public static PointListAssert assertThat(List<Point3D> actual ) {
		return new PointListAssert(actual);
	}

	public PointListAssert areCloseTo( Point3D... expected ) {
		return areCloseTo( Arrays.asList(expected), TOLERANCE.value );
	}

	public PointListAssert areCloseTo( List<Point3D> points, double tolerance ) {
		for( Point3D expected : points ) {
			Assertions.assertThat( areCloseTo( expected, actual, tolerance ) ).isTrue();
		}
		return this;
	}

	static boolean areCloseTo( Point3D point, List<Point3D> points, double tolerance ) {
		for( Point3D check : points ) {
			if( closeTo( point, check, tolerance ) ) return true;
		}
		return false;
	}

	static boolean closeTo( Point3D a, Point3D b, double tolerance ) {
		return CadGeometry.distance( a, b ) <= tolerance;
	}

}
