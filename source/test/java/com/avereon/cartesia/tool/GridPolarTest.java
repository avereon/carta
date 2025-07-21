package com.avereon.cartesia.tool;

import com.avereon.cartesia.test.Point3DAssert;
import com.avereon.curve.math.Constants;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

public class GridPolarTest {

	@Test
	void testFindNearest() {
		Workplane workplane = new Workplane( -10, -10, 10, 10, "1", "90", "1", "45", "1", "30" );
		Point3DAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( 0.3, 0.2, 0 ) ) ).isCloseTo( Point3D.ZERO );
		Point3DAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( -0.3, 0.2, 0 ) ) ).isCloseTo( Point3D.ZERO );
		Point3DAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( -0.3, -0.2, 0 ) ) ).isCloseTo( Point3D.ZERO );
		Point3DAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( 0.3, -0.2, 0 ) ) ).isCloseTo( Point3D.ZERO );

		workplane.setSnapGridY( "30" );
		double d = Math.sqrt( 36 - 9 );
		Point3DAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( -6, 2, 0 ) ) ).isCloseTo( new Point3D( -d, 3, 0 ) );
		Point3DAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( -2, -6, 0 ) ) ).isCloseTo( new Point3D( -3, -d, 0 ) );
		Point3DAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( 6, -2, 0 ) ) ).isCloseTo( new Point3D( d, -3, 0 ) );
		Point3DAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( 2, 6, 0 ) ) ).isCloseTo( new Point3D( 3, d, 0 ) );
	}

	@Test
	void testFindNearestAtZero() {
		Workplane workplane = new Workplane( -10, -10, 10, 10, "1", "90", "1", "45", "1", "30" );
		Point3DAssert.assertThat( Grid.POLAR.getNearest( workplane, Point3D.ZERO ) ).isEqualTo( Point3D.ZERO );
	}

	@Test
	void testFindNearestOffsetOrigin() {
		Workplane workplane = new Workplane( -10, -10, 10, 10, "1", "90", "1", "45", "1", "45" );
		workplane.setOrigin( "0.3, 0.2, 0" );
		Point3DAssert.assertThat( Grid.POLAR.getNearest( workplane, Point3D.ZERO ) ).isCloseTo( new Point3D( 0.3, 0.2, 0 ) );

		workplane.setOrigin( "0.7, 0.8, 0" );
		Point3DAssert.assertThat( Grid.POLAR.getNearest( workplane, Point3D.ZERO ) ).isCloseTo( new Point3D( 0.7 - Constants.SQRT_ONE_HALF, 0.8 - Constants.SQRT_ONE_HALF, 0 ) );
	}

}
