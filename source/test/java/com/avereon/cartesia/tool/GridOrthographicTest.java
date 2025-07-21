package com.avereon.cartesia.tool;

import com.avereon.cartesia.test.Point3DAssert;
import javafx.geometry.Point3D;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class GridOrthographicTest {

	@Test
	void testFindNearest() throws Exception {
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
	void testFindNearestAtZero() throws Exception {
		Workplane workplane = new Workplane();
		Point3DAssert.assertThat( Grid.ORTHO.getNearest( workplane, Point3D.ZERO ) ).isEqualTo( Point3D.ZERO );
	}

	@Test
	void testFindNearestOffsetOrigin() throws Exception {
		Workplane workplane = new Workplane( -10, 10, -10, 10, "1", "1", "1" );
		workplane.setOrigin( "0.3, 0.2, 0" );
		Point3DAssert.assertThat( Grid.ORTHO.getNearest( workplane, Point3D.ZERO ) ).isCloseTo( new Point3D( 0.3, 0.2, 0 ) );

		workplane.setOrigin( "0.7, 0.8, 0" );
		Point3DAssert.assertThat( Grid.ORTHO.getNearest( workplane, Point3D.ZERO ) ).isCloseTo( new Point3D( -0.3, -0.2, 0 ) );
	}

	@Test
	void testGetOffsets() {
		assertThat( Grid.getOffsets( 0, 1, -0.5, 0.5 ) ).contains( 0.0 );
		assertThat( Grid.getOffsets( 0, 1, -1, 1 ) ).contains( -1.0, 0.0, 1.0 );
		assertThat( Grid.getOffsets( 1, Math.PI, -2 * Math.PI, 3 * Math.PI ) ).contains( -2 * Math.PI + 1, -Math.PI + 1, 1.0, Math.PI + 1, 2 * Math.PI + 1 );
	}

	@Test
	void gridOffsetsMissingOne() {
		// https://github.com/avereon/carta/issues/155
		double origin = 0.0;
		double interval = 63.77952755905511;
		double lowerBound = -672.0;
		double upperBound = 672.0;
		assertThat( Grid.getOffsets( origin, interval, lowerBound, upperBound ) ).hasSize( 21 );
	}

	@Test
	void getGridLinesCommon() throws Exception {
		Workplane workplane = new Workplane( -10, -8, 10, 8, "1", "0.5", "0.1" );
		Collection<Shape> lines = Grid.ORTHO.createFxGeometryGrid( workplane, 1.0 );

		// X lines = 10 - -10 = 20 / 0.5 + 1 = 41
		// Y lines = 8 - -8 = 16 / 0.5 + 1 = 33
		// All lines = 41 + 33
		assertThat( lines.size() ).isEqualTo( 74 );
	}

	@Test
	void getGridLinesOffOrigin() throws Exception {
		Workplane workplane = new Workplane( 5, 4, 10, 8, "1", "0.5", "0.1" );
		Collection<Shape> lines = Grid.ORTHO.createFxGeometryGrid( workplane );

		// X lines = 10 - 5 = 5 / 0.5 + 1 = 11
		// Y lines = 8 - 4 = 4 / 0.5 + 1 = 9
		// All lines = 11 + 9
		assertThat( lines.size() ).isEqualTo( 20 );
	}

	@Test
	void reuseOrNew() {
		// Test with empty prior set - should create a new Line
		Set<Line> emptyPrior = new HashSet<>();
		double x1 = 1.0, y1 = 2.0, x2 = 3.0, y2 = 4.0;
		Line newLine = GridOrthographic.reuseOrNew( emptyPrior, x1, y1, x2, y2 );

		assertThat( newLine ).isNotNull();
		assertThat( newLine.getStartX() ).isEqualTo( x1 );
		assertThat( newLine.getStartY() ).isEqualTo( y1 );
		assertThat( newLine.getEndX() ).isEqualTo( x2 );
		assertThat( newLine.getEndY() ).isEqualTo( y2 );

		// Test with non-empty prior set - should reuse a Line
		Set<Line> nonEmptyPrior = new HashSet<>();
		Line existingLine = new Line( 5.0, 6.0, 7.0, 8.0 );
		nonEmptyPrior.add( existingLine );

		double newX1 = 10.0, newY1 = 20.0, newX2 = 30.0, newY2 = 40.0;
		Line reusedLine = GridOrthographic.reuseOrNew( nonEmptyPrior, newX1, newY1, newX2, newY2 );

		// Verify the line was reused (same instance)
		assertThat( reusedLine ).isSameAs( existingLine );

		// Verify the line was removed from the prior set
		assertThat( nonEmptyPrior ).isEmpty();

		// Verify the line's coordinates were updated
		assertThat( reusedLine.getStartX() ).isEqualTo( newX1 );
		assertThat( reusedLine.getStartY() ).isEqualTo( newY1 );
		assertThat( reusedLine.getEndX() ).isEqualTo( newX2 );
		assertThat( reusedLine.getEndY() ).isEqualTo( newY2 );
	}

}
