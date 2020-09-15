package com.avereon.cartesia.math;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class MathExTest {

	@Test
	void testDeg() {
		assertThat( MathEx.eval( "deg(pi)" ), is( Math.toDegrees( Math.PI ) ) );
	}

	@Test
	void testRad() {
		assertThat( MathEx.eval( "rad(270)" ), is( Math.toRadians( 270 ) ) );
	}

	@Test
	void testEval() {
		assertThat( MathEx.eval( "1/8" ), is( 0.125 ) );
		assertThat( MathEx.eval( "sin(pi)" ), is( Math.sin( Math.PI ) ) );
	}

	@Test
	void testParse() throws Exception {
		assertThat( MathEx.parse( "1/8" ), is( 0.125 ) );
		try {
			MathEx.parse( "not a valid expression" );
		} catch( ParseException exception ) {
			assertThat( exception.getMessage(), startsWith( "Unrecognized symbol \"not\"" ) );
			assertThat( exception.getErrorOffset(), is( -1 ) );
		}
	}

}
