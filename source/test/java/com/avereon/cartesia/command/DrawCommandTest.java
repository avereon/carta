package com.avereon.cartesia.command;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.match.Near.near;
import static org.hamcrest.MatcherAssert.assertThat;

public class DrawCommandTest {

	DrawCommand command = new DrawCommand() {};

	@Test
	void testDeriveRotate() {
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 2, 2, 0 ) ), near( 0.0 ) );
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ) ), near( 45.0 ) );
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 1, 3, 0 ) ), near( 90.0 ) );
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 0, 3, 0 ) ), near( 135.0 ) );
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 0, 2, 0 ) ), near( 180.0 ) );
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 0, 1, 0 ) ), near( -135.0 ) );
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 1, 1, 0 ) ), near( -90.0 ) );
		assertThat( command.deriveRotate( new Point3D( 1, 2, 0 ), new Point3D( 2, 1, 0 ) ), near( -45.0 ) );
	}

	@Test
	void testDeriveYRadius() {
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 2, 3, 0 ) ), near( 0.0 ) );
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 1, 3, 0 ) ), near( Math.sqrt( 0.5 ) ) );
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 0, 3, 0 ) ), near( Math.sqrt( 2 ) ) );
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 0, 2, 0 ) ), near( Math.sqrt( 0.5 ) ) );
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 0, 1, 0 ) ), near( 0.0 ) );
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 1, 1, 0 ) ), near( Math.sqrt( 0.5 ) ) );
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 2, 1, 0 ) ), near( Math.sqrt( 2 ) ) );
		assertThat( command.deriveYRadius( new Point3D( 1, 2, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 2, 2, 0 ) ), near( Math.sqrt( 0.5 ) ) );
	}

}
