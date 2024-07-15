package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.BaseCartesiaUnitTest;
import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.Design2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignRendererTest extends BaseCartesiaUnitTest {

	private Design design;

	private DesignRenderer renderer;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		design = new Design2D();
		renderer = new DesignRenderer();
		renderer.setDesign( design );
	}

	@Test
	void realToWorld() {
		// when
		double value = renderer.realToWorld( new DesignValue( 2, DesignUnit.MILLIMETER ) );

		// then
		assertThat( value ).isEqualTo( 0.2 );
	}

	@Test
	void realToScreen() {
		// when
		double value = renderer.realToScreen( new DesignValue( 2, DesignUnit.MILLIMETER ) );

		// then
		assertThat( value).isEqualTo( 5.669291338582677 );
	}

}
