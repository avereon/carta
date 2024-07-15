package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.DesignLayer;
import lombok.CustomLog;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Getter
@CustomLog
public class DesignToolV2LayerUIT extends DesignToolV2TestUIT {

	@Test
	void getCurrentLayer() {
		DesignLayer firstLayer = getDesign().getAllLayers().getFirst();
		assertThat( getTool().getCurrentLayer() ).isEqualTo( firstLayer );
	}

	// TODO Test set curren layer

	@Test
	void getVisibleLayers() throws Exception {
		useLineLayer();
		assertThat( getTool().getVisibleLayers().size() ).isEqualTo( 1 );
	}

	@Test
	void setLayerVisible() {
		// given
		DesignLayer layer = getDesign().getAllLayers().getFirst();
		assertThat( getTool().isLayerVisible( layer ) ).isFalse();
		assertThat( getTool().getVisibleLayers().size() ).isEqualTo( 0 );

		// when
		getTool().setLayerVisible( layer, true );

		// then
		assertThat( getTool().isLayerVisible( layer ) ).isTrue();
		assertThat( getTool().getVisibleLayers().size() ).isEqualTo( 1 );

		// when
		getTool().setLayerVisible( layer, false );

		// then
		assertThat( getTool().isLayerVisible( layer ) ).isFalse();
		assertThat( getTool().getVisibleLayers().size() ).isEqualTo( 0 );
	}

}
