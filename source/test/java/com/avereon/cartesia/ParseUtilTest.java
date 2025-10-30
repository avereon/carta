package com.avereon.cartesia;

import javafx.geometry.Point3D;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ParseUtilTest {

	@ParameterizedTest
	@MethodSource
	public void testParsePoint3D( String string, Point3D expected, Exception exception ) {
		if( exception == null ) {
			assertThat( ParseUtil.parsePoint3D( string ) ).isEqualTo( expected );
		} else {
			assertThatThrownBy( () -> ParseUtil.parsePoint3D( string ) ).hasMessage( exception.getMessage() ).isInstanceOf( exception.getClass() );
		}

	}

	private static Stream<Arguments> testParsePoint3D() {
		return Stream.of(
			// Exceptions
			Arguments.of( null, null, new NullPointerException( "Point string cannot be null" ) ),
			Arguments.of( "", null, new IllegalArgumentException( "Input string cannot be empty" ) ),
			Arguments.of( "  ", null, new IllegalArgumentException( "Input string cannot be empty" ) ),
			Arguments.of( "Not,a,Point", null, new NumberFormatException( "For input string: \"Not\"" ) ),
			// Valid Points
			Arguments.of( "0", Point3D.ZERO, null ),
			Arguments.of( "0,0", Point3D.ZERO, null ),
			Arguments.of( "0,0,0", Point3D.ZERO, null ),
			Arguments.of( "6,-3e-17,-42.3", new Point3D( 6, -3e-17, -42.3 ), null ),
			// More than necessary, but still valid
			Arguments.of( "0,0,0,0", Point3D.ZERO, null ),
			Arguments.of( "0,0,0,0,0", Point3D.ZERO, null )
		);
	}

}
