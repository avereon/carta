package com.avereon.cartesia.test;

import com.avereon.cartesia.math.CadConstants;
import com.avereon.cartesia.math.CadGeometry;
import javafx.geometry.Point3D;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;

public class Point3DAssert extends AbstractAssert<Point3DAssert, Point3D> {

	protected Point3DAssert( Point3D actual ) {
		super( actual, Point3DAssert.class );
	}

	public static Point3DAssert assertThat( Point3D actual ) {
		return new Point3DAssert( actual );
	}

	public Point3DAssert isCloseTo( Point3D expected ) {
		return isCloseTo( expected, CadConstants.RESOLUTION_LENGTH );
	}

	public Point3DAssert isCloseTo( Point3D expected, double tolerance ) {
		Assertions.assertThat( CadGeometry.distance( actual, expected ) ).isCloseTo( 0.0, Offset.offset( tolerance ) );
		return this;
	}

	public Point3DAssert isCloseTo( Point3D expected, Offset<Double> tolerance ) {
		Assertions.assertThat( CadGeometry.distance( actual, expected ) ).isCloseTo( 0.0, tolerance );
		return this;
	}

}
