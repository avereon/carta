package com.avereon.cartesia;

import com.avereon.cartesia.math.CadGeometry;
import javafx.geometry.Point3D;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;

public class PointAssert extends AbstractAssert<PointAssert, Point3D> {

	protected PointAssert( Point3D actual ) {
		super( actual, PointAssert.class );
	}

	public static PointAssert assertThat (Point3D actual ) {
		return new PointAssert(actual);
	}

	public PointAssert isCloseTo( Point3D expected ) {
		return this;
	}

	public PointAssert isCloseTo( Point3D expected, double tolerance ) {
		Assertions.assertThat( CadGeometry.distance( actual, expected ) ).isCloseTo( 0.0, Offset.offset( tolerance ) );
		return this;
	}
}
