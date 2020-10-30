package com.avereon.cartesia.tool;

import com.avereon.cartesia.NumericTest;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static com.avereon.zarra.test.PointCloseTo.closeTo;


public class CoordinateSystemOrthographicTest implements NumericTest {

	@Test
	public void testFindNearest() {
		Workplane workplane = new Workplane();
		assertThat( CoordinateSystem.ORTHOGRAPHIC.getNearest( workplane, Point3D.ZERO ), is( Point3D.ZERO ) );

		workplane.setOrigin( new Point3D( 0.3, 0.2, 0 ) );
		assertThat( CoordinateSystem.ORTHOGRAPHIC.getNearest( workplane, Point3D.ZERO ), is( new Point3D( 0.3, 0.2, 0 ) ) );

		workplane.setOrigin( new Point3D( 0.7, 0.8, 0 ) );
		assertThat( CoordinateSystem.ORTHOGRAPHIC.getNearest( workplane, Point3D.ZERO ), closeTo( new Point3D( -0.3, -0.2, 0 ), TOLERANCE ) );
	}

}
