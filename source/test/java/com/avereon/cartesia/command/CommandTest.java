package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignArc;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.match.Near.near;
import static org.hamcrest.MatcherAssert.assertThat;

public class CommandTest {

	Command command = new Command() {};

	@Test
	void testDeriveStart() {
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 0 ), 1.0, 0.0, 0.0, DesignArc.Type.OPEN );
		assertThat( command.deriveStart( arc, new Point3D( 1, 2, 0 ) ), near( 0.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 2, 2, 0 ) ), near( 0.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 2, 3, 0 ) ), near( 45.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 1, 3, 0 ) ), near( 90.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 0, 3, 0 ) ), near( 135.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 0, 2, 0 ) ), near( 180.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 0, 1, 0 ) ), near( -135.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 1, 1, 0 ) ), near( -90.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 2, 1, 0 ) ), near( -45.0 ) );
	}

	@Test
	void testDeriveStartWithRotate() {
		DesignArc arc = new DesignArc( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, 0.0, 0.0, DesignArc.Type.OPEN );
		assertThat( command.deriveStart( arc, new Point3D( 3, 2, 0 ) ), near( 0.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 2, 2, 0 ) ), near( 45.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 1, 2, 0 ) ), near( 90.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 1, 1, 0 ) ), near( 135.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 1, 0, 0 ) ), near( 180.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 2, 0, 0 ) ), near( -135.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 3, 0, 0 ) ), near( -90.0 ) );
		assertThat( command.deriveStart( arc, new Point3D( 3, 1, 0 ) ), near( -45.0 ) );
	}

	@Test
	void testDeriveExtent() {
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 0 ), 1.0, 45.0, 0.0, DesignArc.Type.OPEN );
		assertThat( command.deriveExtent( arc, new Point3D( 2, 3, 0 ), 1.0 ), near( 0.0 ) );
		assertThat( command.deriveExtent( arc, new Point3D( 2, 3, 0 ), -1.0 ), near( 0.0 ) );

		assertThat( command.deriveExtent( arc, new Point3D( 1, 3, 0 ), 1.0 ), near( 45.0 ) );
		assertThat( command.deriveExtent( arc, new Point3D( 1, 3, 0 ), -1.0 ), near( -315.0 ) );

		assertThat( command.deriveExtent( arc, new Point3D( 0, 3, 0 ), 1.0 ), near( 90.0 ) );
		assertThat( command.deriveExtent( arc, new Point3D( 0, 3, 0 ), -1.0 ), near( -270.0 ) );

		assertThat( command.deriveExtent( arc, new Point3D( 0, 2, 0 ), 1.0 ), near( 135.0 ) );
		assertThat( command.deriveExtent( arc, new Point3D( 0, 2, 0 ), -1.0 ), near( -225.0 ) );

		assertThat( command.deriveExtent( arc, new Point3D( 0, 1, 0 ), 1.0 ), near( 180.0 ) );
		assertThat( command.deriveExtent( arc, new Point3D( 0, 1, 0 ), -1.0 ), near( -180.0 ) );

		assertThat( command.deriveExtent( arc, new Point3D( 1, 1, 0 ), 1.0 ), near( 225.0 ) );
		assertThat( command.deriveExtent( arc, new Point3D( 1, 1, 0 ), -1.0 ), near( -135.0 ) );

		assertThat( command.deriveExtent( arc, new Point3D( 2, 1, 0 ), 1.0 ), near( 270.0 ) );
		assertThat( command.deriveExtent( arc, new Point3D( 2, 1, 0 ), -1.0 ), near( -90.0 ) );

		assertThat( command.deriveExtent( arc, new Point3D( 2, 2, 0 ), 1.0 ), near( 315.0 ) );
		assertThat( command.deriveExtent( arc, new Point3D( 2, 2, 0 ), -1.0 ), near( -45.0 ) );
	}

	@Test
	void testGetExtentSpin() {
		DesignArc arc = new DesignArc( new Point3D( 0, 0, 0 ), 1.0, 0.0, 0.0, DesignArc.Type.OPEN );

		// Initial
		assertThat( command.getExtentSpin( arc, new Point3D( 1, 0, 0 ), new Point3D( 1, 1, 0 ), 0.0 ), near( 1.0 ) );
		assertThat( command.getExtentSpin( arc, new Point3D( 1, 0, 0 ), new Point3D( 1, -1, 0 ), 0.0 ), near( -1.0 ) );

		// Maintain CCW
		assertThat( command.getExtentSpin( arc, new Point3D( 1, 1, 0 ), new Point3D( 1, 1.5, 0 ), 1.0 ), near( 1.0 ) );
		assertThat( command.getExtentSpin( arc, new Point3D( 1, 1, 0 ), new Point3D( 1, 0.5, 0 ), 1.0 ), near( 1.0 ) );

		// Maintain CW
		assertThat( command.getExtentSpin( arc, new Point3D( 1, -1, 0 ), new Point3D( 1, -0.5, 0 ), -1.0 ), near( -1.0 ) );
		assertThat( command.getExtentSpin( arc, new Point3D( 1, -1, 0 ), new Point3D( 1, -1.5, 0 ), -1.0 ), near( -1.0 ) );

		// Switch
		assertThat( command.getExtentSpin( arc, new Point3D( 1, 0.5, 0 ), new Point3D( 1, -0.5, 0 ), 1.0 ), near( -1.0 ) );
		assertThat( command.getExtentSpin( arc, new Point3D( 1, -0.5, 0 ), new Point3D( 1, 0.5, 0 ), -1.0 ), near( 1.0 ) );
	}

	@Test
	void testGetExtentSpinWithRotatedArc() {
		DesignArc arc = new DesignArc( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, 0.0, DesignArc.Type.OPEN );

		// NOTE These tests use the point where the crossover line goes through (4.0,5.0)

		// Initial
		assertThat( command.getExtentSpin( arc, new Point3D( 4, 5, 0 ), new Point3D( 4, 3, 0 ), 0.0 ), near( 1.0 ) );
		assertThat( command.getExtentSpin( arc, new Point3D( 4, 5, 0 ), new Point3D( 5, 5, 0 ), 0.0 ), near( -1.0 ) );

		// Maintain CCW
		assertThat( command.getExtentSpin( arc, new Point3D( 4, 4.9, 0 ), new Point3D( 4, 4.5, 0 ), 1.0 ), near( 1.0 ) );
		assertThat( command.getExtentSpin( arc, new Point3D( 4.5, 5, 0 ), new Point3D( 4.1, 5, 0 ), 1.0 ), near( 1.0 ) );

		// Maintain CW
		assertThat( command.getExtentSpin( arc, new Point3D( 4, 4.5, 0 ), new Point3D( 4, 4.9, 0 ), -1.0 ), near( -1.0 ) );
		assertThat( command.getExtentSpin( arc, new Point3D( 4.1, 5, 0 ), new Point3D( 4.5, 5, 0 ), -1.0 ), near( -1.0 ) );

		// Switch
		assertThat( command.getExtentSpin( arc, new Point3D( 4, 4.9, 0 ), new Point3D( 4, 5.1, 0 ), 1.0 ), near( -1.0 ) );
		assertThat( command.getExtentSpin( arc, new Point3D( 4, 5.1, 0 ), new Point3D( 4, 4.9, 0 ), -1.0 ), near( 1.0 ) );
		assertThat( command.getExtentSpin( arc, new Point3D( 3.9, 5, 0 ), new Point3D( 4.1, 5, 0 ), 1.0 ), near( -1.0 ) );
		assertThat( command.getExtentSpin( arc, new Point3D( 4.1, 5, 0 ), new Point3D( 3.9, 5, 0 ), 1.0 ), near( 1.0 ) );
	}

}