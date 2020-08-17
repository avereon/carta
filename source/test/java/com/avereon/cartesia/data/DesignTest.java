package com.avereon.cartesia.data;

import com.avereon.cartesia.DesignUnit;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
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

		DesignLayer layer = new DesignLayer().setName( "mock-layer" );
		design.getRootLayer().addLayer( layer );
		assertThat( design.getRootLayer().getLayers().size(), is( 1 ) );
		assertThat( design.isModified(), is( true ) );

		design.getRootLayer().removeLayer( layer );
		assertThat( design.getRootLayer().getLayers().size(), is( 0 ) );
		assertThat( design.isModified(), is( false ) );
	}

	@Test
	void testAddLayerAndSave() {
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

	private static class MockDesign extends Design {}

}
