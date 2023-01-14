package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.zarra.color.Paints;
import com.avereon.zarra.font.FontUtil;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignDrawableTest {

	private DesignDrawable drawable;

	private DesignLayer layer;

	@BeforeEach
	void setup() {
		drawable = new DesignLine();
		layer = new DesignLayer();
		layer.addDrawable( drawable );

		// Check the default values
		assertThat( drawable.getOrder() ).isEqualTo( -1 );
		assertThat( drawable.getDrawPaint() ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( drawable.calcDrawPaint() ).isEqualTo( Paints.parse( DesignLayer.DEFAULT_DRAW_PAINT ) );
		assertThat( drawable.getDrawWidth() ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( drawable.calcDrawWidth() ).isEqualTo( CadMath.evalNoException( DesignLayer.DEFAULT_DRAW_WIDTH ) );
		assertThat( drawable.getDrawCap() ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( drawable.calcDrawCap() ).isEqualTo( StrokeLineCap.valueOf( DesignLayer.DEFAULT_DRAW_CAP.toUpperCase() ) );
		assertThat( drawable.getDrawPattern() ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( drawable.calcDrawPattern() ).isEqualTo( CadShapes.parseDashPattern( DesignLayer.DEFAULT_DRAW_PATTERN ) );
		assertThat( drawable.getFillPaint() ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( drawable.calcFillPaint() ).isEqualTo( Paints.parse( DesignLayer.DEFAULT_FILL_PAINT ) );
		assertThat( layer.getTextFont() ).isEqualTo( DesignLayer.DEFAULT_TEXT_FONT );
		assertThat( layer.calcTextFont() ).isEqualTo( FontUtil.decode( DesignLayer.DEFAULT_TEXT_FONT ) );
	}

	@Test
	void testGetValueMode() {
		assertThat( drawable.getValueMode( null ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( "" ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( DesignDrawable.MODE_LAYER ) ).isEqualTo( DesignDrawable.MODE_LAYER );
	}

	@Test
	void testLayerDefaults() {
		assertThat( layer.getDrawPaint() ).isEqualTo( DesignLayer.DEFAULT_DRAW_PAINT );
		assertThat( layer.getDrawWidth() ).isEqualTo( DesignLayer.DEFAULT_DRAW_WIDTH );
		assertThat( layer.getDrawCap() ).isEqualTo( DesignLayer.DEFAULT_DRAW_CAP );
		assertThat( layer.getDrawPattern() ).isEqualTo( DesignLayer.DEFAULT_DRAW_PATTERN );
		assertThat( layer.getFillPaint() ).isEqualTo( DesignLayer.DEFAULT_FILL_PAINT );
		assertThat( layer.getTextFont() ).isEqualTo( DesignLayer.DEFAULT_TEXT_FONT );
	}

	@Test
	void testLayerCalcDefaults() {
		assertThat( layer.calcDrawPaint() ).isEqualTo( Paints.parse( DesignLayer.DEFAULT_DRAW_PAINT ) );
		assertThat( layer.calcDrawWidth() ).isEqualTo( CadMath.evalNoException( DesignLayer.DEFAULT_DRAW_WIDTH ) );
		assertThat( layer.calcDrawCap() ).isEqualTo( StrokeLineCap.valueOf( DesignLayer.DEFAULT_DRAW_CAP.toUpperCase() ) );
		assertThat( layer.calcDrawPattern() ).isEqualTo( CadShapes.parseDashPattern( DesignLayer.DEFAULT_DRAW_PATTERN ) );
		assertThat( layer.calcFillPaint() ).isEqualTo( Paints.parse( DesignLayer.DEFAULT_FILL_PAINT ) );
		assertThat( layer.calcTextFont() ).isEqualTo( FontUtil.decode( DesignLayer.DEFAULT_TEXT_FONT ) );
	}

	@Test
	void testChangeDrawPaintModeFromDefaultToCustom() {
		// Change mode to custom to copy current getDrawPaintWithInheritance value
		drawable.changeDrawPaintMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawPaint() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the draw paint value is a copy of the layer draw paint value
		assertThat( drawable.getDrawPaint() ).isEqualTo( Paints.toString( Color.BLACK ) );
		assertThat( drawable.calcDrawPaint() ).isEqualTo( Color.BLACK );

		// Change the layer draw paint to ensure that draw paint value is still the custom value
		layer.setDrawPaint( Paints.toString( Color.WHITE ) );
		assertThat( drawable.getDrawPaint() ).isEqualTo( Paints.toString( Color.BLACK ) );
		assertThat( drawable.calcDrawPaint() ).isEqualTo( Color.BLACK );
	}

	@Test
	void testChangeDrawPaintModeFromLayerToCustom() {
		String layerDrawPaint = "#402080";
		layer.setDrawPaint( layerDrawPaint );

		// Change mode to custom to copy current getDrawPaintWithInheritance value
		drawable.changeDrawPaintMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawPaint() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the draw paint value is a copy of the layer draw paint value
		assertThat( drawable.getDrawPaint() ).isEqualTo( layerDrawPaint );
		assertThat( drawable.calcDrawPaint() ).isEqualTo( Paints.parse( layerDrawPaint ) );

		// Change the layer draw paint to ensure that draw paint value is still the custom value
		layer.setDrawPaint( Paints.toString( Color.WHITE ) );
		assertThat( drawable.getDrawPaint() ).isEqualTo( layerDrawPaint );
		assertThat( drawable.calcDrawPaint() ).isEqualTo( Paints.parse( layerDrawPaint ) );
	}

	@Test
	void testSetDrawPaintWhenDrawPaintModeIsLayer() {
		drawable.setDrawPaint( Paints.toString( Color.GREEN ) );
		assertThat( drawable.getValueMode( drawable.getDrawPaint() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getDrawPaint() ).isEqualTo( Paints.toString( Color.GREEN ) );
		assertThat( drawable.calcDrawPaint() ).isEqualTo( Color.GREEN );
	}

	@Test
	void testChangeDrawWidthModeFromDefaultToCustom() {
		// Change mode to custom to copy current getDrawWidthWithInheritance value
		drawable.changeDrawWidthMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawWidth() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the width value is a copy of the layer width value
		assertThat( drawable.getDrawWidth() ).isEqualTo( DesignLayer.DEFAULT_DRAW_WIDTH );
		assertThat( drawable.calcDrawWidth() ).isEqualTo( CadMath.evalNoException( DesignLayer.DEFAULT_DRAW_WIDTH ) );

		// Change the layer width to ensure that width value is still the custom value
		layer.setDrawWidth( String.valueOf( 1.0 ) );
		assertThat( drawable.getDrawWidth() ).isEqualTo( DesignLayer.DEFAULT_DRAW_WIDTH );
		assertThat( drawable.calcDrawWidth() ).isEqualTo( CadMath.evalNoException( DesignLayer.DEFAULT_DRAW_WIDTH ) );
	}

	@Test
	void testChangeDrawWidthModeFromLayerToCustom() {
		String layerWidth = "1/2";
		layer.setDrawWidth( layerWidth );

		// Change mode to custom to copy current getDrawWidthWithInheritance value
		drawable.changeDrawWidthMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawWidth() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the width value is a copy of the layer width value
		assertThat( drawable.getDrawWidth() ).isEqualTo( layerWidth );
		assertThat( drawable.calcDrawWidth() ).isEqualTo( CadMath.evalNoException( layerWidth ) );

		// Change the layer width to ensure that width value is still the custom value
		layer.setDrawWidth( "1/3" );
		assertThat( drawable.getDrawWidth() ).isEqualTo( layerWidth );
		assertThat( drawable.calcDrawWidth() ).isEqualTo( CadMath.evalNoException( layerWidth ) );
	}

	@Test
	void testSetDrawWidthWhenDrawWidthModeIsLayer() {
		drawable.setDrawWidth( String.valueOf( 0.03 ) );
		assertThat( drawable.getValueMode( drawable.getDrawWidth() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getDrawWidth() ).isEqualTo( String.valueOf( 0.03 ) );
		assertThat( drawable.calcDrawWidth() ).isEqualTo( 0.03 );
	}

	@Test
	void testChangeDrawCapModeFromDefaultToCustom() {
		// Change mode to custom to copy current getDrawCapWithInheritance value
		drawable.changeDrawCapMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawCap() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the cap value is a copy of the layer cap value
		assertThat( drawable.getDrawCap() ).isEqualTo( DesignLayer.DEFAULT_DRAW_CAP );
		assertThat( drawable.calcDrawCap() ).isEqualTo( StrokeLineCap.valueOf( DesignLayer.DEFAULT_DRAW_CAP.toUpperCase() ) );

		// Change the layer cap to ensure that cap value is still the custom value
		layer.setDrawCap( StrokeLineCap.ROUND.name().toLowerCase() );
		assertThat( drawable.getDrawCap() ).isEqualTo( DesignLayer.DEFAULT_DRAW_CAP );
		assertThat( drawable.calcDrawCap() ).isEqualTo( StrokeLineCap.valueOf( DesignLayer.DEFAULT_DRAW_CAP.toUpperCase() ) );
	}

	@Test
	void testChangeDrawCapModeFromLayerToCustom() {
		String layerCap = StrokeLineCap.SQUARE.name().toLowerCase();
		layer.setDrawCap( layerCap );

		// Change mode to custom to copy current getDrawCapWithInheritance value
		drawable.changeDrawCapMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawCap() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the cap value is a copy of the layer cap value
		assertThat( drawable.getDrawCap() ).isEqualTo( layerCap );
		assertThat( drawable.calcDrawCap() ).isEqualTo( StrokeLineCap.valueOf( layerCap.toUpperCase() ) );

		// Change the layer cap to ensure that cap value is still the custom value
		layer.setDrawCap( StrokeLineCap.ROUND.name().toLowerCase() );
		assertThat( drawable.getDrawCap() ).isEqualTo( layerCap );
		assertThat( drawable.calcDrawCap() ).isEqualTo( StrokeLineCap.valueOf( layerCap.toUpperCase() ) );
	}

	@Test
	void testSetDrawCapWhenDrawCapModeIsLayer() {
		assertThat( drawable.getDrawCap() ).isEqualTo( DesignLayer.MODE_LAYER );
		assertThat( drawable.calcDrawCap() ).isEqualTo( StrokeLineCap.valueOf( DesignLayer.DEFAULT_DRAW_CAP.toUpperCase() ) );

		// Change the layer cap to ensure that the layer cap value is used
		layer.setDrawCap( StrokeLineCap.SQUARE.name().toLowerCase() );
		assertThat( drawable.getValueMode( drawable.getDrawCap() ) ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( drawable.getDrawCap() ).isEqualTo( DesignLayer.MODE_LAYER );
		assertThat( drawable.calcDrawCap() ).isEqualTo( StrokeLineCap.SQUARE );

		drawable.setDrawCap( StrokeLineCap.ROUND.name().toLowerCase() );
		assertThat( drawable.getValueMode( drawable.getDrawCap() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getDrawCap() ).isEqualTo( StrokeLineCap.ROUND.name().toLowerCase() );
		assertThat( drawable.calcDrawCap() ).isEqualTo( StrokeLineCap.ROUND );
	}

	@Test
	void testChangeDrawPatternModeFromDefaultToCustom() {
		// Change mode to custom to copy current getDrawPatternWithInheritance value
		drawable.changeDrawPatternMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawPattern() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the pattern is a copy of the layer pattern value
		assertThat( drawable.getDrawPattern() ).isEqualTo( DesignLayer.DEFAULT_DRAW_PATTERN );
		assertThat( drawable.calcDrawPattern() ).isEqualTo( CadShapes.parseDashPattern( DesignLayer.DEFAULT_DRAW_PATTERN ) );

		// Change the layer pattern to ensure that pattern value is still the custom value
		layer.setDrawPattern( "1/8,1/4" );
		assertThat( drawable.getDrawPattern() ).isEqualTo( DesignLayer.DEFAULT_DRAW_PATTERN );
		assertThat( drawable.calcDrawPattern() ).isEqualTo( CadShapes.parseDashPattern( DesignLayer.DEFAULT_DRAW_PATTERN ) );
	}

	@Test
	void testChangeDrawPatternModeFromLayerToCustom() {
		String layerPattern = "1/6,1/3";
		layer.setDrawPattern( layerPattern );

		// Change mode to custom to copy current getDrawPatternWithInheritance value
		drawable.changeDrawPatternMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawPattern() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the pattern value is a copy of the layer pattern value
		assertThat( drawable.getDrawPattern() ).isEqualTo( layerPattern );
		assertThat( drawable.calcDrawPattern() ).isEqualTo( CadShapes.parseDashPattern( layerPattern ) );

		// Change the layer pattern to ensure that pattern value is still the custom value
		layer.setDrawPattern( "1/8,1/4" );
		assertThat( drawable.getDrawPattern() ).isEqualTo( layerPattern );
		assertThat( drawable.calcDrawPattern() ).isEqualTo( CadShapes.parseDashPattern( layerPattern ) );
	}

	@Test
	void testSetDrawPatternWhenDrawPatternModeIsLayer() {
		assertThat( drawable.getValueMode( drawable.getDrawPattern() ) ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( drawable.getDrawPattern() ).isEqualTo( DesignLayer.MODE_LAYER );
		assertThat( drawable.calcDrawPattern() ).isEqualTo( CadShapes.parseDashPattern( DesignLayer.DEFAULT_DRAW_PATTERN ) );

		layer.setDrawPattern( "0.5,1/4" );
		assertThat( drawable.getValueMode( drawable.getDrawPattern() ) ).isEqualTo( DesignDrawable.MODE_LAYER );
		assertThat( drawable.getDrawPattern() ).isEqualTo( DesignLayer.MODE_LAYER );
		assertThat( drawable.calcDrawPattern() ).isEqualTo( List.of( 0.5, 0.25 ) );

		drawable.setDrawPattern( "1/8, 0.5" );
		assertThat( drawable.getValueMode( drawable.getDrawPattern() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getDrawPattern() ).isEqualTo( "1/8, 0.5" );
		assertThat( drawable.calcDrawPattern() ).isEqualTo( List.of( 0.125, 0.5 ) );
	}

	@Test
	void testChangeFillPaintModeFromDefaultToCustom() {
		// Change mode to custom to copy current getFillPaintWithInheritance value
		drawable.changeFillPaintMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getFillPaint() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the fill paint value is a copy of the layer fill paint value
		assertThat( drawable.getFillPaint() ).isEqualTo( DesignLayer.DEFAULT_FILL_PAINT );
		assertThat( drawable.calcFillPaint() ).isEqualTo( Paints.parse( DesignLayer.DEFAULT_FILL_PAINT ) );

		// Change the layer fill paint to ensure that fill paint value is still the custom value
		layer.setFillPaint( Paints.toString( Color.WHITE ) );
		assertThat( drawable.getFillPaint() ).isEqualTo( DesignLayer.DEFAULT_FILL_PAINT );
		assertThat( drawable.calcFillPaint() ).isEqualTo( Paints.parse( DesignLayer.DEFAULT_FILL_PAINT ) );
	}

	@Test
	void testChangeFillPaintModeFromLayerToCustom() {
		String layerFillPaint = "#204080";
		layer.setFillPaint( layerFillPaint );

		// Change mode to custom to copy current getFillPaintWithInheritance value
		drawable.changeFillPaintMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getFillPaint() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );

		// Check that the fill paint value is a copy of the layer fill paint value
		assertThat( drawable.getFillPaint() ).isEqualTo( layerFillPaint );
		assertThat( drawable.calcFillPaint() ).isEqualTo( Paints.parse( layerFillPaint ) );

		// Change the layer fill paint to ensure that fill paint value is still the custom value
		layer.setFillPaint( Paints.toString( Color.WHITE ) );
		assertThat( drawable.getFillPaint() ).isEqualTo( layerFillPaint );
		assertThat( drawable.calcFillPaint() ).isEqualTo( Paints.parse( layerFillPaint ) );
	}

	@Test
	void testSetFillPaintWhenFillPaintModeIsLayer() {
		drawable.setFillPaint( Paints.toString( Color.RED ) );
		assertThat( drawable.getValueMode( drawable.getFillPaint() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getFillPaint() ).isEqualTo( Paints.toString( Color.RED ) );
		assertThat( drawable.calcFillPaint() ).isEqualTo( Color.RED );
	}

	@Test
	void testReorderLayerModifiesParent() {
		DesignLayer a = new DesignLayer();
		DesignLayer b = new DesignLayer();
		DesignLayer c = new DesignLayer();
		layer.addLayer( a );
		layer.addLayer( b );
		layer.addLayer( c );
		layer.setModified( false );
		assertThat( layer.isModified() ).isFalse();

		layer.removeLayer( c );
		layer.addLayerBeforeOrAfter( c, b, false );
		assertThat( layer.isModified() ).isTrue();
	}

}
