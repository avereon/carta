package com.avereon.cartesia.tool;

import com.avereon.cartesia.match.Near;
import com.avereon.curve.math.Constants;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.avereon.cartesia.match.Near.near;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CoordinateSystemPolarTest {

	@Test
	void testFindNearest() throws Exception {
		DesignWorkplane workplane = new DesignWorkplane( -10, -10, 10, 10, "1", "90", "1", "45", "1", "30" );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( 0.3, 0.2, 0 ) ), near( Point3D.ZERO ) );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( -0.3, 0.2, 0 ) ), near( Point3D.ZERO ) );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( -0.3, -0.2, 0 ) ), near( Point3D.ZERO ) );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( 0.3, -0.2, 0 ) ), near( Point3D.ZERO ) );

		workplane.setSnapGridY( "30" );
		double d = Math.sqrt( 36 - 9 );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( -6, 2, 0 ) ), near( new Point3D( -d, 3, 0 ) ) );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( -2, -6, 0 ) ), near( new Point3D( -3, -d, 0 ) ) );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( 6, -2, 0 ) ), near( new Point3D( d, -3, 0 ) ) );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( 2, 6, 0 ) ), near( new Point3D( 3, d, 0 ) ) );
	}

	@Test
	void testFindNearestAtZero() throws Exception {
		DesignWorkplane workplane = new DesignWorkplane( -10, -10, 10, 10, "1", "90", "1", "45", "1", "30" );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, Point3D.ZERO ), is( Point3D.ZERO ) );
	}

	@Test
	void testFindNearestOffsetOrigin() throws Exception {
		DesignWorkplane workplane = new DesignWorkplane( -10, -10, 10, 10, "1", "90", "1", "45", "1", "45" );
		workplane.setOrigin( "0.3, 0.2, 0" );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, Point3D.ZERO ), Near.near( new Point3D( 0.3, 0.2, 0 ) ) );

		workplane.setOrigin( "0.7, 0.8, 0" );
		assertThat(
			CoordinateSystem.POLAR.getNearest( workplane, Point3D.ZERO ),
			Near.near( new Point3D( 0.7 - Constants.SQRT_ONE_HALF, 0.8 - Constants.SQRT_ONE_HALF, 0 ) )
		);
	}

	@Test
	void testGetGridDots() throws Exception {
		DesignWorkplane workplane = new DesignWorkplane( -10, -10, 10, 10, "1", "90", "1", "45", "1", "45" );
		List<Shape> dots = CoordinateSystem.POLAR.getGridDots( workplane );
		assertThat( dots.size(), is( 0 ) );
	}

	@Test
	void testGetGridLines() throws Exception {
		DesignWorkplane workplane = new DesignWorkplane( -10, -10, 10, 10, "1", "45", "0.5", "30", "0.1", "15" );
		List<Shape> lines = CoordinateSystem.POLAR.getGridLines( workplane );
		assertThat( lines.size(), is( 44 ) );
	}

}
