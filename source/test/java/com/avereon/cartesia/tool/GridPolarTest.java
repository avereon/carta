package com.avereon.cartesia.tool;

import com.avereon.cartesia.test.Point3DAssert;
import com.avereon.curve.math.Constants;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class GridPolarTest {

	@Test
	void getNearest() {
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
	void getNearestAtZero() {
		Workplane workplane = new Workplane( -10, -10, 10, 10, "1", "90", "1", "45", "1", "30" );
		Point3DAssert.assertThat( Grid.POLAR.getNearest( workplane, Point3D.ZERO ) ).isEqualTo( Point3D.ZERO );
	}

	@Test
	void getNearestOffsetOrigin() {
		Workplane workplane = new Workplane( -10, -10, 10, 10, "1", "90", "1", "45", "1", "45" );
		workplane.setOrigin( "0.3, 0.2, 0" );
		Point3DAssert.assertThat( Grid.POLAR.getNearest( workplane, Point3D.ZERO ) ).isCloseTo( new Point3D( 0.3, 0.2, 0 ) );

		workplane.setOrigin( "0.7, 0.8, 0" );
		Point3DAssert.assertThat( Grid.POLAR.getNearest( workplane, Point3D.ZERO ) ).isCloseTo( new Point3D( 0.7 - Constants.SQRT_ONE_HALF, 0.8 - Constants.SQRT_ONE_HALF, 0 ) );
	}

	@Test
	void getGridLinesCommon() {
		Workplane workplane = new Workplane( -7, -5, 7, 5 );
		workplane.setGridSystem( Grid.POLAR );
		workplane.setMajorGridX( "1.0" );
		workplane.setMajorGridY( "30" );
		workplane.setMinorGridX( "0.2" );
		workplane.setMinorGridY( "10" );

		Collection<Shape> shapes = Grid.POLAR.createFxGeometryGrid( workplane, 1.0 );

		// Radius to workplane corners is 10 (Math.sqrt(8*8+6*6))

		// (R)adius circles = 0 -> 10 = 10 / 0.2 + 1 = 51
		// (A)ngle lines = -180 -> 180 = 360 / 10 = 36
		// All shape = 51 + 36 = 87
		// Add (major/minor) more lines for margin = 88
		assertThat( shapes.size() ).isEqualTo( 88 );
	}

}
