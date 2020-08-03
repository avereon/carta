package com.avereon.cartesia;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
		assertThat( design.getDesignUnit(), is( DesignUnit.CENTIMETER ) );
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
		assertThat( design.getLayers().size(), is( 0 ) );
		assertThat( design.isModified(), is( false ) );

		DesignLayer layer = new DesignLayer().setName( "mock-layer" );
		design.addLayer( layer );
		assertThat( design.getLayers().size(), is( 1 ) );
		assertThat( design.isModified(), is( true ) );

		design.removeLayer( layer );
		assertThat( design.getLayers().size(), is( 0 ) );
		assertThat( design.isModified(), is( false ) );
	}

	@Test
	void testAddLayerAndSave() {
		Design design = new MockDesign();
		design.setModified( false );
		assertThat( design.getLayers().size(), is( 0 ) );
		assertThat( design.isModified(), is( false ) );

		DesignLayer layer = new DesignLayer().setName( "mock-layer" );
		design.addLayer( layer );
		assertThat( design.getLayers().size(), is( 1 ) );
		assertThat( design.isModified(), is( true ) );

		design.setModified( false );
		assertThat( design.getLayers().size(), is( 1 ) );
		assertThat( design.isModified(), is( false ) );
	}

	private static class MockDesign extends Design {}

}
