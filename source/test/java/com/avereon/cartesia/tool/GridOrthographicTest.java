package com.avereon.cartesia.tool;

import com.avereon.cartesia.test.Point3DAssert;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class GridOrthographicTest {

	@Test
	void getNearest() {
		Workplane workplane = new Workplane( -10, 10, -10, 10, "1", "1", "1" );
		Point3DAssert.assertThat( Grid.ORTHO.getNearest( workplane, new Point3D( 0.3, 0.2, 0 ) ) ).isCloseTo( Point3D.ZERO );
		Point3DAssert.assertThat( Grid.ORTHO.getNearest( workplane, new Point3D( -0.3, 0.2, 0 ) ) ).isCloseTo( Point3D.ZERO );
		Point3DAssert.assertThat( Grid.ORTHO.getNearest( workplane, new Point3D( -0.3, -0.2, 0 ) ) ).isCloseTo( Point3D.ZERO );
		Point3DAssert.assertThat( Grid.ORTHO.getNearest( workplane, new Point3D( 0.3, -0.2, 0 ) ) ).isCloseTo( Point3D.ZERO );

		workplane.setSnapGridX( "0.2" );
		Point3DAssert.assertThat( Grid.ORTHO.getNearest( workplane, new Point3D( -14.623984, 2.34873, 0 ) ) ).isCloseTo( new Point3D( -14.6, 2, 0 ) );
		Point3DAssert.assertThat( Grid.ORTHO.getNearest( workplane, new Point3D( -4.623984, -6.34873, 0 ) ) ).isCloseTo( new Point3D( -4.6, -6, 0 ) );
		Point3DAssert.assertThat( Grid.ORTHO.getNearest( workplane, new Point3D( 7.623984, -23.34873, 0 ) ) ).isCloseTo( new Point3D( 7.6, -23, 0 ) );
		Point3DAssert.assertThat( Grid.ORTHO.getNearest( workplane, new Point3D( 67.623984, 13.34873, 0 ) ) ).isCloseTo( new Point3D( 67.6, 13, 0 ) );
	}

	@Test
	void getNearestAtZero() {
		Workplane workplane = new Workplane();
		Point3DAssert.assertThat( Grid.ORTHO.getNearest( workplane, Point3D.ZERO ) ).isEqualTo( Point3D.ZERO );
	}

	@Test
	void getNearestWithOffsetOrigin() {
		Workplane workplane = new Workplane( -10, 10, -10, 10, "1", "1", "1" );
		workplane.setOrigin( "0.3, 0.2, 0" );
		Point3DAssert.assertThat( Grid.ORTHO.getNearest( workplane, Point3D.ZERO ) ).isCloseTo( new Point3D( 0.3, 0.2, 0 ) );

		workplane.setOrigin( "0.7, 0.8, 0" );
		Point3DAssert.assertThat( Grid.ORTHO.getNearest( workplane, Point3D.ZERO ) ).isCloseTo( new Point3D( -0.3, -0.2, 0 ) );
	}

	@Test
	void getOffsets() {
		assertThat( Grid.getOffsets( 0, 1, -0.5, 0.5 ) ).contains( 0.0 );
		assertThat( Grid.getOffsets( 0, 1, -1, 1 ) ).contains( -1.0, 0.0, 1.0 );
		assertThat( Grid.getOffsets( 1, Math.PI, -2 * Math.PI, 3 * Math.PI ) ).contains( -2 * Math.PI + 1, -Math.PI + 1, 1.0, Math.PI + 1, 2 * Math.PI + 1 );
	}

	@Test
	@Tag( "recursion" )
	void issue155() {
		// https://github.com/avereon/carta/issues/155
		double origin = 0.0;
		double interval = 63.77952755905511;
		double lowerBound = -672.0;
		double upperBound = 672.0;
		assertThat( Grid.getOffsets( origin, interval, lowerBound, upperBound ) ).hasSize( 21 );
	}

	@Test
	void getGridLinesCommon() {
		Workplane workplane = new Workplane( -10, -8, 10, 8, "1", "0.5", "0.1" );
		Collection<Shape> lines = Grid.ORTHO.createFxGeometryGrid( workplane, 1.0 );

		// X lines = -10 -> 10 = 20 / 0.5 + 1 = 41
		// Y lines = -8 -> 8 = 16 / 0.5 + 1 = 33
		// All lines = 41 + 33 = 74
		// Add (major/minor) * 4 more lines for margin = 82
		assertThat( lines.size() ).isEqualTo( 82 );
	}

}
