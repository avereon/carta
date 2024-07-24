package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.BaseCartesiaUnitTest;
import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.Design2D;
import com.avereon.cartesia.test.Point3DAssert;
import com.avereon.zarra.javafx.Fx;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class DesignRendererTest extends BaseCartesiaUnitTest {

	private Design design;

	private DesignRenderer renderer;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		Fx.startup();
		design = new Design2D();
		renderer = new DesignRenderer();
		renderer.setDesign( design );
		renderer.resizeRelocate( 0, 0, 100, 100 );
	}

	@Test
	void parentToLocal() {
		double oneCentimeterPerInch = new DesignValue( 1, DesignUnit.CENTIMETER ).to( DesignUnit.INCH ).getValue();
		double pixelsPerCentimeter = renderer.getDpiX() * oneCentimeterPerInch;

		// Check the center
		Point3DAssert.assertThat( renderer.parentToLocal( new Point3D( 50, 50, 0 ) ) ).isCloseTo( new Point3D( 0, 0, 0 ), TOLERANCE );

		// Check the x-axis
		Point3DAssert.assertThat( renderer.parentToLocal( new Point3D( 50 + pixelsPerCentimeter, 50, 0 ) ) ).isCloseTo( new Point3D( 1, 0, 0 ), TOLERANCE );
		Point3DAssert.assertThat( renderer.parentToLocal( new Point3D( 50 - pixelsPerCentimeter, 50, 0 ) ) ).isCloseTo( new Point3D( -1, 0, 0 ), TOLERANCE );

		// Check the y-axis. Remember the Y axis is inverted
		Point3DAssert.assertThat( renderer.parentToLocal( new Point3D( 50, 50 - pixelsPerCentimeter, 0 ) ) ).isCloseTo( new Point3D( 0, 1, 0 ), TOLERANCE );
		Point3DAssert.assertThat( renderer.parentToLocal( new Point3D( 50, 50 + pixelsPerCentimeter, 0 ) ) ).isCloseTo( new Point3D( 0, -1, 0 ), TOLERANCE );
	}

	@Test
	void localToParent() {
		double oneCentimeterPerInch = new DesignValue( 1, DesignUnit.CENTIMETER ).to( DesignUnit.INCH ).getValue();
		double pixelsPerCentimeter = renderer.getDpiX() * oneCentimeterPerInch;

		// Check the center
		Point3DAssert.assertThat( renderer.localToParent( new Point3D( 0, 0, 0 ) ) ).isCloseTo( new Point3D( 50, 50, 0 ), TOLERANCE );

		// Check the x-axis
		Point3DAssert.assertThat( renderer.localToParent( new Point3D( 1, 0, 0 ) ) ).isCloseTo( new Point3D( 50 + pixelsPerCentimeter, 50, 0 ), TOLERANCE );
		Point3DAssert.assertThat( renderer.localToParent( new Point3D( -1, 0, 0 ) ) ).isCloseTo( new Point3D( 50 - pixelsPerCentimeter, 50, 0 ), TOLERANCE );

		// Check the y-axis. Remember the Y axis is inverted
		Point3DAssert.assertThat( renderer.localToParent( new Point3D( 0, 1, 0 ) ) ).isCloseTo( new Point3D( 50, 50 - pixelsPerCentimeter, 0 ), TOLERANCE );
		Point3DAssert.assertThat( renderer.localToParent( new Point3D( 0, -1, 0 ) ) ).isCloseTo( new Point3D( 50, 50 + pixelsPerCentimeter, 0 ), TOLERANCE );
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
		double oneMillimeterPerInch = new DesignValue( 1, DesignUnit.MILLIMETER ).to( DesignUnit.INCH ).getValue();
		double pixelsPerMillimeter = renderer.getDpiX() * oneMillimeterPerInch;

		// when
		double value = renderer.realToScreen( new DesignValue( 2, DesignUnit.MILLIMETER ) );

		// then
		assertThat( value ).isEqualTo( 2 * pixelsPerMillimeter );
	}

}
