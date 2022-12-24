package com.avereon.cartesia;

import com.avereon.cartesia.math.CadConstants;
import javafx.geometry.Point2D;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;

public class Point2DAssert extends AbstractAssert<Point2DAssert, Point2D> {

	protected Point2DAssert( Point2D actual ) {
		super( actual, Point2DAssert.class );
	}

	public static Point2DAssert assertThat ( Point2D actual ) {
		return new Point2DAssert(actual);
	}

	public Point2DAssert isCloseTo( Point2D expected ) {
		return isCloseTo( expected, CadConstants.RESOLUTION_LENGTH );
	}

	public Point2DAssert isCloseTo( Point2D expected, double tolerance ) {
		Assertions.assertThat( actual.distance( expected ) ).isCloseTo( 0.0, Offset.offset( tolerance ) );
		return this;
	}
}
