package com.avereon.cartesia.math;

import com.avereon.cartesia.PointAssert;
import com.avereon.cartesia.data.DesignArc;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class CadGeometryTest {

	// Most of the methods in CadGeometry are just forwarded to the Geometry
	// class. However, there are a few that are specific to CadGeometry. Those
	// are tested here.

	@Test
	void testArcFromThreePoints() {
		DesignArc arc;
		arc = CadGeometry.arcFromThreePoints( new Point3D( -1, 0, 0 ), new Point3D( 0, 1, 0 ), new Point3D( 1, 0, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 1.0 );
		assertThat( arc.getStart() ).isEqualTo( 180.0 );
		assertThat( arc.getExtent() ).isEqualTo( -180.0 );

		arc = CadGeometry.arcFromThreePoints( new Point3D( -1, 0, 0 ), new Point3D( 0, -1, 0 ), new Point3D( 1, 0, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 1.0 );
		assertThat( arc.getStart() ).isEqualTo( 180.0 );
		assertThat( arc.getExtent() ).isEqualTo( 180.0 );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 0, -1, 0 ), new Point3D( 1, 0, 0 ), new Point3D( 0, 1, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 1.0 );
		assertThat( arc.getStart() ).isEqualTo( -90.0 );
		assertThat( arc.getExtent() ).isEqualTo( 180.0 );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 0, 1, 0 ), new Point3D( 1, 0, 0 ), new Point3D( 0, -1, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 1.0 );
		assertThat( arc.getStart() ).isEqualTo( 90.0 );
		assertThat( arc.getExtent() ).isEqualTo( -180.0 );
	}

	@Test
	void testArcFromThreePointsScenarioB() {
		DesignArc arc;

		// Class A
		double a = 126.86989764584402;
		double b = 540 - 2 * a;
		double c = 2 * a - 180;
		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, -4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 8, -4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( -a );
		assertThat( arc.getExtent() ).isEqualTo( -b );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, -4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 2, -4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( a - 180 );
		assertThat( arc.getExtent() ).isEqualTo( b );

		// Class B
		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, 4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 8, 4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( a );
		assertThat( arc.getExtent() ).isEqualTo( b );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, 4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 2, 4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( 180 - a );
		assertThat( arc.getExtent() ).isEqualTo( -b );

		// Class C
		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, -4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 8, -4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( -a );
		assertThat( arc.getExtent() ).isCloseTo( c, TOLERANCE );

		arc = CadGeometry.arcFromThreePoints( new Point3D( 2, 4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 8, 4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( a );
		assertThat( arc.getExtent() ).isCloseTo( -c, TOLERANCE );

		// Class D
		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, -4, 0 ), new Point3D( 5, -5, 0 ), new Point3D( 2, -4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( a - 180 );
		assertThat( arc.getExtent() ).isCloseTo( -c, TOLERANCE );
		arc = CadGeometry.arcFromThreePoints( new Point3D( 8, 4, 0 ), new Point3D( 5, 5, 0 ), new Point3D( 2, 4, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 5, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 5.0 );
		assertThat( arc.getStart() ).isEqualTo( 180 - a );
		assertThat( arc.getExtent() ).isCloseTo( c, TOLERANCE );
	}

	@Test
	void testArcFromThreePointsScenarioC() {
		DesignArc arc = CadGeometry.arcFromThreePoints( new Point3D( 1, 1, 0 ), new Point3D( 2, 2 + Math.sqrt( 2 ), 0 ), new Point3D( 3, 1, 0 ) );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( 2, 2, 0 ) );
		assertThat( arc.getRadius() ).isCloseTo( Math.sqrt( 2 ), TOLERANCE );
		assertThat( arc.getStart() ).isCloseTo( -135, TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( -270, TOLERANCE );

		arc = CadGeometry.arcFromThreePoints( new Point3D( -3, 1, 0 ), new Point3D( -2, 2 + Math.sqrt( 2 ), 0 ), new Point3D( -1, 1, 0 ) );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( -2, 2, 0 ) );
		assertThat( arc.getRadius() ).isCloseTo( Math.sqrt( 2 ), TOLERANCE );
		assertThat( arc.getStart() ).isCloseTo( -135, TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( -270, TOLERANCE );
	}

	@Test
	void testArcFromThreePointsScenarioD() {
		double start = CadGeometry.cartesianToPolar360( new Point3D( -1, -0.75, 0 ) ).getY();
		double extent = -(360 + 2 * (start + 90));
		DesignArc arc = CadGeometry.arcFromThreePoints( new Point3D( 1, 1, 0 ), new Point3D( 2, 3, 0 ), new Point3D( 3, 1, 0 ) );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( 2, 1.75, 0 ) );
		assertThat( arc.getRadius() ).isCloseTo( 1.25, TOLERANCE );
		assertThat( arc.getStart() ).isCloseTo( CadGeometry.cartesianToPolar360( new Point3D( -1, -0.75, 0 ) ).getY(), TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( extent, TOLERANCE );
	}

	@Test
	void testArcFromThreePointsWithSameMidAndEndPoints() {
		DesignArc arc;
		arc = CadGeometry.arcFromThreePoints( new Point3D( -1, 0, 0 ), new Point3D( 0, 1, 0 ), new Point3D( 1, 0, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 1.0 );
		assertThat( arc.getStart() ).isEqualTo( 180.0 );
		assertThat( arc.getExtent() ).isEqualTo( -180.0 );
	}

	@Test
	void testNormalizeAngle180() {
		assertThat( CadGeometry.normalizeAngle180( 0 ) ).isEqualTo( 0.0 );
		assertThat( CadGeometry.normalizeAngle180( 180 ) ).isEqualTo( 180.0 );
		assertThat( CadGeometry.normalizeAngle180( -180 ) ).isEqualTo( -180.0 );
		assertThat( CadGeometry.normalizeAngle180( 315 ) ).isEqualTo( -45.0 );
		assertThat( CadGeometry.normalizeAngle180( -315 ) ).isEqualTo( 45.0 );
		assertThat( CadGeometry.normalizeAngle180( 675 ) ).isEqualTo( -45.0 );
		assertThat( CadGeometry.normalizeAngle180( -675 ) ).isEqualTo( 45.0 );
	}

	@Test
	void testNormalizeAngle360() {
		assertThat( CadGeometry.normalizeAngle360( 0 ) ).isEqualTo( 0.0 );
		assertThat( CadGeometry.normalizeAngle360( 180 ) ).isEqualTo( 180.0 );
		assertThat( CadGeometry.normalizeAngle360( -180 ) ).isEqualTo( -180.0 );
		assertThat( CadGeometry.normalizeAngle360( 315 ) ).isEqualTo( 315.0 );
		assertThat( CadGeometry.normalizeAngle360( -315 ) ).isEqualTo( -315.0 );
		assertThat( CadGeometry.normalizeAngle360( 675 ) ).isEqualTo( 315.0 );
		assertThat( CadGeometry.normalizeAngle360( -675 ) ).isEqualTo( -315.0 );
	}

}
