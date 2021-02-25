package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignArc;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DrawCommandTest {

	@Test
	void testGetExtentSpin() {
		DesignArc arc = new DesignArc( new Point3D( 0, 0, 0 ), 1.0, 0.0, 0.0, DesignArc.Type.OPEN );

		// Initial
		assertThat( DrawCommand.getExtentSpin( arc, new Point3D( 1, 0, 0 ), new Point3D( 1, 1, 0 ), 0.0 ), is( 1.0 ) );
		assertThat( DrawCommand.getExtentSpin( arc, new Point3D( 1, 0, 0 ), new Point3D( 1, -1, 0 ), 0.0 ), is( -1.0 ) );

		// Maintain CCW
		assertThat( DrawCommand.getExtentSpin( arc, new Point3D( 1, 0, 0 ), new Point3D( 1, 1.5, 0 ), 1.0 ), is( 1.0 ) );
		assertThat( DrawCommand.getExtentSpin( arc, new Point3D( 1, 0, 0 ), new Point3D( 1, 0.5, 0 ), 1.0 ), is( 1.0 ) );

		// Maintain CW
		assertThat( DrawCommand.getExtentSpin( arc, new Point3D( 1, 0, 0 ), new Point3D( 1, -0.5, 0 ), -1.0 ), is( -1.0 ) );
		assertThat( DrawCommand.getExtentSpin( arc, new Point3D( 1, 0, 0 ), new Point3D( 1, -1.5, 0 ), -1.0 ), is( -1.0 ) );

		// Swtich
		assertThat( DrawCommand.getExtentSpin( arc, new Point3D( 1, 0.5, 0 ), new Point3D( 1, -0.5, 0 ), 1.0 ), is( -1.0 ) );
		assertThat( DrawCommand.getExtentSpin( arc, new Point3D( 1, -0.5, 0 ), new Point3D( 1, 0.5, 0 ), -1.0 ), is( 1.0 ) );
	}

}
