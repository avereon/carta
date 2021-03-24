package com.avereon.cartesia.data;

import com.avereon.cartesia.DesignUnit;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DesignTest {

	@Test
	void testNameModifiesDesign() {
		Design design = new MockDesign();
		design.setModified( false );
		assertNull( design.getName() );
		assertThat( design.isModified(), is( false ) );

		design.setName( "Mock Design Alpha" );
		assertThat( design.isModified(), is( true ) );
		design.setModified( false );
		assertThat( design.isModified(), is( false ) );

		design.setName( "Mock Design Final" );
		assertThat( design.isModified(), is( true ) );
		design.setModified( false );
		assertThat( design.isModified(), is( false ) );
	}

	@Test
	void testDesignUnitModifiesDesign() {
		Design design = new MockDesign();
		design.setModified( false );
		assertThat( design.getDesignUnit(), Matchers.is( DesignUnit.CENTIMETER ) );
		assertThat( design.isModified(), is( false ) );

		design.setDesignUnit( DesignUnit.MILLIMETER );
		assertThat( design.isModified(), is( true ) );
		design.setModified( false );
		assertThat( design.isModified(), is( false ) );

		design.setDesignUnit( DesignUnit.INCH );
		assertThat( design.isModified(), is( true ) );
		design.setModified( false );
		assertThat( design.isModified(), is( false ) );
	}

	@Test
	void testAddRemoveLayer() {
		Design design = new MockDesign();
		design.setModified( false );
		assertThat( design.getRootLayer().getLayers().size(), is( 0 ) );
		assertThat( design.isModified(), is( false ) );

		DesignLayer layer = new DesignLayer().setName( "test-layer" );
		design.getRootLayer().addLayer( layer );
		assertThat( design.getRootLayer().getLayers().size(), is( 1 ) );
		assertThat( design.isModified(), is( true ) );

		design.getRootLayer().removeLayer( layer );
		assertThat( design.getRootLayer().getLayers().size(), is( 0 ) );
		assertThat( design.isModifiedBySelf(), is( false ) );
		assertThat( design.isModifiedByValue(), is( false ) );
		assertThat( design.isModifiedByChild(), is( false ) );
		assertThat( design.isModified(), is( false ) );
	}

	@Test
	void testAddLayerAndClearModified() {
		Design design = new MockDesign();
		design.setModified( false );
		assertThat( design.getRootLayer().getLayers().size(), is( 0 ) );
		assertThat( design.isModified(), is( false ) );

		DesignLayer layer = new DesignLayer().setName( "mock-layer" );
		design.getRootLayer().addLayer( layer );
		assertThat( design.getRootLayer().getLayers().size(), is( 1 ) );
		assertThat( design.isModified(), is( true ) );

		design.setModified( false );
		assertThat( design.getRootLayer().getLayers().size(), is( 1 ) );
		assertThat( design.isModified(), is( false ) );
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

		design.getRootLayer().addLayer( layer0 );
		design.getRootLayer().addLayer( layer1 );
		layer0.addLayer( layer00 );
		layer0.addLayer( layer01 );
		layer1.addLayer( layer10 );
		layer1.addLayer( layer11 );

		assertThat( design.getAllLayers(), contains( layer0, layer00, layer01, layer1, layer10, layer11));
	}

	private static class MockDesign extends Design {}

}
