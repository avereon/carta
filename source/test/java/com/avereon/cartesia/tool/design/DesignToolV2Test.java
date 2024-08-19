package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignToolBaseTest;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.test.Point3DAssert.assertThat;

public class DesignToolV2Test extends DesignToolBaseTest {

	private DesignToolV2 tool;

	@BeforeEach
	void beforeEach() {
		tool = new DesignToolV2( module, asset );
	}

	@Test
	void testScreenToWorkplane() {
		assertThat( tool.screenToWorkplane( 72, -72, 0 ) ).isEqualTo( new Point3D( 2.5, 2.5, 0 ) );
	}

}
