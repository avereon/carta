package com.avereon.cartesia.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CadMathTest {

	@Test
	void testDeg() {
		assertThat( CadMath.eval( "deg(pi)" ) ).isEqualTo( java.lang.Math.toDegrees( java.lang.Math.PI ) );
	}

	@Test
	void testRad() {
		assertThat( CadMath.eval( "rad(270)" ) ).isEqualTo( java.lang.Math.toRadians( 270 ) );
	}

	@ParameterizedTest
	@MethodSource
	void eval( String expression, Double expected, IllegalArgumentException expectedException ) {
		if( expectedException == null ) {
			assertThat( CadMath.eval( expression ) ).isEqualTo( expected );
		} else {
			try {
				assertThat( CadMath.eval( expression ) ).isEqualTo( expected );
			} catch( IllegalArgumentException exception ) {
				assertThat( exception.getMessage() ).isEqualTo( expectedException.getMessage() );
			}
		}
	}

	private static Stream<Arguments> eval() {
		return Stream.of(
			// Valid expressions
			Arguments.of( "1/8", 0.125, null ),
			Arguments.of( "1.7e8", 1.7e8, null ),
			Arguments.of( "1.8E-7", 1.8e-7, null ),
			Arguments.of( "sin(pi)", java.lang.Math.sin( java.lang.Math.PI ), null ),
			Arguments.of( "pi * 2.5 ^ 2", 19.634954084936208, null ),
			// Invalid expressions
			Arguments.of( "", null, new IllegalArgumentException() ),
			Arguments.of( "y=mx+b", null, new IllegalArgumentException( "y=mx is not a number" ) ),
			Arguments.of( "y = mx + b", null, new IllegalArgumentException( "y = mx is not a number" ) ),
			Arguments.of( "not a valid expression", null, new IllegalArgumentException( "not a valid expression is not a number" ) )
		);
	}

}
