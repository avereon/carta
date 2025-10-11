package com.avereon.cartesia.math;

import org.junit.jupiter.api.Test;

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
	void testEvalNoException() {
		assertThat( CadMath.evalNoException( "" ) ).isEqualTo( 0.0 );
		assertThat( CadMath.evalNoException( "1/8" ) ).isEqualTo( 0.125 );
		assertThat( CadMath.evalNoException( "sin(pi)" ) ).isEqualTo( java.lang.Math.sin( java.lang.Math.PI ) );
	}

	@Test
	void testEvalNoExceptionWithIllegalArgument() {
		assertThat( CadMath.evalNoException( "1/8" ) ).isEqualTo( 0.125 );
		try {
			CadMath.evalNoException( "not a valid expression" );
		} catch( IllegalArgumentException exception ) {
			assertThat( exception.getMessage() ).startsWith( "not a valid expression is not a number" );
		}
	}

}
