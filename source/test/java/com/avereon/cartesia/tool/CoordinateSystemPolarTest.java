package com.avereon.cartesia.tool;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.match.Near.near;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CoordinateSystemPolarTest {

	@Test
	void testFindNearest() {
		Workplane workplane = new Workplane( -10, 10, -10, 10, 1, 90, 1, 45, 1, 5 );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( 0.3, 0.2, 0 ) ), near( Point3D.ZERO ) );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( -0.3, 0.2, 0 ) ), near( Point3D.ZERO ) );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( -0.3, -0.2, 0 ) ), near( Point3D.ZERO ) );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( 0.3, -0.2, 0 ) ), near( Point3D.ZERO ) );

		workplane.setSnapSpacingY( 30 );
		double d = Math.sqrt( 36 - 9 );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( -6, 2, 0 ) ), near( new Point3D( -d, 3, 0 ) ) );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( -2, -6, 0 ) ), near( new Point3D( -3, -d, 0 ) ) );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( 6, -2, 0 ) ), near( new Point3D( d, -3, 0 ) ) );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, new Point3D( 2, 6, 0 ) ), near( new Point3D( 3, d, 0 ) ) );
	}

	@Test
	void testFindNearestAtZero() {
		Workplane workplane = new Workplane( -10, 10, -10, 10, 1, 45, 0.5, 5, 0.1, 1 );
		assertThat( CoordinateSystem.POLAR.getNearest( workplane, Point3D.ZERO ), is( Point3D.ZERO ) );
	}

	@Test
	void testFindNearestOffsetOrigin() {
		//		Workplane workplane = new Workplane( -10, 10, -10, 10, 1, 1, 1 );
		//		workplane.setOrigin( new Point3D( 0.3, 0.2, 0 ) );
		//		assertThat( CoordinateSystem.POLAR.getNearest( workplane, Point3D.ZERO ), Near.near( new Point3D( 0.3, 0.2, 0 ) ) );
		//
		//		workplane.setOrigin( new Point3D( 0.7, 0.8, 0 ) );
		//		assertThat( CoordinateSystem.POLAR.getNearest( workplane, Point3D.ZERO ), Near.near( new Point3D( -0.3, -0.2, 0 ) ) );
	}

}
