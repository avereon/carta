package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignToolBaseTest;
import com.avereon.cartesia.test.Point3DAssert;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignToolV2Test extends DesignToolBaseTest {

	private static final double DPC = 72 / 2.54;

	private DesignToolV2 tool;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		tool = new DesignToolV2( module, asset );
	}

	@Test
	void testScreenToWorkplaneWithCoordinates() {
		// given
		assertThat( tool.isGridSnapEnabled() ).isTrue();
		assertThat( tool.getWorkplane().calcSnapGridX() ).isEqualTo( 0.1 );
		assertThat( tool.getWorkplane().calcSnapGridY() ).isEqualTo( 0.1 );

		// then
		Point3DAssert.assertThat( tool.screenToWorkplane( 2 * DPC, -2 * DPC, 0 ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.screenToWorkplane( 2.001 * DPC, -2.002 * DPC, 0 ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.screenToWorkplane( 2.101 * DPC + 0.1, -2.101 * DPC, 0 ) ).isEqualTo( new Point3D( 2.1, 2.1, 0 ) );
	}

	@Test
	void testScreenToWorkplaneWithPoints() {
		// given
		assertThat( tool.isGridSnapEnabled() ).isTrue();
		assertThat( tool.getWorkplane().calcSnapGridX() ).isEqualTo( 0.1 );
		assertThat( tool.getWorkplane().calcSnapGridY() ).isEqualTo( 0.1 );

		// then
		Point3DAssert.assertThat( tool.screenToWorkplane( new Point3D( 2 * DPC, -2 * DPC, 0 ) ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.screenToWorkplane( new Point3D( 2.001 * DPC, -2.001 * DPC, 0 ) ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.screenToWorkplane( new Point3D( 2.101 * DPC + 0.1, -2.101 * DPC, 0 ) ) ).isEqualTo( new Point3D( 2.1, 2.1, 0 ) );
	}

	@Test
	void testSnapToWorkplaneWithCoordinates() {
		// given
		assertThat( tool.isGridSnapEnabled() ).isTrue();
		assertThat( tool.getWorkplane().calcSnapGridX() ).isEqualTo( 0.1 );
		assertThat( tool.getWorkplane().calcSnapGridY() ).isEqualTo( 0.1 );

		// then
		Point3DAssert.assertThat( tool.snapToWorkplane( 2, 2, 0 ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.snapToWorkplane( 2.01, 2.01, 0 ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.snapToWorkplane( 2.101, 2.101, 0 ) ).isEqualTo( new Point3D( 2.1, 2.1, 0 ) );
	}

	@Test
	void testSnapToWorkplaneWithPoints() {
		// given
		assertThat( tool.isGridSnapEnabled() ).isTrue();
		assertThat( tool.getWorkplane().calcSnapGridX() ).isEqualTo( 0.1 );
		assertThat( tool.getWorkplane().calcSnapGridY() ).isEqualTo( 0.1 );

		// then
		Point3DAssert.assertThat( tool.snapToWorkplane( new Point3D( 2, 2, 0 ) ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.snapToWorkplane( new Point3D( 2.01, 2.01, 0 ) ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.snapToWorkplane( new Point3D( 2.101, 2.101, 0 ) ) ).isEqualTo( new Point3D( 2.1, 2.1, 0 ) );
	}

}