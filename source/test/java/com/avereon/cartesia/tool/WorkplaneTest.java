package com.avereon.cartesia.tool;

import com.avereon.cartesia.math.CadMath;
import com.avereon.zerra.color.Colors;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class WorkplaneTest {

	private final Workplane workplane = new Workplane();

	@Test
	void defaultOriginConstant() {
		// The default origin should be "0,0". The workplane is a 2D plane and has no Z coordinate.
		assertThat( Workplane.DEFAULT_ORIGIN ).isEqualTo( "0,0" );
	}

	@ParameterizedTest
	@MethodSource( "gridAxisVisibleArguments" )
	void isGridAxisVisible( Boolean input, boolean expected ) {
		// given
		assertThat( workplane.isGridAxisVisible() ).isEqualTo( Workplane.DEFAULT_GRID_AXIS_VISIBLE );

		// when
		workplane.setGridAxisVisible( input );

		// then
		assertThat( workplane.isGridAxisVisible() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "gridMajorVisibleArguments" )
	void isMajorGridVisible( Boolean input, boolean expected ) {
		// given
		assertThat( workplane.isMajorGridVisible() ).isEqualTo( Workplane.DEFAULT_GRID_AXIS_VISIBLE );

		// when
		workplane.setMajorGridVisible( input );

		// then
		assertThat( workplane.isMajorGridVisible() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "gridMinorVisibleArguments" )
	void isMinorGridVisible( Boolean input, boolean expected ) {
		// given
		assertThat( workplane.isMinorGridVisible() ).isEqualTo( Workplane.DEFAULT_GRID_AXIS_VISIBLE );

		// when
		workplane.setMinorGridVisible( input );

		// then
		assertThat( workplane.isMinorGridVisible() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "majorGridShowingArguments" )
	void majorGridShowing( Boolean input, boolean expected ) {
		// given
		assertThat( workplane.isMajorGridShowing() ).isEqualTo( Workplane.DEFAULT_GRID_MAJOR_VISIBLE );

		// when
		workplane.setMajorGridShowing( input );

		// then
		assertThat( workplane.isMajorGridShowing() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "minorGridShowingArguments" )
	void minorGridShowing( Boolean input, boolean expected ) {
		// given
		assertThat( workplane.isMinorGridShowing() ).isEqualTo( Workplane.DEFAULT_GRID_MAJOR_VISIBLE );

		// when
		workplane.setMinorGridShowing( input );

		// then
		assertThat( workplane.isMinorGridShowing() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "gridAxisPaintArguments" )
	void gridAxisPaint( String input, Color expected ) {
		// given
		assertThat( workplane.calcGridAxisPaint() ).isEqualTo( Colors.parse( Workplane.DEFAULT_GRID_AXIS_PAINT ) );

		// when
		workplane.setGridAxisPaint( input );

		// then
		assertThat( workplane.calcGridAxisPaint() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "majorGridPaintArguments" )
	void majorGridPaint( String input, Color expected ) {
		// given
		assertThat( workplane.calcMajorGridPaint() ).isEqualTo( Colors.parse( Workplane.DEFAULT_GRID_MAJOR_PAINT ) );

		// when
		workplane.setMajorGridPaint( input );

		// then
		assertThat( workplane.calcMajorGridPaint() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "minorGridPaintArguments" )
	void minorGridPaint( String input, Color expected ) {
		// given
		assertThat( workplane.calcMinorGridPaint() ).isEqualTo( Colors.parse( Workplane.DEFAULT_GRID_MINOR_PAINT ) );

		// when
		workplane.setMinorGridPaint( input );

		// then
		assertThat( workplane.calcMinorGridPaint() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "gridAxisWidthArguments" )
	void gridAxisWidth( String input, double expected ) {
		// given
		assertThat( workplane.calcGridAxisWidth() ).isEqualTo( CadMath.eval( Workplane.DEFAULT_GRID_AXIS_WIDTH ) );

		// when
		workplane.setGridAxisWidth( input );

		// then
		assertThat( workplane.calcGridAxisWidth() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "majorGridWidthArguments" )
	void majorGridWidth( String input, double expected ) {
		// given
		assertThat( workplane.calcMajorGridWidth() ).isEqualTo( CadMath.eval( Workplane.DEFAULT_GRID_MAJOR_WIDTH ) );

		// when
		workplane.setMajorGridWidth( input );

		// then
		assertThat( workplane.calcMajorGridWidth() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "minorGridWidthArguments" )
	void minorGridWidth( String input, double expected ) {
		// given
		assertThat( workplane.calcMinorGridWidth() ).isEqualTo( CadMath.eval( Workplane.DEFAULT_GRID_MINOR_WIDTH ) );

		// when
		workplane.setMinorGridWidth( input );

		// then
		assertThat( workplane.calcMinorGridWidth() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "majorGridSizeArguments" )
	void majorGridX( String input, double expected ) {
		// given
		assertThat( workplane.calcMajorGridX() ).isEqualTo( CadMath.eval( Workplane.DEFAULT_GRID_MAJOR_SIZE ) );

		// when
		workplane.setMajorGridX( input );

		// then
		assertThat( workplane.calcMajorGridX() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "majorGridSizeArguments" )
	void majorGridY( String input, double expected ) {
		// given
		assertThat( workplane.calcMajorGridY() ).isEqualTo( CadMath.eval( Workplane.DEFAULT_GRID_MAJOR_SIZE ) );

		// when
		workplane.setMajorGridY( input );

		// then
		assertThat( workplane.calcMajorGridY() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "minorGridSizeArguments" )
	void minorGridX( String input, double expected ) {
		// given
		assertThat( workplane.calcMinorGridX() ).isEqualTo( CadMath.eval( Workplane.DEFAULT_GRID_MINOR_SIZE ) );

		// when
		workplane.setMinorGridX( input );

		// then
		assertThat( workplane.calcMinorGridX() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "minorGridSizeArguments" )
	void minorGridY( String input, double expected ) {
		// given
		assertThat( workplane.calcMinorGridY() ).isEqualTo( CadMath.eval( Workplane.DEFAULT_GRID_MINOR_SIZE ) );

		// when
		workplane.setMinorGridY( input );

		// then
		assertThat( workplane.calcMinorGridY() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "snapGridSizeArguments" )
	void snapGridX( String input, double expected ) {
		// given
		assertThat( workplane.calcSnapGridX() ).isEqualTo( CadMath.eval( Workplane.DEFAULT_GRID_SNAP_SIZE ) );

		// when
		workplane.setSnapGridX( input );

		// then
		assertThat( workplane.calcSnapGridX() ).isEqualTo( expected );
	}

	@ParameterizedTest
	@MethodSource( "snapGridSizeArguments" )
	void snapGridY( String input, double expected ) {
		// given
		assertThat( workplane.calcSnapGridY() ).isEqualTo( CadMath.eval( Workplane.DEFAULT_GRID_SNAP_SIZE ) );

		// when
		workplane.setSnapGridY( input );

		// then
		assertThat( workplane.calcSnapGridY() ).isEqualTo( expected );
	}

	private static Stream<Arguments> gridAxisWidthArguments() {
		return widthArguments( Workplane.DEFAULT_GRID_AXIS_WIDTH );
	}

	private static Stream<Arguments> majorGridWidthArguments() {
		return widthArguments( Workplane.DEFAULT_GRID_MAJOR_WIDTH );
	}

	private static Stream<Arguments> minorGridWidthArguments() {
		return widthArguments( Workplane.DEFAULT_GRID_MINOR_WIDTH );
	}

	private static Stream<Arguments> gridAxisVisibleArguments() {
		return booleanArguments( Workplane.DEFAULT_GRID_AXIS_VISIBLE );
	}

	private static Stream<Arguments> gridMajorVisibleArguments() {
		return booleanArguments( Workplane.DEFAULT_GRID_MAJOR_VISIBLE );
	}

	private static Stream<Arguments> gridMinorVisibleArguments() {
		return booleanArguments( Workplane.DEFAULT_GRID_MINOR_VISIBLE );
	}

	private static Stream<Arguments> majorGridShowingArguments() {
		return booleanArguments( Workplane.DEFAULT_GRID_MAJOR_VISIBLE );
	}

	private static Stream<Arguments> minorGridShowingArguments() {
		return booleanArguments( Workplane.DEFAULT_GRID_MINOR_VISIBLE );
	}

	private static Stream<Arguments> booleanArguments( boolean defaultBoolean ) {
		return Stream.of( Arguments.of( null, defaultBoolean ), Arguments.of( Boolean.FALSE, false ), Arguments.of( null, defaultBoolean ) );
	}

	private static Stream<Arguments> gridAxisPaintArguments() {
		return paintArguments( Workplane.DEFAULT_GRID_AXIS_PAINT );
	}

	private static Stream<Arguments> majorGridPaintArguments() {
		return paintArguments( Workplane.DEFAULT_GRID_MAJOR_PAINT );
	}

	private static Stream<Arguments> minorGridPaintArguments() {
		return paintArguments( Workplane.DEFAULT_GRID_MINOR_PAINT );
	}

	private static Stream<Arguments> majorGridSizeArguments() {
		return valueArguments( Workplane.DEFAULT_GRID_MAJOR_SIZE );
	}

	private static Stream<Arguments> minorGridSizeArguments() {
		return valueArguments( Workplane.DEFAULT_GRID_MINOR_SIZE );
	}

	private static Stream<Arguments> snapGridSizeArguments() {
		return valueArguments( Workplane.DEFAULT_GRID_SNAP_SIZE );
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

	private static Stream<Arguments> widthArguments( String defaultWidth ) {
		return Stream.of(
			Arguments.of( null, CadMath.eval( defaultWidth ) ),
			Arguments.of( "0.5", 0.5 ),
			Arguments.of( "1/2", 0.5 ),
			Arguments.of( "1.0", 1.0 ),
			Arguments.of( "2/1", 2.0 ),
			Arguments.of( "2.0", 2.0 ),
			Arguments.of( null, CadMath.eval( defaultWidth ) )
		);
	}

	private static Stream<Arguments> valueArguments( String defaultValue ) {
		return Stream.of(
			Arguments.of( null, CadMath.eval( defaultValue ) ),
			Arguments.of( "0.5", 0.5 ),
			Arguments.of( "1/2", 0.5 ),
			Arguments.of( "1.0", 1.0 ),
			Arguments.of( "2/1", 2.0 ),
			Arguments.of( "2.0", 2.0 ),
			Arguments.of( null, CadMath.eval( defaultValue ) )
		);
	}

}
