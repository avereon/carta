package com.avereon.cartesia.command;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class CommandTest {

	private final Command command = new Command() {};

	@Test
	void testDeriveStart() {
		assertThat( command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 1, 2, 0 ) ) ).isCloseTo( 0.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 2, 2, 0 ) ) ).isCloseTo( 0.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 2, 3, 0 ) ) ).isCloseTo( 45.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 1, 3, 0 ) ) ).isCloseTo( 90.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 0, 3, 0 ) ) ).isCloseTo( 135.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 0, 2, 0 ) ) ).isCloseTo( 180.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 0, 1, 0 ) ) ).isCloseTo( -135.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 1, 1, 0 ) ) ).isCloseTo( -90.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 2, 1, 0 ) ) ).isCloseTo( -45.0, TOLERANCE );
	}

	@Test
	void testDeriveStartWithRotate() {
		assertThat( command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 3, 2, 0 ) ) ).isCloseTo( 0.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 2, 2, 0 ) ) ).isCloseTo( 45.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 1, 2, 0 ) ) ).isCloseTo( 90.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 1, 1, 0 ) ) ).isCloseTo( 135.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 1, 0, 0 ) ) ).isCloseTo( 180.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 2, 0, 0 ) ) ).isCloseTo( -135.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 3, 0, 0 ) ) ).isCloseTo( -90.0, TOLERANCE );
		assertThat( command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 3, 1, 0 ) ) ).isCloseTo( -45.0, TOLERANCE );
	}

	@Test
	void testDeriveExtent() {
		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 2, 3, 0 ), 1.0 ) ).isCloseTo( 0.0, TOLERANCE );
		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 2, 3, 0 ), -1.0 ) ).isCloseTo( 0.0, TOLERANCE );

		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 1, 3, 0 ), 1.0 ) ).isCloseTo( 45.0, TOLERANCE );
		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 1, 3, 0 ), -1.0 ) ).isCloseTo( -315.0, TOLERANCE );

		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 0, 3, 0 ), 1.0 ) ).isCloseTo( 90.0, TOLERANCE );
		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 0, 3, 0 ), -1.0 ) ).isCloseTo( -270.0, TOLERANCE );

		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 0, 2, 0 ), 1.0 ) ).isCloseTo( 135.0, TOLERANCE );
		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 0, 2, 0 ), -1.0 ) ).isCloseTo( -225.0, TOLERANCE );

		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 0, 1, 0 ), 1.0 ) ).isCloseTo( 180.0, TOLERANCE );
		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 0, 1, 0 ), -1.0 ) ).isCloseTo( -180.0, TOLERANCE );

		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 1, 1, 0 ), 1.0 ) ).isCloseTo( 225.0, TOLERANCE );
		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 1, 1, 0 ), -1.0 ) ).isCloseTo( -135.0, TOLERANCE );

		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 2, 1, 0 ), 1.0 ) ).isCloseTo( 270.0, TOLERANCE );
		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 2, 1, 0 ), -1.0 ) ).isCloseTo( -90.0, TOLERANCE );

		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 2, 2, 0 ), 1.0 ) ).isCloseTo( 315.0, TOLERANCE );
		assertThat( command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 2, 2, 0 ), -1.0 ) ).isCloseTo( -45.0, TOLERANCE );
	}

	@Test
	void testGetExtentSpin() {
		// Initial
		assertThat( command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, 0, 0 ), new Point3D( 1, 1, 0 ), 0.0 ) ).isCloseTo( 1.0, TOLERANCE );
		assertThat( command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, 0, 0 ), new Point3D( 1, -1, 0 ), 0.0 ) ).isCloseTo( -1.0, TOLERANCE );

		// Maintain CCW
		assertThat( command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, 1, 0 ), new Point3D( 1, 1.5, 0 ), 1.0 ) ).isCloseTo( 1.0, TOLERANCE );
		assertThat( command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, 1, 0 ), new Point3D( 1, 0.5, 0 ), 1.0 ) ).isCloseTo( 1.0, TOLERANCE );

		// Maintain CW
		assertThat( command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, -1, 0 ), new Point3D( 1, -0.5, 0 ), -1.0 ) ).isCloseTo( -1.0, TOLERANCE );
		assertThat( command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, -1, 0 ), new Point3D( 1, -1.5, 0 ), -1.0 ) ).isCloseTo( -1.0, TOLERANCE );

		// Switch
		assertThat( command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, 0.5, 0 ), new Point3D( 1, -0.5, 0 ), 1.0 ) ).isCloseTo( -1.0, TOLERANCE );
		assertThat( command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, -0.5, 0 ), new Point3D( 1, 0.5, 0 ), -1.0 ) ).isCloseTo( 1.0, TOLERANCE );
	}

	@Test
	void testGetExtentSpinWithRotatedArc() {
		// NOTE These tests use the point where the crossover line goes through (4.0,5.0)

		// Initial
		assertThat( command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4, 5, 0 ), new Point3D( 4, 3, 0 ), 0.0 ) ).isCloseTo( 1.0, TOLERANCE );
		assertThat( command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4, 5, 0 ), new Point3D( 5, 5, 0 ), 0.0 ) ).isCloseTo( -1.0, TOLERANCE );

		// Maintain CCW
		assertThat( command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4, 4.9, 0 ), new Point3D( 4, 4.5, 0 ), 1.0 ) ).isCloseTo( 1.0, TOLERANCE );
		assertThat( command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4.5, 5, 0 ), new Point3D( 4.1, 5, 0 ), 1.0 ) ).isCloseTo( 1.0, TOLERANCE );

		// Maintain CW
		assertThat( command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4, 4.5, 0 ), new Point3D( 4, 4.9, 0 ), -1.0 ) ).isCloseTo( -1.0, TOLERANCE );
		assertThat( command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4.1, 5, 0 ), new Point3D( 4.5, 5, 0 ), -1.0 ) ).isCloseTo( -1.0, TOLERANCE );

		// Switch
		assertThat( command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4, 4.9, 0 ), new Point3D( 4, 5.1, 0 ), 1.0 ) ).isCloseTo( -1.0, TOLERANCE );
		assertThat( command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4, 5.1, 0 ), new Point3D( 4, 4.9, 0 ), -1.0 ) ).isCloseTo( 1.0, TOLERANCE );
		assertThat( command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 3.9, 5, 0 ), new Point3D( 4.1, 5, 0 ), 1.0 ) ).isCloseTo( -1.0, TOLERANCE );
		assertThat( command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4.1, 5, 0 ), new Point3D( 3.9, 5, 0 ), 1.0 ) ).isCloseTo( 1.0, TOLERANCE );
	}

}
