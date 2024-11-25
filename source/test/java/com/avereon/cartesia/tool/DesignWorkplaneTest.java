package com.avereon.cartesia.tool;

import com.avereon.zarra.color.Colors;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DesignWorkplaneTest {

	private final DesignWorkplane workplane = new DesignWorkplane();

	@ParameterizedTest
	@MethodSource( "gridAxisVisibleArguments" )
	void isGridAxisVisible( Boolean input, boolean expected ) {
		// given
		assertThat( workplane.isGridAxisVisible() ).isEqualTo( DesignWorkplane.DEFAULT_GRID_AXIS_VISIBLE );

		// when
		workplane.setGridAxisVisible( input );

		// then
		assertThat( workplane.isGridAxisVisible() ).isEqualTo( expected );
	}

	private static Stream<Arguments> gridAxisVisibleArguments() {
		return booleanArguments( DesignWorkplane.DEFAULT_GRID_AXIS_VISIBLE );
	}

	private static Stream<Arguments> booleanArguments( boolean defaultBoolean ) {
		return Stream.of( Arguments.of( null, defaultBoolean ), Arguments.of( Boolean.FALSE, false ) );
	}

	@ParameterizedTest
	@MethodSource( "gridAxisPaintArguments" )
	void calcGridAxisPaint( String input, Color expected ) {
		// given
		assertThat( workplane.calcGridAxisPaint() ).isEqualTo( Colors.parse( DesignWorkplane.DEFAULT_GRID_AXIS_PAINT ) );

		// when
		workplane.setGridAxisPaint( input );

		// then
		assertThat( workplane.calcGridAxisPaint() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "majorGridPaintArguments" )
	void calcMajorGridPaint( String input, Color expected ) {
		// given
		assertThat( workplane.calcMajorGridPaint() ).isEqualTo( Colors.parse( DesignWorkplane.DEFAULT_GRID_MAJOR_PAINT ) );

		// when
		workplane.setMajorGridPaint( input );

		// then
		assertThat( workplane.calcMajorGridPaint() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "minorGridPaintArguments" )
	void calcMinorGridPaint( String input, Color expected ) {
		// given
		assertThat( workplane.calcMinorGridPaint() ).isEqualTo( Colors.parse( DesignWorkplane.DEFAULT_GRID_MINOR_PAINT ) );

		// when
		workplane.setMinorGridPaint( input );

		// then
		assertThat( workplane.calcMinorGridPaint() ).isEqualTo( expected );
	}

	@Test
	void performanceComparisonBoolean() {
		//		Random random = new Random();
		//		int iterations = 1000000000;
		//
		//		long start = System.currentTimeMillis();
		//		for( int i = 0; i < iterations; i++ ) {
		//			boolean flag = random.nextBoolean();
		//		}
		//		long end = System.currentTimeMillis();
		//		long booleanDuration = end - start;
		//
		//		start = System.currentTimeMillis();
		//		for( int i = 0; i < iterations; i++ ) {
		//			boolean flag = Boolean.parseBoolean( random.nextBoolean() ? "true" : "false" );
		//		}
		//		end = System.currentTimeMillis();
		//		long parseBooleanDuration = end - start;
		//
		//		System.out.println( "Boolean: " + booleanDuration + "ms" );
		//		System.out.println( "Boolean: " + parseBooleanDuration + "ms" );
		//		System.out.println( "Performance difference: " + (double)parseBooleanDuration / booleanDuration );

		// RESULT Parsing the boolean is about 60-65% slower than direct method access
	}

	@Test
	void performanceComparisonBooleanCast() {
		//				Random random = new Random();
		//				int iterations = 1000000000;
		//
		//				long start = System.currentTimeMillis();
		//				for( int i = 0; i < iterations; i++ ) {
		//					boolean flag = random.nextBoolean();
		//				}
		//				long end = System.currentTimeMillis();
		//				long booleanDuration = end - start;
		//
		//				start = System.currentTimeMillis();
		//				for( int i = 0; i < iterations; i++ ) {
		//					boolean flag = random.nextBoolean() ? Boolean.TRUE : Boolean.FALSE;
		//				}
		//				end = System.currentTimeMillis();
		//				long parseBooleanDuration = end - start;
		//
		//				System.out.println( "Boolean: " + booleanDuration + "ms" );
		//				System.out.println( "Boolean: " + parseBooleanDuration + "ms" );
		//				System.out.println( "Performance difference: " + (double)parseBooleanDuration / booleanDuration );

		// RESULT Casting the boolean is about 3% faster than direct method access
	}

	private static Stream<Arguments> gridAxisPaintArguments() {
		return paintArguments( DesignWorkplane.DEFAULT_GRID_AXIS_PAINT );
	}

	private static Stream<Arguments> majorGridPaintArguments() {
		return paintArguments( DesignWorkplane.DEFAULT_GRID_MAJOR_PAINT );
	}

	private static Stream<Arguments> minorGridPaintArguments() {
		return paintArguments( DesignWorkplane.DEFAULT_GRID_MINOR_PAINT );
	}

	private static Stream<Arguments> paintArguments( String defaultColor ) {
		return Stream.of(
			Arguments.of( null, Colors.parse( defaultColor ) ),
			Arguments.of( "#000000", Colors.parse( "#000000ff" ) ),
			Arguments.of( "#ff0000", Colors.parse( "#ff0000ff" ) ),
			Arguments.of( "#00ff00", Colors.parse( "#00ff00ff" ) ),
			Arguments.of( "#0000ff", Colors.parse( "#0000ffff" ) ),
			Arguments.of( "#ffffff", Colors.parse( "#ffffffff" ) ),
			Arguments.of( "#000000ff", Colors.parse( "#000000ff" ) ),
			Arguments.of( "#ff0000ff", Colors.parse( "#ff0000ff" ) ),
			Arguments.of( "#00ff00ff", Colors.parse( "#00ff00ff" ) ),
			Arguments.of( "#0000ffff", Colors.parse( "#0000ffff" ) ),
			Arguments.of( "#ffffffff", Colors.parse( "#ffffffff" ) ),
			Arguments.of( null, Colors.parse( defaultColor ) )
		);
	}

}
