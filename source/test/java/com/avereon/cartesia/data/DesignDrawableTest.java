package com.avereon.cartesia.data;

import com.avereon.zerra.color.Paints;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DesignDrawableTest {

	private DesignDrawable drawable;

	private DesignLayer layer;

	@BeforeEach
	void setup() {
		drawable = new DesignCircle();
		layer = new DesignLayer();
		layer.addDrawable( drawable );

		// Check the default values
		assertThat( drawable.getDrawPaint(), is( DesignDrawable.MODE_LAYER ) );
		assertThat( drawable.calcDrawPaint(), is( DesignDrawable.DEFAULT_DRAW_PAINT ) );
		assertThat( drawable.getDrawWidth(), is( DesignDrawable.MODE_LAYER ) );
		assertThat( drawable.calcDrawWidth(), is( DesignDrawable.DEFAULT_DRAW_WIDTH ) );
		assertThat( drawable.getDrawCap(), is( DesignDrawable.MODE_LAYER ) );
		assertThat( drawable.calcDrawCap(), is( DesignDrawable.DEFAULT_DRAW_CAP ) );
		assertThat( drawable.getDrawPattern(), is( DesignDrawable.MODE_LAYER ) );
		assertThat( drawable.calcDrawPattern(), is( DesignDrawable.DEFAULT_DRAW_PATTERN ) );
		assertThat( drawable.getFillPaint(), is( DesignDrawable.MODE_LAYER ) );
		assertThat( drawable.calcFillPaint(), is( DesignDrawable.DEFAULT_FILL_PAINT ) );
	}

	@Test
	void testGetValueMode() {
		assertThat( drawable.getValueMode( null ), is( DesignDrawable.MODE_LAYER ) );
		assertThat( drawable.getValueMode( DesignDrawable.MODE_LAYER ), is( DesignDrawable.MODE_LAYER ) );
		assertThat( drawable.getValueMode( DesignDrawable.MODE_NONE ), is( DesignDrawable.MODE_NONE ) );
		assertThat( drawable.getValueMode( "" ), is( DesignDrawable.MODE_CUSTOM ) );
	}

	@Test
	void testChangeDrawPaintModeFromDefaultToCustom() {
		// Change mode to custom to copy current calcDrawPaint value
		drawable.changeDrawPaintMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawPaint() ), is( DesignDrawable.MODE_CUSTOM ) );
		// Check that the cap value is a copy of the layer cap value
		assertThat( drawable.getDrawPaint(), is( Paints.toString( Color.BLACK ) ) );
		assertThat( drawable.calcDrawPaint(), is( Color.BLACK ) );

		// Change the layer cap to ensure that cap values are still the custom value
		layer.setDrawPaint( Paints.toString( Color.WHITE ) );
		assertThat( drawable.getDrawPaint(), is( Paints.toString( Color.BLACK ) ) );
		assertThat( drawable.calcDrawPaint(), is( Color.BLACK ) );
	}

	@Test
	void testSetDrawPaintWhenDrawPaintModeIsLayer() {
		// Change paint value
		layer.setDrawPaint( Paints.toString( Color.WHITE ) );
		drawable.setDrawPaint( Paints.toString( Color.BLACK ) );
		assertThat( drawable.getValueMode( drawable.getDrawPaint() ), is( DesignDrawable.MODE_CUSTOM ) );
		assertThat( drawable.getDrawPaint(), is( Paints.toString( Color.BLACK ) ) );
		assertThat( drawable.calcDrawPaint(), is( Color.BLACK ) );
	}

	@Test
	void testChangeDrawWidthModeFromDefaultToCustom() {
		// Change mode to custom to copy current calcDrawWidth value
		drawable.changeDrawWidthMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawWidth() ), is( DesignDrawable.MODE_CUSTOM ) );
		// Check that the cap value is a copy of the layer cap value
		assertThat( drawable.getDrawWidth(), is( String.valueOf( DesignDrawable.DEFAULT_DRAW_WIDTH ) ) );
		assertThat( drawable.calcDrawWidth(), is( DesignDrawable.DEFAULT_DRAW_WIDTH ) );

		// Change the layer cap to ensure that cap values are still the custom value
		layer.setDrawWidth( String.valueOf( 1.0 ) );
		assertThat( drawable.getDrawWidth(), is( String.valueOf( DesignDrawable.DEFAULT_DRAW_WIDTH ) ) );
		assertThat( drawable.calcDrawWidth(), is( DesignDrawable.DEFAULT_DRAW_WIDTH ) );
	}

	@Test
	void testSetDrawWidthWhenDrawWidthModeIsLayer() {
		// Change paint value
		layer.setDrawWidth( String.valueOf( DesignDrawable.DEFAULT_DRAW_WIDTH ) );
		drawable.setDrawWidth( String.valueOf( 0.03 ) );
		assertThat( drawable.getValueMode( drawable.getDrawWidth() ), is( DesignDrawable.MODE_CUSTOM ) );
		assertThat( drawable.getDrawWidth(), is( String.valueOf( 0.03 ) ) );
		assertThat( drawable.calcDrawWidth(), is( 0.03 ) );
	}

	@Test
	void testChangeDrawCapModeFromDefaultToCustom() {
		// Change mode to custom to copy current calcDrawCap value
		drawable.changeDrawCapMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawCap() ), is( DesignDrawable.MODE_CUSTOM ) );
		// Check that the cap value is a copy of the layer cap value
		assertThat( drawable.getDrawCap(), is( StrokeLineCap.SQUARE.name().toLowerCase() ) );
		assertThat( drawable.calcDrawCap(), is( StrokeLineCap.SQUARE ) );

		// Change the layer cap to ensure that cap values are still the custom value
		layer.setDrawCap( StrokeLineCap.ROUND.name().toLowerCase() );
		assertThat( drawable.getDrawCap(), is( StrokeLineCap.SQUARE.name().toLowerCase() ) );
		assertThat( drawable.calcDrawCap(), is( StrokeLineCap.SQUARE ) );
	}

	@Test
	void testSetDrawCapWhenDrawCapModeIsLayer() {
		// Change cap value
		layer.setDrawCap( StrokeLineCap.SQUARE.name().toLowerCase() );
		drawable.setDrawCap( StrokeLineCap.ROUND.name().toLowerCase() );
		assertThat( drawable.getValueMode( drawable.getDrawCap() ), is( DesignDrawable.MODE_CUSTOM ) );
		assertThat( drawable.getDrawCap(), is( StrokeLineCap.ROUND.name().toLowerCase() ) );
		assertThat( drawable.calcDrawCap(), is( StrokeLineCap.ROUND ) );
	}

	@Test
	void testChangeDrawPatternModeFromDefaultToCustom() {
		// Change mode to custom to copy current calcDrawPattern value
		drawable.changeDrawPatternMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawPattern() ), is( DesignDrawable.MODE_CUSTOM ) );
		// Check that the cap value is a copy of the layer cap value
		assertThat( drawable.getDrawPattern(), is( String.valueOf( DesignDrawable.DEFAULT_DRAW_PATTERN ) ) );
		assertThat( drawable.calcDrawPattern(), is( DesignDrawable.DEFAULT_DRAW_PATTERN ) );

		// Change the layer cap to ensure that cap values are still the custom value
		layer.setDrawPattern( "1/8,1/4" );
		assertThat( drawable.getDrawPattern(), is( String.valueOf( DesignDrawable.DEFAULT_DRAW_PATTERN ) ) );
		assertThat( drawable.calcDrawPattern(), is( DesignDrawable.DEFAULT_DRAW_PATTERN ) );
	}

	@Test
	void testSetDrawPatternWhenDrawPatternModeIsLayer() {
		// Change paint value
		layer.setDrawPattern( String.valueOf( DesignDrawable.DEFAULT_DRAW_PATTERN ) );
		drawable.setDrawPattern( "0.5,0.5" );
		assertThat( drawable.getValueMode( drawable.getDrawPattern() ), is( DesignDrawable.MODE_CUSTOM ) );
		assertThat( drawable.getDrawPattern(), is( "0.5,0.5" ) );
		assertThat( drawable.calcDrawPattern(), is( "0.5,0.5" ) );
	}

	@Test
	void testChangeFillPaintModeFromDefaultToCustom() {
		// Change mode to custom to copy current calcFillPaint value
		drawable.changeFillPaintMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getFillPaint() ), is( DesignDrawable.MODE_CUSTOM ) );
		// Check that the cap value is a copy of the layer cap value
		assertThat( drawable.getFillPaint(), is( Paints.toString( Color.BLACK ) ) );
		assertThat( drawable.calcFillPaint(), is( Color.BLACK ) );

		// Change the layer cap to ensure that cap values are still the custom value
		layer.setFillPaint( Paints.toString( Color.WHITE ) );
		assertThat( drawable.getFillPaint(), is( Paints.toString( Color.BLACK ) ) );
		assertThat( drawable.calcFillPaint(), is( Color.BLACK ) );
	}

	@Test
	void testSetFillPaintWhenFillPaintModeIsLayer() {
		// Change paint value
		layer.setFillPaint( Paints.toString( Color.WHITE ) );
		drawable.setFillPaint( Paints.toString( Color.BLACK ) );
		assertThat( drawable.getValueMode( drawable.getFillPaint() ), is( DesignDrawable.MODE_CUSTOM ) );
		assertThat( drawable.getFillPaint(), is( Paints.toString( Color.BLACK ) ) );
		assertThat( drawable.calcFillPaint(), is( Color.BLACK ) );
	}

}
