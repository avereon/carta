package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.BaseCartesiaUnitTest;
import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.Design2D;
import com.avereon.cartesia.test.Point3DAssert;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.zerra.color.Colors;
import com.avereon.zerra.javafx.Fx;
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
	void getApertureDrawPaint() {
		// given
		assertThat( renderer.getApertureDrawPaint() ).isEqualTo( "#ffff00cc" );

		// when
		renderer.setApertureDrawPaint( DesignTool.DEFAULT_APERTURE_DRAW );

		// then
		assertThat( renderer.getApertureDrawPaint() ).isEqualTo( DesignTool.DEFAULT_APERTURE_DRAW );
		assertThat( renderer.calcApertureDrawPaint() ).isEqualTo( Colors.parse( DesignTool.DEFAULT_APERTURE_DRAW ) );
	}

	@Test
	void getApertureFillPaint() {
		// given
		assertThat( renderer.getApertureFillPaint() ).isEqualTo( "#ffff0033" );

		// when
		renderer.setApertureFillPaint( DesignTool.DEFAULT_APERTURE_FILL );

		// then
		assertThat( renderer.getApertureFillPaint() ).isEqualTo( DesignTool.DEFAULT_APERTURE_FILL );
		assertThat( renderer.calcApertureFillPaint() ).isEqualTo( Colors.parse( DesignTool.DEFAULT_APERTURE_FILL ) );
	}

	@Test
	void getPreviewDrawPaint() {
		// given
		assertThat( renderer.getPreviewDrawPaint() ).isEqualTo( "#ff00ffcc" );

		// when
		renderer.setPreviewDrawPaint( DesignTool.DEFAULT_PREVIEW_DRAW );

		// then
		assertThat( renderer.getPreviewDrawPaint() ).isEqualTo( DesignTool.DEFAULT_PREVIEW_DRAW );
		assertThat( renderer.calcPreviewDrawPaint() ).isEqualTo( Colors.parse( DesignTool.DEFAULT_PREVIEW_DRAW ) );
	}

	@Test
	void getPreviewFillPaint() {
		// given
		assertThat( renderer.getPreviewFillPaint() ).isEqualTo( "#ff00ff33" );

		// when
		renderer.setPreviewFillPaint( DesignTool.DEFAULT_PREVIEW_FILL );

		// then
		assertThat( renderer.getPreviewFillPaint() ).isEqualTo( DesignTool.DEFAULT_PREVIEW_FILL );
		assertThat( renderer.calcPreviewFillPaint() ).isEqualTo( Colors.parse( DesignTool.DEFAULT_PREVIEW_FILL ) );
	}

	@Test
	void getSelectDrawPaint() {
		// given
		assertThat( renderer.getSelectedDrawPaint() ).isEqualTo( "#ff00ffcc" );

		// when
		renderer.setSelectedDrawPaint( DesignTool.DEFAULT_SELECTED_DRAW );

		// then
		assertThat( renderer.getSelectedDrawPaint() ).isEqualTo( DesignTool.DEFAULT_SELECTED_DRAW );
		assertThat( renderer.calcSelectedDrawPaint() ).isEqualTo( Colors.parse( DesignTool.DEFAULT_SELECTED_DRAW ) );
	}

	@Test
	void getSelectFillPaint() {
		// given
		assertThat( renderer.getSelectedFillPaint() ).isEqualTo( "#ff00ff33" );

		// when
		renderer.setSelectedFillPaint( DesignTool.DEFAULT_SELECTED_FILL );

		// then
		assertThat( renderer.getSelectedFillPaint() ).isEqualTo( DesignTool.DEFAULT_SELECTED_FILL );
		assertThat( renderer.calcSelectedFillPaint() ).isEqualTo( Colors.parse( DesignTool.DEFAULT_SELECTED_FILL ) );
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
