package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadShapes;
import com.avereon.zerra.color.Paints;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignLayerTest {

	private DesignLayer layer;

	@BeforeEach
	void setup() {
		layer = new DesignLayer();
	}

	@Test
	void testInitialValues() {
		// Initial values should generally be null, so they don't have to be saved
		assertThat( layer.getTextSize() ).isEqualTo( DesignLayer.DEFAULT_TEXT_SIZE );
		assertThat( layer.getTextFillPaint() ).isEqualTo( DesignLayer.DEFAULT_TEXT_FILL_PAINT );
		assertThat( layer.getTextDrawPaint() ).isEqualTo( DesignLayer.DEFAULT_TEXT_DRAW_PAINT );
		assertThat( layer.getTextDrawWidth() ).isEqualTo( DesignLayer.DEFAULT_TEXT_DRAW_WIDTH );
		assertThat( layer.getTextDrawCap() ).isEqualTo( DesignLayer.DEFAULT_TEXT_DRAW_CAP );
		assertThat( layer.getTextDrawPattern() ).isEqualTo( DesignLayer.DEFAULT_TEXT_DRAW_PATTERN );

		assertThat( layer.getFontName() ).isEqualTo( DesignLayer.DEFAULT_FONT_NAME );
		assertThat( layer.getFontWeight() ).isEqualTo( DesignLayer.DEFAULT_FONT_WEIGHT );
		assertThat( layer.getFontPosture() ).isEqualTo( DesignLayer.DEFAULT_FONT_POSTURE );
		assertThat( layer.getFontUnderline() ).isEqualTo( DesignLayer.DEFAULT_FONT_UNDERLINE );
		assertThat( layer.getFontStrikethrough() ).isEqualTo( DesignLayer.DEFAULT_FONT_STRIKETHROUGH );
	}

	@Test
	void testCalcWithInitialValues() {
		// Calculated values should never be null and use the default values for initial values
		assertThat( layer.calcTextSize() ).isEqualTo( Double.parseDouble( DesignLayer.DEFAULT_TEXT_SIZE ) );
		assertThat( layer.calcDrawPaint() ).isEqualTo( Paints.parse( DesignLayer.DEFAULT_DRAW_PAINT ) );
		assertThat( layer.calcDrawWidth() ).isEqualTo( Double.parseDouble( DesignLayer.DEFAULT_DRAW_WIDTH ) );
		assertThat( layer.calcDrawPattern() ).containsExactlyElementsOf( CadShapes.parseDashPattern( DesignLayer.DEFAULT_DRAW_PATTERN ) );
		assertThat( layer.calcDrawCap() ).isEqualTo( StrokeLineCap.valueOf( DesignLayer.DEFAULT_DRAW_CAP.toUpperCase() ) );
		assertThat( layer.calcFillPaint() ).isEqualTo( Paints.parse( DesignLayer.DEFAULT_FILL_PAINT ) );

		assertThat( layer.calcFontName() ).isEqualTo( DesignLayer.DEFAULT_FONT_NAME );
		assertThat( layer.calcFontWeight() ).isEqualTo( FontWeight.valueOf( DesignLayer.DEFAULT_FONT_WEIGHT.toUpperCase() ) );
		assertThat( layer.calcFontPosture() ).isEqualTo( FontPosture.valueOf( DesignLayer.DEFAULT_FONT_POSTURE.toUpperCase() ) );
		assertThat( layer.calcFontUnderline() ).isEqualTo( Boolean.parseBoolean( DesignLayer.DEFAULT_FONT_UNDERLINE ) );
		assertThat( layer.calcFontStrikethrough() ).isEqualTo( Boolean.parseBoolean( DesignLayer.DEFAULT_FONT_STRIKETHROUGH ) );
	}

	@Test
	void testModify() {
		assertThat( layer.isModified() ).isTrue();
		layer.setModified( false );
		assertThat( layer.isModified() ).isFalse();
	}

}
