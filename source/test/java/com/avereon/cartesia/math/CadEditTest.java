package com.avereon.cartesia.math;

import com.avereon.cartesia.BaseCartesiaTest;
import com.avereon.cartesia.data.DesignArc;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CadEditTest extends BaseCartesiaTest {

	private static final double ALPHA = Math.toDegrees( Math.atan2( 3, 4 ) );

	@Test
	void testUpdateArcCW() {
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 0 ), 5.0, 180 - ALPHA, -180 + ALPHA, DesignArc.Type.OPEN );
		CadEdit.updateArc( arc, new Point3D( 5, 5, 0 ), new Point3D( 6, 2, 0 ) );

		assertThat( arc.getOrigin(), is( new Point3D( 1, 2, 0 ) ) );
		assertThat( arc.calcRotate(), is( 0.0 ) );
		assertThat( arc.getStart(), is( 180 - ALPHA ) );
		assertThat( arc.getExtent(), is( -180 + 2 * ALPHA ) );
	}

	@Test
	void testUpdateArcCCW() {
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 0 ), 5.0, ALPHA, 180 - ALPHA, DesignArc.Type.OPEN );
		CadEdit.updateArc( arc, new Point3D( -3, 5, 0 ), new Point3D( -4, 2, 0 ) );

		assertThat( arc.getOrigin(), is( new Point3D( 1, 2, 0 ) ) );
		assertThat( arc.calcRotate(), is( 0.0 ) );
		assertThat( arc.getStart(), is( ALPHA ) );
		assertThat( arc.getExtent(), is( 180 - 2 * ALPHA ) );
	}

	@Test
	void testUpdateBacksideArcCW() {
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 0 ), 5.0, -90 - ALPHA, -180 + ALPHA, DesignArc.Type.OPEN );
		CadEdit.updateArc( arc, new Point3D( -2, 6, 0 ), new Point3D( 1, -3, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 1, 2, 0 ) ) );
		assertThat( arc.calcRotate(), is( 0.0 ) );
		assertThat( arc.getStart(), is( -90 - ALPHA ) );
		assertThat( arc.getExtent(), is( -180 + 2 * ALPHA ) );
	}

	@Test
	void testUpdateBacksideArcCCW() {
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 0 ), 5.0, 90 + ALPHA, 180 - ALPHA, DesignArc.Type.OPEN );
		CadEdit.updateArc( arc, new Point3D( -2, -2, 0 ), new Point3D( 1, -3, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 1, 2, 0 ) ) );
		assertThat( arc.calcRotate(), is( 0.0 ) );
		assertThat( arc.getStart(), is( 90 + ALPHA ) );
		assertThat( arc.getExtent(), is( 180 - 2 * ALPHA ) );
	}

}
