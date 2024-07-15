package com.avereon.cartesia.tool;

import com.avereon.cartesia.test.PointAssert;
import com.avereon.curve.math.Constants;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GridPolarTest {

	@Test
	void testFindNearest() {
		DesignWorkplane workplane = new DesignWorkplane( -10, -10, 10, 10, "1", "90", "1", "45", "1", "30" );
		PointAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( 0.3, 0.2, 0 ) ) ).isCloseTo( Point3D.ZERO );
		PointAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( -0.3, 0.2, 0 ) ) ).isCloseTo( Point3D.ZERO );
		PointAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( -0.3, -0.2, 0 ) ) ).isCloseTo( Point3D.ZERO );
		PointAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( 0.3, -0.2, 0 ) ) ).isCloseTo( Point3D.ZERO );

		workplane.setSnapGridY( "30" );
		double d = Math.sqrt( 36 - 9 );
		PointAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( -6, 2, 0 ) ) ).isCloseTo( new Point3D( -d, 3, 0 ) );
		PointAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( -2, -6, 0 ) ) ).isCloseTo( new Point3D( -3, -d, 0 ) );
		PointAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( 6, -2, 0 ) ) ).isCloseTo( new Point3D( d, -3, 0 ) );
		PointAssert.assertThat( Grid.POLAR.getNearest( workplane, new Point3D( 2, 6, 0 ) ) ).isCloseTo( new Point3D( 3, d, 0 ) );
	}

	@Test
	void testFindNearestAtZero() {
		DesignWorkplane workplane = new DesignWorkplane( -10, -10, 10, 10, "1", "90", "1", "45", "1", "30" );
		PointAssert.assertThat( Grid.POLAR.getNearest( workplane, Point3D.ZERO ) ).isEqualTo( Point3D.ZERO );
	}

	@Test
	void testFindNearestOffsetOrigin() {
		DesignWorkplane workplane = new DesignWorkplane( -10, -10, 10, 10, "1", "90", "1", "45", "1", "45" );
		workplane.setOrigin( "0.3, 0.2, 0" );
		PointAssert.assertThat( Grid.POLAR.getNearest( workplane, Point3D.ZERO ) ).isCloseTo( new Point3D( 0.3, 0.2, 0 ) );

		workplane.setOrigin( "0.7, 0.8, 0" );
		PointAssert.assertThat( Grid.POLAR.getNearest( workplane, Point3D.ZERO ) ).isCloseTo( new Point3D( 0.7 - Constants.SQRT_ONE_HALF, 0.8 - Constants.SQRT_ONE_HALF, 0 ) );
	}

	@Test
	void testGetGridDots() throws Exception {
		DesignWorkplane workplane = new DesignWorkplane( -10, -10, 10, 10, "1", "90", "1", "45", "1", "45" );
		List<Shape> dots = Grid.POLAR.getGridDots( workplane );
		assertThat( dots.size() ).isEqualTo( 0 );
	}

	@Test
	void testGetGridLines() throws Exception {
		DesignWorkplane workplane = new DesignWorkplane( -10, -10, 10, 10, "1", "45", "0.5", "30", "0.1", "15" );
		List<Shape> lines = Grid.POLAR.createFxGeometryGrid( workplane );
		assertThat( lines.size() ).isEqualTo( 44 );
	}

}
