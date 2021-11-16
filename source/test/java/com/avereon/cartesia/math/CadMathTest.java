package com.avereon.cartesia.math;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class CadMathTest {

	@Test
	void testDeg() {
		assertThat( CadMath.evalNoException( "deg(pi)" ) ).isEqualTo( java.lang.Math.toDegrees( java.lang.Math.PI ) );
	}

	@Test
	void testRad() {
		assertThat( CadMath.evalNoException( "rad(270)" ) ).isEqualTo( java.lang.Math.toRadians( 270 ) );
	}

	@Test
	void testEval() throws Exception {
		assertThat( CadMath.eval( "1/8" ) ).isEqualTo( 0.125 );
		try {
			CadMath.eval( "not a valid expression" );
		} catch( CadMathExpressionException exception ) {
			assertThat( exception.getMessage() ).startsWith( "Unrecognized symbol \"not\"" );
			assertThat( ((ParseException)exception.getCause()).getErrorOffset() ).isEqualTo( -1 );
		}
	}

	@Test
	void testEvalNoException() {
		assertThat( CadMath.evalNoException( "" ) ).isEqualTo( 0.0 );
		assertThat( CadMath.evalNoException( "1/8" ) ).isEqualTo( 0.125 );
		assertThat( CadMath.evalNoException( "sin(pi)" ) ).isEqualTo( java.lang.Math.sin( java.lang.Math.PI ) );
	}

}
