package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.BaseToolTest;
import com.avereon.cartesia.test.Point3DAssert;
import com.avereon.cartesia.tool.Grid;
import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignToolV2Test extends BaseToolTest {

	private static final double DPC = FxRenderer2d.DEFAULT_DPI / 2.54;

	private DesignToolV2 tool;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		Fx.run( () -> tool = new DesignToolV2( module, resource ) );
		Fx.waitFor( 2, TimeUnit.SECONDS );
	}

	@Test
	void testScreenToWorkplaneWithCoordinates() {
		// given
		tool.setDpi( FxRenderer2d.DEFAULT_DPI );
		assertThat( tool.isGridSnapEnabled() ).isTrue();
		assertThat( tool.getWorkplane().calcSnapGridX() ).isEqualTo( 0.1 );
		assertThat( tool.getWorkplane().calcSnapGridY() ).isEqualTo( 0.1 );
		assertThat( tool.getWorkplane().getGridSystem() ).isEqualTo( Grid.ORTHO );

		// then
		Point3DAssert.assertThat( tool.screenToWorkplane( 2 * DPC, -2 * DPC, 0 ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.screenToWorkplane( 2.001 * DPC, -2.002 * DPC, 0 ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.screenToWorkplane( 2.101 * DPC + 0.1, -2.101 * DPC, 0 ) ).isEqualTo( new Point3D( 2.1, 2.1, 0 ) );
	}

	@Test
	void testScreenToWorkplaneWithPoints() {
		// given
		tool.setDpi( FxRenderer2d.DEFAULT_DPI );
		assertThat( tool.isGridSnapEnabled() ).isTrue();
		assertThat( tool.getWorkplane().calcSnapGridX() ).isEqualTo( 0.1 );
		assertThat( tool.getWorkplane().calcSnapGridY() ).isEqualTo( 0.1 );
		assertThat( tool.getWorkplane().getGridSystem() ).isEqualTo( Grid.ORTHO );

		// then
		Point3DAssert.assertThat( tool.screenToWorkplane( new Point3D( 2 * DPC, -2 * DPC, 0 ) ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.screenToWorkplane( new Point3D( 2.001 * DPC, -2.001 * DPC, 0 ) ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.screenToWorkplane( new Point3D( 2.101 * DPC + 0.1, -2.101 * DPC, 0 ) ) ).isEqualTo( new Point3D( 2.1, 2.1, 0 ) );
	}

	@Test
	void testSnapToGridWithCoordinates() {
		// given
		assertThat( tool.isGridSnapEnabled() ).isTrue();
		assertThat( tool.getWorkplane().calcSnapGridX() ).isEqualTo( 0.1 );
		assertThat( tool.getWorkplane().calcSnapGridY() ).isEqualTo( 0.1 );

		// then
		Point3DAssert.assertThat( tool.snapToGrid( 2, 2, 0 ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.snapToGrid( 2.01, 2.01, 0 ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.snapToGrid( 2.101, 2.101, 0 ) ).isEqualTo( new Point3D( 2.1, 2.1, 0 ) );
	}

	@Test
	void testSnapToGridWithPoints() {
		// given
		assertThat( tool.isGridSnapEnabled() ).isTrue();
		assertThat( tool.getWorkplane().calcSnapGridX() ).isEqualTo( 0.1 );
		assertThat( tool.getWorkplane().calcSnapGridY() ).isEqualTo( 0.1 );

		// then
		Point3DAssert.assertThat( tool.snapToGrid( new Point3D( 2, 2, 0 ) ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.snapToGrid( new Point3D( 2.01, 2.01, 0 ) ) ).isEqualTo( new Point3D( 2.0, 2.0, 0 ) );
		Point3DAssert.assertThat( tool.snapToGrid( new Point3D( 2.101, 2.101, 0 ) ) ).isEqualTo( new Point3D( 2.1, 2.1, 0 ) );
	}

}
