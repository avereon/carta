package com.avereon.cartesia;

import javafx.geometry.Point2D;
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
	void testParseValue( String string, Double expected, Exception exception ) {
		if( exception == null ) {
			assertThat( ParseUtil.parseValue( string ) ).isEqualTo( expected );
		} else {
			assertThatThrownBy( () -> ParseUtil.parseValue( string ) ).hasMessage( exception.getMessage() ).isInstanceOf( exception.getClass() );
		}
	}

	@ParameterizedTest
	@MethodSource
	void testParsePoint2D( String string, Point2D expected, Exception exception ) {
		if( exception == null ) {
			assertThat( ParseUtil.parsePoint2D( string ) ).isEqualTo( expected );
		} else {
			assertThatThrownBy( () -> ParseUtil.parsePoint2D( string ) ).hasMessage( exception.getMessage() ).isInstanceOf( exception.getClass() );
		}
	}

	@ParameterizedTest
	@MethodSource
	void testParsePoint3D( String string, Point3D expected, Exception exception ) {
		if( exception == null ) {
			assertThat( ParseUtil.parsePoint3D( string ) ).isEqualTo( expected );
		} else {
			assertThatThrownBy( () -> ParseUtil.parsePoint3D( string ) ).hasMessage( exception.getMessage() ).isInstanceOf( exception.getClass() );
		}
	}

	private static Stream<Arguments> testParseValue() {
		return Stream.of(
			// Valid Points
			Arguments.of( "", 0.0, null ),
			Arguments.of( " ", 0.0, null ),
			Arguments.of( "0", 0.0, null ),
			Arguments.of( "6", 6.0, null ),
			Arguments.of( "-3.2e-17", -3.2e-17, null ),
			Arguments.of( "-42.3", -42.3, null ),
			Arguments.of( "\t-42.3\n", -42.3, null ),
			// More than necessary, but still valid
			Arguments.of( "0,0", 0.0, null ),
			Arguments.of( "0,0,0", 0.0, null ),
			Arguments.of( "0,0,0,0", 0.0, null ),
			Arguments.of( "0,0,0,0,0", 0.0, null ),
			// Exceptions
			Arguments.of( "Not a Value", null, new NumberFormatException( "For input string: \"Not a Value\"" ) )
		);
	}

	private static Stream<Arguments> testParsePoint2D() {
		return Stream.of(
			// Valid Points
			Arguments.of( "", Point2D.ZERO, null ),
			Arguments.of( " ", Point2D.ZERO, null ),
			Arguments.of( "0", Point2D.ZERO, null ),
			Arguments.of( "0,0", Point2D.ZERO, null ),
			Arguments.of( "-3e-17,-42.3", new Point2D( -3e-17, -42.3 ), null ),
			Arguments.of( " -3.2e-17 , -42.3 ", new Point2D( -3.2e-17, -42.3 ), null ),
			// More than necessary, but still valid
			Arguments.of( "0,0,0", Point2D.ZERO, null ),
			Arguments.of( "0,0,0,0", Point2D.ZERO, null ),
			Arguments.of( "1,2,3,4,5", new Point2D( 1, 2 ), null ),
			// Exceptions
			Arguments.of( "Not,a,Point", null, new NumberFormatException( "For input string: \"Not\"" ) )
		);
	}

	private static Stream<Arguments> testParsePoint3D() {
		return Stream.of(
			// Valid Points
			Arguments.of( "", Point3D.ZERO, null ),
			Arguments.of( " ", Point3D.ZERO, null ),
			Arguments.of( "0", Point3D.ZERO, null ),
			Arguments.of( "0,0", Point3D.ZERO, null ),
			Arguments.of( "0,0,0", Point3D.ZERO, null ),
			Arguments.of( "6,-3.2e-17,-42.3", new Point3D( 6, -3.2e-17, -42.3 ), null ),
			Arguments.of( " 6 , -3.2e-17 , -42.3 ", new Point3D( 6, -3.2e-17, -42.3 ), null ),
			// More than necessary, but still valid
			Arguments.of( "0,0,0,0", Point3D.ZERO, null ),
			Arguments.of( "1,2,3,4,5", new Point3D( 1, 2, 3 ), null ),
			// Exceptions
			Arguments.of( "Not,a,Point", null, new NumberFormatException( "For input string: \"Not\"" ) )
		);
	}

}
