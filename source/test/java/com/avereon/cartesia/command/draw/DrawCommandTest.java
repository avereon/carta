package com.avereon.cartesia.command.draw;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class DrawCommandTest {

	// Make a concrete class, so we can test the abstract methods
	private final DrawCommand command = new DrawCommand() {};

	@Test
	void testDeriveRotate() {
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 2, 2, 0 ) ) ).isCloseTo( 0.0, TOLERANCE );
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ) ) ).isCloseTo( 45.0, TOLERANCE );
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 1, 3, 0 ) ) ).isCloseTo( 90.0, TOLERANCE );
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 0, 3, 0 ) ) ).isCloseTo( 135.0, TOLERANCE );
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 0, 2, 0 ) ) ).isCloseTo( 180.0, TOLERANCE );
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 0, 1, 0 ) ) ).isCloseTo( -135.0, TOLERANCE );
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 1, 1, 0 ) ) ).isCloseTo( -90.0, TOLERANCE );
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 2, 1, 0 ) ) ).isCloseTo( -45.0, TOLERANCE );
	}

	@Test
	void testDeriveYRadius() {
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 2, 3, 0 ) ) ).isCloseTo( 0.0, TOLERANCE );
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 1, 3, 0 ) ) ).isCloseTo( Math.sqrt( 0.5 ), TOLERANCE );
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 0, 3, 0 ) ) ).isCloseTo( Math.sqrt( 2 ), TOLERANCE );
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 0, 2, 0 ) ) ).isCloseTo( Math.sqrt( 0.5 ), TOLERANCE );
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 0, 1, 0 ) ) ).isCloseTo( 0.0, TOLERANCE );
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 1, 1, 0 ) ) ).isCloseTo( Math.sqrt( 0.5 ), TOLERANCE );
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 2, 1, 0 ) ) ).isCloseTo( Math.sqrt( 2 ), TOLERANCE );
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 2, 2, 0 ) ) ).isCloseTo( Math.sqrt( 0.5 ), TOLERANCE );
	}

}
