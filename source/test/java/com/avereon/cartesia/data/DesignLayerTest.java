package com.avereon.cartesia.data;

import com.avereon.zarra.font.FontUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignLayerTest {

	@Test
	void testDefaults() {
		DesignLayer layer = new DesignLayer();
		assertThat( layer.getTextFont() ).isEqualTo( DesignLayer.DEFAULT_TEXT_FONT );
		assertThat( layer.calcTextFont() ).isEqualTo( FontUtil.decode( DesignLayer.DEFAULT_TEXT_FONT ) );
	}

	@Test
	void testModify() {
		DesignLayer layer = new DesignLayer();
		assertThat( layer.isModified() ).isTrue();
		layer.setModified( false );
		assertThat( layer.isModified() ).isFalse();
	}

}
