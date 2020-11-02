package com.avereon.cartesia.math;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class MathsTest {

	@Test
	void testDeg() {
		assertThat( Maths.evalNoException( "deg(pi)" ), is( java.lang.Math.toDegrees( java.lang.Math.PI ) ) );
	}

	@Test
	void testRad() {
		assertThat( Maths.evalNoException( "rad(270)" ), is( java.lang.Math.toRadians( 270 ) ) );
	}

	@Test
	void testEval() throws Exception {
		assertThat( Maths.eval( "1/8" ), is( 0.125 ) );
		try {
			Maths.eval( "not a valid expression" );
		} catch( ExpressionException exception ) {
			assertThat( exception.getMessage(), startsWith( "Unrecognized symbol \"not\"" ) );
			assertThat( ((ParseException)exception.getCause()).getErrorOffset(), is( -1 ) );
		}
	}

	@Test
	void testEvalNoException() {
		assertThat( Maths.evalNoException( "1/8" ), is( 0.125 ) );
		assertThat( Maths.evalNoException( "sin(pi)" ), is( java.lang.Math.sin( java.lang.Math.PI ) ) );
	}

}
