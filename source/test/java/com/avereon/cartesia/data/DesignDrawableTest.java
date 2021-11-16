package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.zarra.color.Paints;
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
	}

	@Test
	void testLayerCalcDefaults() {
		assertThat( layer.calcDrawPaint() ).isEqualTo( Paints.parse( DesignLayer.DEFAULT_DRAW_PAINT ) );
		assertThat( layer.calcDrawWidth() ).isEqualTo( CadMath.evalNoException( DesignLayer.DEFAULT_DRAW_WIDTH ) );
		assertThat( layer.calcDrawCap() ).isEqualTo( StrokeLineCap.valueOf( DesignLayer.DEFAULT_DRAW_CAP.toUpperCase() ) );
		assertThat( layer.calcDrawPattern() ).isEqualTo( CadShapes.parseDashPattern( DesignLayer.DEFAULT_DRAW_PATTERN ) );
		assertThat( layer.calcFillPaint() ).isEqualTo( Paints.parse( DesignLayer.DEFAULT_FILL_PAINT ) );
	}

	@Test
	void testChangeDrawPaintModeFromDefaultToCustom() {
		// Change mode to custom to copy current calcDrawPaint value
		drawable.changeDrawPaintMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawPaint() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		// Check that the cap value is a copy of the layer cap value
		assertThat( drawable.getDrawPaint() ).isEqualTo( Paints.toString( Color.BLACK ) );
		assertThat( drawable.calcDrawPaint() ).isEqualTo( Color.BLACK );

		// Change the layer cap to ensure that cap values are still the custom value
		layer.setDrawPaint( Paints.toString( Color.WHITE ) );
		assertThat( drawable.getDrawPaint() ).isEqualTo( Paints.toString( Color.BLACK ) );
		assertThat( drawable.calcDrawPaint() ).isEqualTo( Color.BLACK );
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
		// Change mode to custom to copy current calcDrawWidth value
		drawable.changeDrawWidthMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawWidth() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		// Check that the cap value is a copy of the layer cap value
		assertThat( drawable.getDrawWidth() ).isEqualTo( DesignLayer.DEFAULT_DRAW_WIDTH );
		assertThat( drawable.calcDrawWidth() ).isEqualTo( CadMath.evalNoException( DesignLayer.DEFAULT_DRAW_WIDTH ) );

		// Change the layer cap to ensure that cap values are still the custom value
		layer.setDrawWidth( String.valueOf( 1.0 ) );
		assertThat( drawable.getDrawWidth() ).isEqualTo( DesignLayer.DEFAULT_DRAW_WIDTH );
		assertThat( drawable.calcDrawWidth() ).isEqualTo( CadMath.evalNoException( DesignLayer.DEFAULT_DRAW_WIDTH ) );
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
		// Change mode to custom to copy current calcDrawCap value
		drawable.changeDrawCapMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawCap() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		// Check that the cap value is a copy of the layer cap value
		assertThat( drawable.getDrawCap() ).isEqualTo( DesignLayer.DEFAULT_DRAW_CAP );
		assertThat( drawable.calcDrawCap() ).isEqualTo( StrokeLineCap.valueOf( DesignLayer.DEFAULT_DRAW_CAP.toUpperCase() ) );

		// Change the layer cap to ensure that cap values are still the custom value
		layer.setDrawCap( StrokeLineCap.ROUND.name().toLowerCase() );
		assertThat( drawable.getDrawCap() ).isEqualTo( DesignLayer.DEFAULT_DRAW_CAP );
		assertThat( drawable.calcDrawCap() ).isEqualTo( StrokeLineCap.valueOf( DesignLayer.DEFAULT_DRAW_CAP.toUpperCase() ) );
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
		// Change mode to custom to copy current calcDrawPattern value
		drawable.changeDrawPatternMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getDrawPattern() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		// Check that the layer values are a copy of the layer pattern values
		assertThat( drawable.getDrawPattern() ).isEqualTo( DesignLayer.DEFAULT_DRAW_PATTERN );
		assertThat( drawable.calcDrawPattern() ).isEqualTo( CadShapes.parseDashPattern( DesignLayer.DEFAULT_DRAW_PATTERN ) );

		// Change the layer cap to ensure that cap values are still the custom value
		layer.setDrawPattern( "1/8,1/4" );
		assertThat( drawable.getDrawPattern() ).isEqualTo( DesignLayer.DEFAULT_DRAW_PATTERN );
		assertThat( drawable.calcDrawPattern() ).isEqualTo( CadShapes.parseDashPattern( DesignLayer.DEFAULT_DRAW_PATTERN ) );
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
		// Change mode to custom to copy current calcFillPaint value
		drawable.changeFillPaintMode( DesignDrawable.MODE_CUSTOM );
		assertThat( drawable.getValueMode( drawable.getFillPaint() ) ).isEqualTo( DesignDrawable.MODE_CUSTOM );
		// Check that the cap value is a copy of the layer cap value
		assertThat( drawable.getFillPaint() ).isEqualTo( DesignLayer.DEFAULT_FILL_PAINT );
		assertThat( drawable.calcFillPaint() ).isEqualTo( Paints.parse( DesignLayer.DEFAULT_FILL_PAINT ) );

		// Change the layer cap to ensure that cap values are still the custom value
		layer.setFillPaint( Paints.toString( Color.WHITE ) );
		assertThat( drawable.getFillPaint() ).isEqualTo( DesignLayer.DEFAULT_FILL_PAINT );
		assertThat( drawable.calcFillPaint() ).isEqualTo( Paints.parse( DesignLayer.DEFAULT_FILL_PAINT ) );
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
