package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadShapes;
import com.avereon.data.NodeEvent;
import com.avereon.zerra.color.Paints;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

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
		assertThat( layer.getTextDrawPattern() ).isEqualTo( DesignLayer.DEFAULT_TEXT_DASH_PATTERN );

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
		assertThat( layer.calcDashPattern() ).containsExactlyElementsOf( CadShapes.parseDashPattern( DesignLayer.DEFAULT_DASH_PATTERN ) );
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

	@Test
	void testAddLayerEvent() {
		// given
		DesignLayer child = new DesignLayer();
		AtomicInteger count = new AtomicInteger( 0 );
		layer.register( NodeEvent.CHILD_ADDED, _ -> count.incrementAndGet() );

		// when
		layer.addLayer( child );
		// then
		assertThat( count.get() ).isEqualTo( 1 );

		// when
		child.addLayer( new DesignLayer() );
		// then
		assertThat( count.get() ).isEqualTo( 2 );
	}

	@Test
	void testRemoveLayerEvent() {
		DesignLayer child = new DesignLayer();
		DesignLayer grandchild = new DesignLayer();
		AtomicInteger count = new AtomicInteger( 0 );
		layer.addLayer( child );
		child.addLayer( grandchild );
		layer.register( NodeEvent.CHILD_REMOVED, _ -> count.incrementAndGet() );

		// when
		child.removeLayer( grandchild );
		// then
		assertThat( count.get() ).isEqualTo( 1 );

		// when
		layer.removeLayer( child );
		// then
		assertThat( count.get() ).isEqualTo( 2 );
	}

}
