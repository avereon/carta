package com.avereon.cartesia.data.util;

import com.avereon.cartesia.BaseCartesiaUiTest;
import com.avereon.cartesia.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DesignPropertiesMapUIT extends BaseCartesiaUiTest {

	private DesignPropertiesMap designPropertiesMap;

	@BeforeEach
	void beforeEach() {
		designPropertiesMap = new DesignPropertiesMap( getMod() );
		assertThat( designPropertiesMap ).isNotNull();
	}

	@Test
	void testGetSettingsPage() {
		// Layer properties page
		assertThat( designPropertiesMap.getSettingsPage( DesignLayer.class ) ).isNotNull();

		// Common shape properties page
		assertThat( designPropertiesMap.getSettingsPage( DesignShape.class ) ).isNotNull();

		// Specific shape properties pages
		assertThat( designPropertiesMap.getSettingsPage( DesignBox.class ) ).isNotNull();
		assertThat( designPropertiesMap.getSettingsPage( DesignLine.class ) ).isNotNull();
		assertThat( designPropertiesMap.getSettingsPage( DesignEllipse.class ) ).isNotNull();
		assertThat( designPropertiesMap.getSettingsPage( DesignArc.class ) ).isNotNull();
		assertThat( designPropertiesMap.getSettingsPage( DesignQuad.class ) ).isNotNull();
		assertThat( designPropertiesMap.getSettingsPage( DesignCubic.class ) ).isNotNull();
		assertThat( designPropertiesMap.getSettingsPage( DesignPath.class ) ).isNotNull();
		assertThat( designPropertiesMap.getSettingsPage( DesignMarker.class ) ).isNotNull();
		assertThat( designPropertiesMap.getSettingsPage( DesignText.class ) ).isNotNull();
	}

}
