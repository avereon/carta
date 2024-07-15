package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.DesignLayer;
import lombok.CustomLog;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Getter
@CustomLog
public class DesignToolV2LayerUIT extends DesignToolV2BaseUIT {

	@Test
	void initialCurrentLayerNotNull() {
		// when
		DesignLayer firstLayer = getDesign().getAllLayers().getFirst();

		// then
		assertThat( getTool().getCurrentLayer() ).isEqualTo( firstLayer );
	}

	@Test
	void setCurrentLayer() {
		// given
		DesignLayer firstLayer = getDesign().getAllLayers().getFirst();
		DesignLayer secondLayer = getDesign().getAllLayers().get( 1 );
		assertThat( getTool().getCurrentLayer() ).isEqualTo( firstLayer );

		// when
		getTool().setCurrentLayer( secondLayer );

		// then
		assertThat( getTool().getCurrentLayer() ).isEqualTo( secondLayer );
	}

	@Test
	void getVisibleLayers() throws Exception {
		// given
		useLineLayer();
		useEllipseLayer();

		// then
		assertThat( getTool().getVisibleLayers().size() ).isEqualTo( 2 );
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
