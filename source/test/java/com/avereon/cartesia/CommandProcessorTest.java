package com.avereon.cartesia;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.avereon.zarra.test.PointCloseTo.closeTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CommandProcessorTest implements NumericTest {

	private static final double PI_OVER_4 = 0.25 * Math.PI;

	private CommandProcessor processor;

	@BeforeEach
	void setup() {
		processor = new CommandProcessor();
	}

	@Test
	void testFullCommand() {
		String command = "ll 0,0 1,1";
		// Executing this should give a line from 0,0 to 1,1
	}

	@Test
	void testParseValue() {
		assertThat( processor.parseValue( "1/8" ), is( 0.125 ) );
		assertThat( processor.parseValue( "sin(pi)" ), is( Math.sin( Math.PI ) ) );
	}

	@Test
	void testParsePoint() {
		assertThat( processor.parsePoint( "0" ), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( processor.parsePoint( "0,0" ), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( processor.parsePoint( "0,0,0" ), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( processor.parsePoint( "1" ), is( new Point3D( 1, 0, 0 ) ) );
		assertThat( processor.parsePoint( "1,2" ), is( new Point3D( 1, 2, 0 ) ) );
		assertThat( processor.parsePoint( "1,2,3" ), is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void parseRelativeCoordinates() {
		processor.setAnchor( new Point3D( -1, -2, -3 ) );
		assertThat( processor.parsePoint( "@1" ), is( new Point3D( 0, -2, -3 ) ) );
		assertThat( processor.parsePoint( "@1,2" ), is( new Point3D( 0, 0, -3 ) ) );
		assertThat( processor.parsePoint( "@1,2,3" ), is( new Point3D( 0, 0, 0 ) ) );
	}

	@Test
	void testParsePolarCoordinatesWithBothIdentifiers() {
		assertThat( processor.parsePoint( "<0,1" ), is( new Point3D( 1, 0, 0 ) ) );
		assertThat( processor.parsePoint( ">0,1" ), is( new Point3D( 1, 0, 0 ) ) );
	}

	@Test
	void testParsePolarCoordinates() {
		assertThat( processor.parsePoint( ">0,1" ), closeTo( new Point3D( 1, 0, 0 ), TOLERANCE ) );
		assertThat( processor.parsePoint( ">pi/2,1" ), closeTo( new Point3D( 0, 1, 0 ), TOLERANCE ) );
		assertThat( processor.parsePoint( ">pi,1" ), closeTo( new Point3D( -1, 0, 0 ), TOLERANCE ) );
		assertThat( processor.parsePoint( ">3*pi/2,1" ), closeTo( new Point3D( 0, -1, 0 ), TOLERANCE ) );

		assertThat( processor.parsePoint( ">rad(90),1" ), closeTo( new Point3D( 0, 1, 0 ), TOLERANCE ) );
		assertThat( processor.parsePoint( ">rad(180),1" ), closeTo( new Point3D( -1, 0, 0 ), TOLERANCE ) );
		assertThat( processor.parsePoint( ">rad(270),1" ), closeTo( new Point3D( 0, -1, 0 ), TOLERANCE ) );

		assertThat( processor.parsePoint( ">rad(45),1" ), closeTo( new Point3D( Math.cos( PI_OVER_4 ), Math.sin( PI_OVER_4 ), 0 ), TOLERANCE ) );
	}

	@Test
	void parseRelativePolarCoordinates() {
		processor.setAnchor( new Point3D( 1, 1, 0 ) );
		assertThat( processor.parsePoint( "@<rad(180+45),1" ), closeTo( new Point3D( 1 - Math.cos( PI_OVER_4 ), 1 - Math.sin( PI_OVER_4 ), 0 ), TOLERANCE ) );
		assertThat( processor.parsePoint( "@>rad(180+45),1" ), closeTo( new Point3D( 1 - Math.cos( PI_OVER_4 ), 1 - Math.sin( PI_OVER_4 ), 0 ), TOLERANCE ) );
	}

	@Test
	void testParsePointWithWhitespace() {
		processor.setAnchor( new Point3D( 1, 1, 0 ) );
		assertThat( processor.parsePoint( " 1 , 2 " ), is( new Point3D( 1, 2, 0 ) ) );
		assertThat( processor.parsePoint( " @ 1 , 2 " ), is( new Point3D( 2, 3, 0 ) ) );
		assertThat( processor.parsePoint( " > pi , 1 " ), closeTo( new Point3D( -1, 0, 0 ), TOLERANCE ) );
		assertThat(
			processor.parsePoint( " @ < rad ( 180 + 45 ) , 1 " ),
			closeTo( new Point3D( 1 - Math.cos( PI_OVER_4 ), 1 - Math.sin( PI_OVER_4 ), 0 ), TOLERANCE )
		);
	}

}
