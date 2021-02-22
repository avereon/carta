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

	}

	@Test
	void testArcFromThreePointsScenarioB() {
		DesignArc arc;

		// Class A
		double a = 126.86989764584402;
		double b = 540 - 2 * a;
		double c = 2 * a - 180;
		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, -4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 8, -4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( -a ) );
		assertThat( arc.getExtent(), is( -b ) );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, -4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 2, -4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( a - 180 ) );
		assertThat( arc.getExtent(), is( b ) );

		// Class B
		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, 4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 8, 4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( a ) );
		assertThat( arc.getExtent(), is( b ) );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, 4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 2, 4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( 180 - a ) );
		assertThat( arc.getExtent(), is( -b ) );

		// Class C
		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, -4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 8, -4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( -a ) );
		assertThat( arc.getExtent(), near( c ) );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, 4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 8, 4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( a ) );
		assertThat( arc.getExtent(), near( -c ) );

		// Class D
		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, -4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 2, -4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( a - 180 ) );
		assertThat( arc.getExtent(), near( -c ) );
		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, 4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 2, 4, 0 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 5, 0, 0 ) ) );
		assertThat( arc.getRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( 180 - a ) );
		assertThat( arc.getExtent(), near( c ) );

	}

	@Test
	void testArcFromThreePointsScenarioC() {
		DesignArc arc = CadGeometry.arcFromThreePoints( new Point3D( 1, 1, 0 ), new Point3D( 2, 2 + Math.sqrt( 2 ), 0 ), new Point3D( 3, 1, 0 ) );
		assertThat( arc.getOrigin(), near( new Point3D( 2, 2, 0 ) ) );
		assertThat( arc.getRadius(), near( Math.sqrt( 2 ) ) );
		assertThat( arc.getStart(), near( -135 ) );
		assertThat( arc.getExtent(), near( -270 ) );

		arc = CadGeometry.arcFromThreePoints( new Point3D( -3, 1, 0 ), new Point3D( -2, 2 + Math.sqrt( 2 ), 0 ), new Point3D( -1, 1, 0 ) );
		assertThat( arc.getOrigin(), near( new Point3D( -2, 2, 0 ) ) );
		assertThat( arc.getRadius(), near( Math.sqrt( 2 ) ) );
		assertThat( arc.getStart(), near( -135 ) );
		assertThat( arc.getExtent(), near( -270 ) );
	}

	@Test
	void testArcFromThreePointsScenarioD() {
		double start = CadGeometry.cartesianToPolar360( new Point3D( -1, -0.75, 0 ) ).getY();
		double extent = -(360 + 2 * (start + 90));
		System.out.println( "expect=" + extent );
		DesignArc arc = CadGeometry.arcFromThreePoints( new Point3D( 1, 1, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 3, 1, 0 ) );
		assertThat( arc.getOrigin(), near( new Point3D( 2, 1.75, 0 ) ) );
		assertThat( arc.getRadius(), near( 1.25 ) );
		assertThat( arc.getStart(), near( CadGeometry.cartesianToPolar360( new Point3D( -1, -0.75, 0 ) ).getY() ) );
		assertThat( arc.getExtent(), near( extent ) );
	}

}
