package com.avereon.cartesia.math;

import com.avereon.cartesia.test.PointAssert;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.curve.math.Constants;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CadShapesTest {

	@Test
	void testCartesianToPolar() {
		PointAssert.assertThat( CadShapes.cartesianToPolarDegrees( new Point3D( 1, 0, 0 ) ) ).isCloseTo( new Point3D( 1, 0, 0 ) );
		PointAssert.assertThat( CadShapes.cartesianToPolarDegrees( new Point3D( 0, 1, 0 ) ) ).isCloseTo( new Point3D( 1, 90, 0 ) );
		PointAssert.assertThat( CadShapes.cartesianToPolarDegrees( new Point3D( -1, 0, 0 ) ) ).isCloseTo( new Point3D( 1, 180, 0 ) );
		PointAssert.assertThat( CadShapes.cartesianToPolarDegrees( new Point3D( 0, -1, 0 ) ) ).isCloseTo( new Point3D( 1, -90, 0 ) );
	}

	@Test
	void testPolarToCartesian() {
		PointAssert.assertThat( CadShapes.polarDegreesToCartesian( new Point3D( 1, 0, 0 ) ) ).isCloseTo( new Point3D( 1, 0, 0 ) );
		PointAssert.assertThat( CadShapes.polarDegreesToCartesian( new Point3D( 1, 90, 0 ) ) ).isCloseTo( new Point3D( 0, 1, 0 ) );
		PointAssert.assertThat( CadShapes.polarDegreesToCartesian( new Point3D( 1, 180, 0 ) ) ).isCloseTo( new Point3D( -1, 0, 0 ) );
		PointAssert.assertThat( CadShapes.polarDegreesToCartesian( new Point3D( 1, 270, 0 ) ) ).isCloseTo( new Point3D( 0, -1, 0 ) );
		PointAssert.assertThat( CadShapes.polarDegreesToCartesian( new Point3D( 1, 360, 0 ) ) ).isCloseTo( new Point3D( 1, 0, 0 ) );

		PointAssert.assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, 45, 0 ) ) ).isCloseTo( new Point3D( 1, 1, 0 ) );
		PointAssert.assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, 135, 0 ) ) ).isCloseTo( new Point3D( -1, 1, 0 ) );
		PointAssert.assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, 225, 0 ) ) ).isCloseTo( new Point3D( -1, -1, 0 ) );
		PointAssert.assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, 315, 0 ) ) ).isCloseTo( new Point3D( 1, -1, 0 ) );

		PointAssert.assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, -45, 0 ) ) ).isCloseTo( new Point3D( 1, -1, 0 ) );
		PointAssert.assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, -135, 0 ) ) ).isCloseTo( new Point3D( -1, -1, 0 ) );
		PointAssert.assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, -225, 0 ) ) ).isCloseTo( new Point3D( -1, 1, 0 ) );
		PointAssert.assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, -315, 0 ) ) ).isCloseTo( new Point3D( 1, 1, 0 ) );
	}

	@Test
	void testParsePoint() {
		assertThat( CadShapes.parsePoint( "0" ) ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( CadShapes.parsePoint( "0,0" ) ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( CadShapes.parsePoint( "0,0,0" ) ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( CadShapes.parsePoint( "1" ) ).isEqualTo( new Point3D( 1, 0, 0 ) );
		assertThat( CadShapes.parsePoint( "1,2" ) ).isEqualTo( new Point3D( 1, 2, 0 ) );
		assertThat( CadShapes.parsePoint( "1,2,3" ) ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testParseRelativeCoordinates() {
		Point3D anchor = new Point3D( -1, -2, -3 );
		assertThat( CadShapes.parsePoint( "@1", anchor ) ).isEqualTo( new Point3D( 0, -2, -3 ) );
		assertThat( CadShapes.parsePoint( "@1,2", anchor ) ).isEqualTo( new Point3D( 0, 0, -3 ) );
		assertThat( CadShapes.parsePoint( "@1,2,3", anchor ) ).isEqualTo( new Point3D( 0, 0, 0 ) );

		// Test missing anchor point
		assertThat( CadShapes.parsePoint( "@1", null ) ).isNull();
	}

	@Test
	void testParsePolarCoordinatesWithBothIdentifiers() {
		assertThat( CadShapes.parsePoint( "1,<0" ) ).isEqualTo( new Point3D( 1, 0, 0 ) );
		assertThat( CadShapes.parsePoint( "<0,1" ) ).isEqualTo( new Point3D( 1, 0, 0 ) );
	}

	@Test
	void testParsePolarCoordinates() {
		PointAssert.assertThat( CadShapes.parsePoint( "<0,1" ) ).isCloseTo( new Point3D( 1, 0, 0 ) );
		PointAssert.assertThat( CadShapes.parsePoint( "<90,1" ) ).isCloseTo( new Point3D( 0, 1, 0 ) );
		PointAssert.assertThat( CadShapes.parsePoint( "<180,1" ) ).isCloseTo( new Point3D( -1, 0, 0 ) );
		PointAssert.assertThat( CadShapes.parsePoint( "<3*180/2,1" ) ).isCloseTo( new Point3D( 0, -1, 0 ) );

		PointAssert.assertThat( CadShapes.parsePoint( "<90,1" ) ).isCloseTo( new Point3D( 0, 1, 0 ) );
		PointAssert.assertThat( CadShapes.parsePoint( "<180,1" ) ).isCloseTo( new Point3D( -1, 0, 0 ) );
		PointAssert.assertThat( CadShapes.parsePoint( "<270,1" ) ).isCloseTo( new Point3D( 0, -1, 0 ) );

		PointAssert.assertThat( CadShapes.parsePoint( "<45,1" ) ).isCloseTo( new Point3D( java.lang.Math.cos( Constants.PI_OVER_4 ), java.lang.Math.sin( Constants.PI_OVER_4 ), 0 ) );
	}

	@Test
	void testParsePolarCoordinatesAngleSeparator() {
		PointAssert.assertThat( CadShapes.parsePoint( "1<0" ) ).isCloseTo( new Point3D( 1, 0, 0 ) );
	}

	@Test
	void testParsePolarCoordinatesReverseAngleSeparator() {
		PointAssert.assertThat( CadShapes.parsePoint( "<0,1" ) ).isCloseTo( new Point3D( 1, 0, 0 ) );
	}

	@Test
	void testParseRelativePolarCoordinates() {
		Point3D anchor = new Point3D( 1, 1, 0 );
		PointAssert
			.assertThat( CadShapes.parsePoint( "@1<180+45", anchor ) )
			.isCloseTo( new Point3D( 1 - java.lang.Math.cos( Constants.PI_OVER_4 ), 1 - java.lang.Math.sin( Constants.PI_OVER_4 ), 0 ) );
		PointAssert
			.assertThat( CadShapes.parsePoint( "@1,<180+45", anchor ) )
			.isCloseTo( new Point3D( 1 - java.lang.Math.cos( Constants.PI_OVER_4 ), 1 - java.lang.Math.sin( Constants.PI_OVER_4 ), 0 ) );
		PointAssert
			.assertThat( CadShapes.parsePoint( "@<180+45,1", anchor ) )
			.isCloseTo( new Point3D( 1 - java.lang.Math.cos( Constants.PI_OVER_4 ), 1 - java.lang.Math.sin( Constants.PI_OVER_4 ), 0 ) );

		// Test missing anchor point
		assertThat( CadShapes.parsePoint( "@1,<180+45", null ) ).isNull();
	}

	@Test
	void testParsePointWithWhitespace() {
		Point3D anchor = new Point3D( 1, 1, 0 );
		assertThat( CadShapes.parsePoint( " 1 , 2 " ) ).isEqualTo( new Point3D( 1, 2, 0 ) );
		PointAssert.assertThat( CadShapes.parsePoint( " pi , 1 " ) ).isCloseTo( new Point3D( Math.PI, 1, 0 ) );
		PointAssert.assertThat( CadShapes.parsePoint( " <180 , 1 " ) ).isCloseTo( new Point3D( -1, 0, 0 ) );
		assertThat( CadShapes.parsePoint( " @ 1 , 2 ", anchor ) ).isEqualTo( new Point3D( 2, 3, 0 ) );
		PointAssert
			.assertThat( CadShapes.parsePoint( " @ 1 , < 180 + 45 ", anchor ) )
			.isCloseTo( new Point3D( 1 - java.lang.Math.cos( Constants.PI_OVER_4 ), 1 - java.lang.Math.sin( Constants.PI_OVER_4 ), 0 ) );
	}

	@Test
	void testParseDashPattern() {
		assertThat( CadShapes.parseDashPattern( "" ) ).isEqualTo( List.of() );
		assertThat( CadShapes.parseDashPattern( "0" ) ).isEqualTo( List.of() );
		assertThat( CadShapes.parseDashPattern( "0, 0" ) ).isEqualTo( List.of() );
		assertThat( CadShapes.parseDashPattern( "1, 0" ) ).isEqualTo( List.of( 1.0, 0.0 ) );
	}

	@Test
	void testFindNearestShapeToPointWithLines() {
		DesignLine a = new DesignLine();
		a.setOrigin( new Point3D( 0, 0, 0 ) );
		a.setPoint( new Point3D( 1, 0, 0 ) );

		DesignLine b = new DesignLine();
		b.setOrigin( new Point3D( 0, 1, 0 ) );
		b.setPoint( new Point3D( 1, 1, 0 ) );

		assertThat( CadShapes.findNearestShapeToPoint( Set.of( a, b ), new Point3D( 0, -0.25, 0 ) ) ).isEqualTo( a );
		assertThat( CadShapes.findNearestShapeToPoint( Set.of( a, b ), new Point3D( 0, 0, 0 ) ) ).isEqualTo( a );
		assertThat( CadShapes.findNearestShapeToPoint( Set.of( a, b ), new Point3D( 0, 0.25, 0 ) ) ).isEqualTo( a );
		assertThat( List.of( CadShapes.findNearestShapeToPoint( Set.of( a, b ), new Point3D( 0, 0.5, 0 ) ) ) ).isSubsetOf( a, b );
		assertThat( CadShapes.findNearestShapeToPoint( Set.of( a, b ), new Point3D( 0, 0.75, 0 ) ) ).isEqualTo( b );
		assertThat( CadShapes.findNearestShapeToPoint( Set.of( a, b ), new Point3D( 0, 1.0, 0 ) ) ).isEqualTo( b );
		assertThat( CadShapes.findNearestShapeToPoint( Set.of( a, b ), new Point3D( 0, 1.25, 0 ) ) ).isEqualTo( b );
	}

}
