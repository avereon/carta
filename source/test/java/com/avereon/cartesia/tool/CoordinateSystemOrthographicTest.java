package com.avereon.cartesia.tool;

import com.avereon.cartesia.match.Near;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class CoordinateSystemOrthographicTest {

	@Test
	void testFindNearest() throws Exception {
		Workplane workplane = new Workplane( -10, 10, -10, 10, "1", "1", "1" );
		assertThat( CoordinateSystem.ORTHO.getNearest( workplane, new Point3D( 0.3, 0.2, 0 ) ), Near.near( Point3D.ZERO ) );
		assertThat( CoordinateSystem.ORTHO.getNearest( workplane, new Point3D( -0.3, 0.2, 0 ) ), Near.near( Point3D.ZERO ) );
		assertThat( CoordinateSystem.ORTHO.getNearest( workplane, new Point3D( -0.3, -0.2, 0 ) ), Near.near( Point3D.ZERO ) );
		assertThat( CoordinateSystem.ORTHO.getNearest( workplane, new Point3D( 0.3, -0.2, 0 ) ), Near.near( Point3D.ZERO ) );

		workplane.setSnapGridX( "0.2" );
		assertThat( CoordinateSystem.ORTHO.getNearest( workplane, new Point3D( -14.623984, 2.34873, 0 ) ), Near.near( new Point3D( -14.6, 2, 0 ) ) );
		assertThat( CoordinateSystem.ORTHO.getNearest( workplane, new Point3D( -4.623984, -6.34873, 0 ) ), Near.near( new Point3D( -4.6, -6, 0 ) ) );
		assertThat( CoordinateSystem.ORTHO.getNearest( workplane, new Point3D( 7.623984, -23.34873, 0 ) ), Near.near( new Point3D( 7.6, -23, 0 ) ) );
		assertThat( CoordinateSystem.ORTHO.getNearest( workplane, new Point3D( 67.623984, 13.34873, 0 ) ), Near.near( new Point3D( 67.6, 13, 0 ) ) );
	}

	@Test
	void testFindNearestAtZero() throws Exception {
		Workplane workplane = new Workplane();
		assertThat( CoordinateSystem.ORTHO.getNearest( workplane, Point3D.ZERO ), is( Point3D.ZERO ) );
	}

	@Test
	void testFindNearestOffsetOrigin() throws Exception {
		Workplane workplane = new Workplane( -10, 10, -10, 10, "1", "1", "1" );
		workplane.setOrigin( "0.3, 0.2, 0" );
		assertThat( CoordinateSystem.ORTHO.getNearest( workplane, Point3D.ZERO ), Near.near( new Point3D( 0.3, 0.2, 0 ) ) );

		workplane.setOrigin( "0.7, 0.8, 0" );
		assertThat( CoordinateSystem.ORTHO.getNearest( workplane, Point3D.ZERO ), Near.near( new Point3D( -0.3, -0.2, 0 ) ) );
	}

	@Test
	void testGetOffsets() throws Exception {
		assertThat( CoordinateSystem.getOffsets( 0, 1, -0.5, 0.5 ), contains( 0.0 ) );
		assertThat( CoordinateSystem.getOffsets( 0, 1, -1, 1 ), contains( -1.0, 0.0, 1.0 ) );
		assertThat( CoordinateSystem.getOffsets( 1, Math.PI, -2 * Math.PI, 3 * Math.PI ),
			contains( -2 * Math.PI + 1, -Math.PI + 1, 1.0, Math.PI + 1, 2 * Math.PI + 1 )
		);
	}

	@Test
	void testGetGridDots() throws Exception {
		Workplane workplane = new Workplane( -10, 10, -10, 10, "1", "90", "1", "45", "1", "45" );
		List<Shape> dots = CoordinateSystem.ORTHO.getGridDots( workplane );
		assertThat( dots.size(), is( 0 ) );
	}

	@Test
	void getGridLinesCommon() throws Exception {
		Workplane workplane = new Workplane( -10, -8, 10, 8, "1", "0.5", "0.1" );
		List<Shape> lines = CoordinateSystem.ORTHO.getGridLines( workplane );

		// X lines = 10 - -10 = 20 / 0.5 + 1 = 41
		// Y lines = 8 - -8 = 16 / 0.5 + 1 = 33
		// All lines = 41 + 33
		assertThat( lines.size(), is( 74 ) );
	}

	@Test
	void getGridLinesOffOrigin() throws Exception {
		Workplane workplane = new Workplane( 5, 4, 10, 8, "1", "0.5", "0.1" );
		List<Shape> lines = CoordinateSystem.ORTHO.getGridLines( workplane );

		// X lines = 10 - 5 = 5 / 0.5 + 1 = 11
		// Y lines = 8 - 4 = 4 / 0.5 + 1 = 9
		// All lines = 11 + 9
		assertThat( lines.size(), is( 20 ) );
	}

}
