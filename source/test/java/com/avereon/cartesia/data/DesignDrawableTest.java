package com.avereon.cartesia.data;

import javafx.scene.shape.StrokeLineCap;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DesignDrawableTest {

	@Test
	void testGetValueMode() {
		DesignDrawable drawable = new DesignPoint();

		assertThat( drawable.getValueMode( null ), is( DesignDrawable.MODE_LAYER ) );
		assertThat( drawable.getValueMode( DesignDrawable.MODE_LAYER ), is( DesignDrawable.MODE_LAYER ) );
		assertThat( drawable.getValueMode( DesignDrawable.MODE_NONE ), is( DesignDrawable.MODE_NONE ) );
		assertThat( drawable.getValueMode( "" ), is( DesignDrawable.MODE_CUSTOM ) );
	}

	@Test
	void testChangeDrawCapModeFromDefaultToCustom() {
		DesignDrawable drawable = new DesignPoint();
		DesignLayer layer = new DesignLayer();
		layer.addDrawable( drawable );

		// Check the default values
		assertThat( drawable.getDrawCap(), is( DesignDrawable.MODE_LAYER ) );
		assertThat( drawable.calcDrawCap(), is( StrokeLineCap.SQUARE ) );

		// Change mode to custom to copy current calcDrawCap value
		DesignDrawable.changeDrawCapMode( drawable, DesignDrawable.MODE_CUSTOM );
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
		DesignDrawable drawable = new DesignPoint();
		DesignLayer layer = new DesignLayer();
		layer.addDrawable( drawable );

		// Check the default values
		assertThat( drawable.getDrawCap(), is( DesignDrawable.MODE_LAYER ) );
		assertThat( drawable.calcDrawCap(), is( StrokeLineCap.SQUARE ) );

		// Change cap value
		layer.setDrawCap( StrokeLineCap.SQUARE.name().toLowerCase() );
		drawable.setDrawCap( StrokeLineCap.ROUND.name().toLowerCase() );
		assertThat( drawable.getValueMode( drawable.getDrawCap() ), is( DesignDrawable.MODE_CUSTOM ) );
		assertThat( drawable.getDrawCap(), is( StrokeLineCap.ROUND.name().toLowerCase() ) );
		assertThat( drawable.calcDrawCap(), is( StrokeLineCap.ROUND ) );
	}

}
