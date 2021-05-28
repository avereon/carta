package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.curve.math.Constants;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.avereon.cartesia.match.Near.near;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CadShapesTest {

	@Test
	void testCartesianToPolar() {
		assertThat( CadShapes.cartesianToPolarDegrees( new Point3D( 1, 0, 0 ) ), near( new Point3D( 1, 0, 0 ) ) );
		assertThat( CadShapes.cartesianToPolarDegrees( new Point3D( 0, 1, 0 ) ), near( new Point3D( 1, 90, 0 ) ) );
		assertThat( CadShapes.cartesianToPolarDegrees( new Point3D( -1, 0, 0 ) ), near( new Point3D( 1, 180, 0 ) ) );
		assertThat( CadShapes.cartesianToPolarDegrees( new Point3D( 0, -1, 0 ) ), near( new Point3D( 1, -90, 0 ) ) );
	}

	@Test
	void testPolarToCartesian() {
		assertThat( CadShapes.polarDegreesToCartesian( new Point3D( 1, 0, 0 ) ), near( new Point3D( 1, 0, 0 ) ) );
		assertThat( CadShapes.polarDegreesToCartesian( new Point3D( 1, 90, 0 ) ), near( new Point3D( 0, 1, 0 ) ) );
		assertThat( CadShapes.polarDegreesToCartesian( new Point3D( 1, 180, 0 ) ), near( new Point3D( -1, 0, 0 ) ) );
		assertThat( CadShapes.polarDegreesToCartesian( new Point3D( 1, 270, 0 ) ), near( new Point3D( 0, -1, 0 ) ) );
		assertThat( CadShapes.polarDegreesToCartesian( new Point3D( 1, 360, 0 ) ), near( new Point3D( 1, 0, 0 ) ) );

		assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, 45, 0 ) ), near( new Point3D( 1, 1, 0 ) ) );
		assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, 135, 0 ) ), near( new Point3D( -1, 1, 0 ) ) );
		assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, 225, 0 ) ), near( new Point3D( -1, -1, 0 ) ) );
		assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, 315, 0 ) ), near( new Point3D( 1, -1, 0 ) ) );

		assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, -45, 0 ) ), near( new Point3D( 1, -1, 0 ) ) );
		assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, -135, 0 ) ), near( new Point3D( -1, -1, 0 ) ) );
		assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, -225, 0 ) ), near( new Point3D( -1, 1, 0 ) ) );
		assertThat( CadShapes.polarDegreesToCartesian( new Point3D( Constants.SQRT_TWO, -315, 0 ) ), near( new Point3D( 1, 1, 0 ) ) );
	}

	@Test
	void testParsePoint() {
		assertThat( CadShapes.parsePoint( "0" ), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( CadShapes.parsePoint( "0,0" ), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( CadShapes.parsePoint( "0,0,0" ), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( CadShapes.parsePoint( "1" ), is( new Point3D( 1, 0, 0 ) ) );
		assertThat( CadShapes.parsePoint( "1,2" ), is( new Point3D( 1, 2, 0 ) ) );
		assertThat( CadShapes.parsePoint( "1,2,3" ), is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void testParseRelativeCoordinates() {
		Point3D anchor = new Point3D( -1, -2, -3 );
		assertThat( CadShapes.parsePoint( "@1", anchor ), is( new Point3D( 0, -2, -3 ) ) );
		assertThat( CadShapes.parsePoint( "@1,2", anchor ), is( new Point3D( 0, 0, -3 ) ) );
		assertThat( CadShapes.parsePoint( "@1,2,3", anchor ), is( new Point3D( 0, 0, 0 ) ) );

		// Test missing anchor point
		assertThat( CadShapes.parsePoint( "@1", null ), is( nullValue() ) );
	}

	@Test
	void testParsePolarCoordinatesWithBothIdentifiers() {
		assertThat( CadShapes.parsePoint( "<1,0" ), is( new Point3D( 1, 0, 0 ) ) );
		assertThat( CadShapes.parsePoint( ">0,1" ), is( new Point3D( 1, 0, 0 ) ) );
	}

	@Test
	void testParsePolarCoordinates() {
		assertThat( CadShapes.parsePoint( ">0,1" ), near( new Point3D( 1, 0, 0 ) ) );
		assertThat( CadShapes.parsePoint( ">pi/2,1" ), near( new Point3D( 0, 1, 0 ) ) );
		assertThat( CadShapes.parsePoint( ">pi,1" ), near( new Point3D( -1, 0, 0 ) ) );
		assertThat( CadShapes.parsePoint( ">3*pi/2,1" ), near( new Point3D( 0, -1, 0 ) ) );

		assertThat( CadShapes.parsePoint( ">rad(90),1" ), near( new Point3D( 0, 1, 0 ) ) );
		assertThat( CadShapes.parsePoint( ">rad(180),1" ), near( new Point3D( -1, 0, 0 ) ) );
		assertThat( CadShapes.parsePoint( ">rad(270),1" ), near( new Point3D( 0, -1, 0 ) ) );

		assertThat( CadShapes.parsePoint( ">rad(45),1" ), near( new Point3D( java.lang.Math.cos( Constants.PI_OVER_4 ), java.lang.Math.sin( Constants.PI_OVER_4 ), 0 ) ) );
	}

	@Test
	void testParseRelativePolarCoordinates() {
		Point3D anchor = new Point3D( 1, 1, 0 );
		assertThat( CadShapes.parsePoint( "@<1,rad(180+45)", anchor ), near( new Point3D( 1 - java.lang.Math.cos( Constants.PI_OVER_4 ), 1 - java.lang.Math.sin( Constants.PI_OVER_4 ), 0 ) ) );
		assertThat( CadShapes.parsePoint( "@>rad(180+45),1", anchor ), near( new Point3D( 1 - java.lang.Math.cos( Constants.PI_OVER_4 ), 1 - java.lang.Math.sin( Constants.PI_OVER_4 ), 0 ) ) );

		// Test missing anchor point
		assertThat( CadShapes.parsePoint( "@<1,rad(180+45)", null ), is( nullValue() ) );
	}

	@Test
	void testParsePointWithWhitespace() {
		Point3D anchor = new Point3D( 1, 1, 0 );
		assertThat( CadShapes.parsePoint( " 1 , 2 " ), is( new Point3D( 1, 2, 0 ) ) );
		assertThat( CadShapes.parsePoint( " > pi , 1 " ), near( new Point3D( -1, 0, 0 ) ) );
		assertThat( CadShapes.parsePoint( " @ 1 , 2 ", anchor ), is( new Point3D( 2, 3, 0 ) ) );
		assertThat( CadShapes.parsePoint( " @ < 1 , rad ( 180 + 45 ) ", anchor ), near( new Point3D( 1 - java.lang.Math.cos( Constants.PI_OVER_4 ), 1 - java.lang.Math.sin( Constants.PI_OVER_4 ), 0 ) ) );
	}

	@Test
	void testParseDashPattern() {
		assertThat( CadShapes.parseDashPattern( "" ), is( List.of() ) );
		assertThat( CadShapes.parseDashPattern( "0" ), is( List.of() ) );
		assertThat( CadShapes.parseDashPattern( "0, 0" ), is( List.of() ) );
		assertThat( CadShapes.parseDashPattern( "1, 0" ), is( List.of( 1.0, 0.0 ) ) );
	}

	@Test
	void testFindNearestShapeToPointWithLines() {
		DesignLine a = new DesignLine();
		a.setOrigin( new Point3D( 0, 0, 0 ) );
		a.setPoint( new Point3D( 1, 0, 0 ) );

		DesignLine b = new DesignLine();
		b.setOrigin( new Point3D( 0, 1, 0 ) );
		b.setPoint( new Point3D( 1, 1, 0 ) );

		assertThat( CadShapes.findNearestShapeToPoint( Set.of( a, b ), new Point3D( 0, -0.25, 0 ) ), is( a ) );
		assertThat( CadShapes.findNearestShapeToPoint( Set.of( a, b ), new Point3D( 0, 0, 0 ) ), is( a ) );
		assertThat( CadShapes.findNearestShapeToPoint( Set.of( a, b ), new Point3D( 0, 0.25, 0 ) ), is( a ) );
		assertThat( CadShapes.findNearestShapeToPoint( Set.of( a, b ), new Point3D( 0, 0.5, 0 ) ), oneOf( a, b ) );
		assertThat( CadShapes.findNearestShapeToPoint( Set.of( a, b ), new Point3D( 0, 0.75, 0 ) ), is( b ) );
		assertThat( CadShapes.findNearestShapeToPoint( Set.of( a, b ), new Point3D( 0, 1.0, 0 ) ), is( b ) );
		assertThat( CadShapes.findNearestShapeToPoint( Set.of( a, b ), new Point3D( 0, 1.25, 0 ) ), is( b ) );
	}

}
