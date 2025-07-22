package com.avereon.cartesia.tool;

import javafx.scene.shape.Line;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.avereon.cartesia.CartesiaTestTag.AI_GENERATED;
import static org.assertj.core.api.Assertions.assertThat;

public class GridTest {

	@Test
	void getOffsets() {
		assertThat( Grid.getOffsets( 0, 1, -0.5, 0.5 ) ).contains( 0.0 );
		assertThat( Grid.getOffsets( 0, 1, -1, 1 ) ).contains( -1.0, 0.0, 1.0 );
		assertThat( Grid.getOffsets( 1, Math.PI, -2 * Math.PI, 3 * Math.PI ) ).contains( -2 * Math.PI + 1, -Math.PI + 1, 1.0, Math.PI + 1, 2 * Math.PI + 1 );
	}

	@Test
	@Tag( AI_GENERATED )
	void getBoundaryX1() {
		// Normal case: x1 < x2
		assertThat( Grid.getBoundaryX1( 5.0, 10.0, 2.0, 3.0 ) ).isEqualTo( 5.0 * 2.0 - 3.0 );

		// Edge case: x1 = x2
		assertThat( Grid.getBoundaryX1( 5.0, 5.0, 2.0, 3.0 ) ).isEqualTo( 5.0 * 2.0 - 3.0 );

		// Edge case: x1 > x2
		assertThat( Grid.getBoundaryX1( 10.0, 5.0, 2.0, 3.0 ) ).isEqualTo( 5.0 * 2.0 - 3.0 );

		// Edge case: negative coordinates
		assertThat( Grid.getBoundaryX1( -10.0, -5.0, 2.0, 3.0 ) ).isEqualTo( -10.0 * 2.0 - 3.0 );

		// Edge case: zero scale
		assertThat( Grid.getBoundaryX1( 5.0, 10.0, 0.0, 3.0 ) ).isEqualTo( -3.0 );

		// Edge case: negative scale
		assertThat( Grid.getBoundaryX1( 5.0, 10.0, -1.0, 3.0 ) ).isEqualTo( 5.0 * -1.0 - 3.0 );
	}

	@Test
	@Tag( AI_GENERATED )
	void getBoundaryX2() {
		// Normal case: x1 < x2
		assertThat( Grid.getBoundaryX2( 5.0, 10.0, 2.0, 3.0 ) ).isEqualTo( 10.0 * 2.0 + 3.0 );

		// Edge case: x1 = x2
		assertThat( Grid.getBoundaryX2( 5.0, 5.0, 2.0, 3.0 ) ).isEqualTo( 5.0 * 2.0 + 3.0 );

		// Edge case: x1 > x2
		assertThat( Grid.getBoundaryX2( 10.0, 5.0, 2.0, 3.0 ) ).isEqualTo( 10.0 * 2.0 + 3.0 );

		// Edge case: negative coordinates
		assertThat( Grid.getBoundaryX2( -10.0, -5.0, 2.0, 3.0 ) ).isEqualTo( -5.0 * 2.0 + 3.0 );

		// Edge case: zero scale
		assertThat( Grid.getBoundaryX2( 5.0, 10.0, 0.0, 3.0 ) ).isEqualTo( 3.0 );

		// Edge case: negative scale
		assertThat( Grid.getBoundaryX2( 5.0, 10.0, -1.0, 3.0 ) ).isEqualTo( 10.0 * -1.0 + 3.0 );
	}

	@Test
	@Tag( AI_GENERATED )
	void getBoundaryY1() {
		// Normal case: y1 < y2
		assertThat( Grid.getBoundaryY1( 5.0, 10.0, 2.0, 3.0 ) ).isEqualTo( 5.0 * 2.0 - 3.0 );

		// Edge case: y1 = y2
		assertThat( Grid.getBoundaryY1( 5.0, 5.0, 2.0, 3.0 ) ).isEqualTo( 5.0 * 2.0 - 3.0 );

		// Edge case: y1 > y2
		assertThat( Grid.getBoundaryY1( 10.0, 5.0, 2.0, 3.0 ) ).isEqualTo( 5.0 * 2.0 - 3.0 );

		// Edge case: negative coordinates
		assertThat( Grid.getBoundaryY1( -10.0, -5.0, 2.0, 3.0 ) ).isEqualTo( -10.0 * 2.0 - 3.0 );

		// Edge case: zero scale
		assertThat( Grid.getBoundaryY1( 5.0, 10.0, 0.0, 3.0 ) ).isEqualTo( -3.0 );

		// Edge case: negative scale
		assertThat( Grid.getBoundaryY1( 5.0, 10.0, -1.0, 3.0 ) ).isEqualTo( 5.0 * -1.0 - 3.0 );

		// Test the bug: Math.min(y2, y2) instead of Math.min(y1, y2)
		// This test will fail if the bug is present
		assertThat( Grid.getBoundaryY1( 10.0, 5.0, 2.0, 3.0 ) ).isEqualTo( 5.0 * 2.0 - 3.0 );
	}

	@Test
	@Tag( AI_GENERATED )
	void getBoundaryY2() {
		// Normal case: y1 < y2
		assertThat( Grid.getBoundaryY2( 5.0, 10.0, 2.0, 3.0 ) ).isEqualTo( 10.0 * 2.0 + 3.0 );

		// Edge case: y1 = y2
		assertThat( Grid.getBoundaryY2( 5.0, 5.0, 2.0, 3.0 ) ).isEqualTo( 5.0 * 2.0 + 3.0 );

		// Edge case: y1 > y2
		assertThat( Grid.getBoundaryY2( 10.0, 5.0, 2.0, 3.0 ) ).isEqualTo( 10.0 * 2.0 + 3.0 );

		// Edge case: negative coordinates
		assertThat( Grid.getBoundaryY2( -10.0, -5.0, 2.0, 3.0 ) ).isEqualTo( -5.0 * 2.0 + 3.0 );

		// Edge case: zero scale
		assertThat( Grid.getBoundaryY2( 5.0, 10.0, 0.0, 3.0 ) ).isEqualTo( 3.0 );

		// Edge case: negative scale
		assertThat( Grid.getBoundaryY2( 5.0, 10.0, -1.0, 3.0 ) ).isEqualTo( 10.0 * -1.0 + 3.0 );
	}

	@Test
	@Tag( AI_GENERATED )
	void reuseOrNewLine() {
		// Test with empty prior set - should create a new Line
		Set<Line> emptyPrior = new HashSet<>();
		double x1 = 1.0, y1 = 2.0, x2 = 3.0, y2 = 4.0;
		Line newLine = Grid.reuseOrNewLine( emptyPrior, x1, y1, x2, y2 );

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
		Line reusedLine = Grid.reuseOrNewLine( nonEmptyPrior, newX1, newY1, newX2, newY2 );

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
