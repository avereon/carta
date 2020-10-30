package com.avereon.cartesia.math;

import com.avereon.cartesia.match.Near;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class GeometryTest {

	@Test
	void testParsePoint() {
		assertThat( Geometry.parsePoint( "0" ), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( Geometry.parsePoint( "0,0" ), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( Geometry.parsePoint( "0,0,0" ), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( Geometry.parsePoint( "1" ), is( new Point3D( 1, 0, 0 ) ) );
		assertThat( Geometry.parsePoint( "1,2" ), is( new Point3D( 1, 2, 0 ) ) );
		assertThat( Geometry.parsePoint( "1,2,3" ), is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void testParseRelativeCoordinates() {
		Point3D anchor = new Point3D( -1, -2, -3 );
		assertThat( Geometry.parsePoint( "@1", anchor ), is( new Point3D( 0, -2, -3 ) ) );
		assertThat( Geometry.parsePoint( "@1,2", anchor ), is( new Point3D( 0, 0, -3 ) ) );
		assertThat( Geometry.parsePoint( "@1,2,3", anchor ), is( new Point3D( 0, 0, 0 ) ) );

		// Test missing anchor point
		assertThat( Geometry.parsePoint( "@1", null ), is( nullValue() ) );
	}

	@Test
	void testParsePolarCoordinatesWithBothIdentifiers() {
		assertThat( Geometry.parsePoint( "<0,1" ), is( new Point3D( 1, 0, 0 ) ) );
		assertThat( Geometry.parsePoint( ">0,1" ), is( new Point3D( 1, 0, 0 ) ) );
	}

	@Test
	void testParsePolarCoordinates() {
		assertThat( Geometry.parsePoint( ">0,1" ), Near.near( new Point3D( 1, 0, 0 ) ) );
		assertThat( Geometry.parsePoint( ">pi/2,1" ), Near.near( new Point3D( 0, 1, 0 ) ) );
		assertThat( Geometry.parsePoint( ">pi,1" ), Near.near( new Point3D( -1, 0, 0 ) ) );
		assertThat( Geometry.parsePoint( ">3*pi/2,1" ), Near.near( new Point3D( 0, -1, 0 ) ) );

		assertThat( Geometry.parsePoint( ">rad(90),1" ), Near.near( new Point3D( 0, 1, 0 ) ) );
		assertThat( Geometry.parsePoint( ">rad(180),1" ), Near.near( new Point3D( -1, 0, 0 ) ) );
		assertThat( Geometry.parsePoint( ">rad(270),1" ), Near.near( new Point3D( 0, -1, 0 ) ) );

		assertThat( Geometry.parsePoint( ">rad(45),1" ), Near.near( new Point3D( java.lang.Math.cos( Constants.PI_OVER_4 ), java.lang.Math.sin( Constants.PI_OVER_4 ), 0 ) ) );
	}

	@Test
	void testParseRelativePolarCoordinates() {
		Point3D anchor = new Point3D( 1, 1, 0 );
		assertThat( Geometry.parsePoint( "@<rad(180+45),1", anchor ), Near.near( new Point3D( 1 - java.lang.Math.cos( Constants.PI_OVER_4 ), 1 - java.lang.Math.sin( Constants.PI_OVER_4 ), 0 ) ) );
		assertThat( Geometry.parsePoint( "@>rad(180+45),1", anchor ), Near.near( new Point3D( 1 - java.lang.Math.cos( Constants.PI_OVER_4 ), 1 - java.lang.Math.sin( Constants.PI_OVER_4 ), 0 ) ) );

		// Test missing anchor point
		assertThat( Geometry.parsePoint( "@<rad(180+45),1", null ), is( nullValue() ) );
	}

	@Test
	void testParsePointWithWhitespace() {
		Point3D anchor = new Point3D( 1, 1, 0 );
		assertThat( Geometry.parsePoint( " 1 , 2 " ), is( new Point3D( 1, 2, 0 ) ) );
		assertThat( Geometry.parsePoint( " > pi , 1 " ), Near.near( new Point3D( -1, 0, 0 ) ) );
		assertThat( Geometry.parsePoint( " @ 1 , 2 ", anchor ), is( new Point3D( 2, 3, 0 ) ) );
		assertThat( Geometry.parsePoint( " @ < rad ( 180 + 45 ) , 1 ", anchor ),
			Near.near( new Point3D( 1 - java.lang.Math.cos( Constants.PI_OVER_4 ), 1 - java.lang.Math.sin( Constants.PI_OVER_4 ), 0 ) )
		);
	}


}
