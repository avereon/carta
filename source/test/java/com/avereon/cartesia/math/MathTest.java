package com.avereon.cartesia.math;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class MathTest {

	@Test
	void testDeg() {
		assertThat( Math.evalNoException( "deg(pi)" ), is( java.lang.Math.toDegrees( java.lang.Math.PI ) ) );
	}

	@Test
	void testRad() {
		assertThat( Math.evalNoException( "rad(270)" ), is( java.lang.Math.toRadians( 270 ) ) );
	}

	@Test
	void testEval() {
		assertThat( Math.evalNoException( "1/8" ), is( 0.125 ) );
		assertThat( Math.evalNoException( "sin(pi)" ), is( java.lang.Math.sin( java.lang.Math.PI ) ) );
	}

	@Test
	void testParse() throws Exception {
		assertThat( Math.eval( "1/8" ), is( 0.125 ) );
		try {
			Math.eval( "not a valid expression" );
		} catch( ParseException exception ) {
			assertThat( exception.getMessage(), startsWith( "Unrecognized symbol \"not\"" ) );
			assertThat( exception.getErrorOffset(), is( -1 ) );
		}
	}

}
