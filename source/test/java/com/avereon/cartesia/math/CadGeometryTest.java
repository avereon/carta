package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignArc;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.match.Near.near;
import static org.hamcrest.Matchers.*;

import static org.hamcrest.MatcherAssert.assertThat;

public class CadGeometryTest {

	// Most of the methods in CadGeometry are just forwarded to the Geometry
	// class. However, there are a few that are specific to CadGeometry. Those
	// are tested here.

	@Test
	void testArcFromThreePoints() {
		DesignArc arc;
		arc = CadGeometry.arcFromThreePoints( new Point3D( -1, 0, 0 ), new Point3D( 0, 1, 0 ), new Point3D( 1, 0, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 1.0 ) );
		assertThat( arc.getStart(), is( 180.0 ) );
		assertThat( arc.getExtent(), is( -180.0 ) );

		arc = CadGeometry.arcFromThreePoints( new Point3D( -1, 0, 0 ), new Point3D( 0, -1, 0 ), new Point3D( 1, 0, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 1.0 ) );
		assertThat( arc.getStart(), is( 180.0 ) );
		assertThat( arc.getExtent(), is( 180.0 ) );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 0, -1, 0 ), new Point3D( 1, 0, 0 ), new Point3D( 0, 1, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 1.0 ) );
		assertThat( arc.getStart(), is( -90.0 ) );
		assertThat( arc.getExtent(), is( 180.0 ) );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 0, 1, 0 ), new Point3D( 1, 0, 0 ), new Point3D( 0, -1, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 1.0 ) );
		assertThat( arc.getStart(), is( 90.0 ) );
		assertThat( arc.getExtent(), is( -180.0 ) );

		// Class A
		double a = 126.86989764584402;
		double b = a - 90;
		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, -4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 8, -4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( -a ) );
		assertThat( arc.getExtent(), is( -2 * a ) );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, -4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 2, -4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( a - 180 ) );
		assertThat( arc.getExtent(), is( 2 * a ) );

		// Class B
		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, 4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 8, 4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( a ) );
		assertThat( arc.getExtent(), is( 2 * a ) );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, 4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 2, 4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( 180 - a ) );
		assertThat( arc.getExtent(), is( -2 * a ) );

		// Class C
		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, -4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 8, -4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( -a ) );
		assertThat( arc.getExtent(), near( 2 * b ) );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, 4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 8, 4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( a ) );
		assertThat( arc.getExtent(), near( -2 * b ) );

		// Class D
		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, -4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 2, -4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( a - 180 ) );
		assertThat( arc.getExtent(), near( -2 * b ) );
		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, 4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 2, 4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( 180 - a ) );
		assertThat( arc.getExtent(), near( 2 * b ) );
	}

}
