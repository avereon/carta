package com.avereon.cartesia.data;

import com.avereon.cartesia.DesignUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignTest {

	@Test
	void testNameModifiesDesign() {
		Design design = new MockDesign();
		design.setModified( false );
		assertThat( design.getName() ).isNull();
		assertThat( design.isModified() ).isFalse();

		design.setName( "Mock Design Alpha" );
		assertThat( design.isModified() ).isTrue();
		design.setModified( false );
		assertThat( design.isModified() ).isFalse();

		design.setName( "Mock Design Final" );
		assertThat( design.isModified() ).isTrue();
		design.setModified( false );
		assertThat( design.isModified() ).isFalse();
	}

	@Test
	void testDesignUnitModifiesDesign() {
		Design design = new MockDesign();
		design.setModified( false );
		assertThat( design.calcDesignUnit() ).isEqualTo( DesignUnit.CM );
		assertThat( design.isModified() ).isFalse();

		design.setDesignUnit( DesignUnit.MM );
		assertThat( design.isModified() ).isTrue();
		design.setModified( false );
		assertThat( design.isModified() ).isFalse();

		design.setDesignUnit( DesignUnit.IN );
		assertThat( design.isModified() ).isTrue();
		design.setModified( false );
		assertThat( design.isModified() ).isFalse();
	}

	@Test
	void testAddRemoveLayer() {
		Design design = new MockDesign();
		design.setModified( false );
		assertThat( design.getLayers().getLayers().size() ).isEqualTo( 0 );
		assertThat( design.isModified() ).isFalse();

		DesignLayer layer = new DesignLayer().setName( "test-layer" );
		design.getLayers().addLayer( layer );
		assertThat( design.getLayers().getLayers().size() ).isEqualTo( 1 );
		assertThat( design.isModified() ).isTrue();

		design.getLayers().removeLayer( layer );
		assertThat( design.getLayers().getLayers().size() ).isEqualTo( 0 );
		assertThat( design.isModifiedBySelf() ).isFalse();
		assertThat( design.isModifiedByValue() ).isFalse();
		assertThat( design.isModifiedByChild() ).isFalse();
		assertThat( design.isModified() ).isFalse();
	}

	@Test
	void testAddLayerAndClearModified() {
		Design design = new MockDesign();
		design.setModified( false );
		assertThat( design.getLayers().getLayers().size() ).isEqualTo( 0 );
		assertThat( design.isModified() ).isFalse();

		DesignLayer layer = new DesignLayer().setName( "mock-layer" );
		design.getLayers().addLayer( layer );
		assertThat( design.getLayers().getLayers().size() ).isEqualTo( 1 );
		assertThat( design.isModified() ).isTrue();

		design.setModified( false );
		assertThat( design.getLayers().getLayers().size() ).isEqualTo( 1 );
		assertThat( design.isModified() ).isFalse();
	}

	@Test
	void testGetAllLayers() {
		Design design = new MockDesign();

		// Using names that are in reverse order helps ensure the nodes are not ordered by name
		DesignLayer layer0 = new DesignLayer().setName( "layer-f" ).setOrder( 0 );
		DesignLayer layer1 = new DesignLayer().setName( "layer-e" ).setOrder( 1 );
		DesignLayer layer00 = new DesignLayer().setName( "layer-d" ).setOrder( 0 );
		DesignLayer layer01 = new DesignLayer().setName( "layer-c" ).setOrder( 1 );
		DesignLayer layer10 = new DesignLayer().setName( "layer-b" ).setOrder( 0 );
		DesignLayer layer11 = new DesignLayer().setName( "layer-a" ).setOrder( 1 );

		design.getLayers().addLayer( layer0 );
		design.getLayers().addLayer( layer1 );
		layer0.addLayer( layer00 );
		layer0.addLayer( layer01 );
		layer1.addLayer( layer10 );
		layer1.addLayer( layer11 );

		assertThat( design.getAllLayers() ).contains( layer0, layer00, layer01, layer1, layer10, layer11 );
	}

	private static class MockDesign extends Design {}

}
