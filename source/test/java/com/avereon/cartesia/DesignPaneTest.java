package com.avereon.cartesia;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.Design2D;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class DesignPaneTest implements NumericTest {

	private static final boolean ZOOM_IN = true;

	private static final boolean ZOOM_OUT = false;

	private static final double SCALE = DesignUnit.INCH.from( DesignPane.DEFAULT_DPI, DesignUnit.CENTIMETER );

	private Design design;

	private DesignPane pane;

	@BeforeEach
	void setup() throws Exception {
		design = new Design2D();
		pane = new DesignPane( design );
		assertThat( pane.getScaleX(), is( 1.0 * SCALE ) );
		assertThat( pane.getScaleY(), is( -1.0 * SCALE ) );
		assertThat( pane.getTranslateX(), is( 0.0 ) );
		assertThat( pane.getTranslateY(), is( 0.0 ) );
	}

	@Test
	void testPan() throws Exception {
		pane.pan( new Point2D( pane.getTranslateX(), pane.getTranslateY() ), new Point2D( 1, 1 ), 2, 0.5 );
		assertThat( pane.getTranslateX(), is( 1.0 ) );
		assertThat( pane.getTranslateY(), is( -0.5 ) );
	}

	@Test
	void testPanOffsetOrigin() {
		pane.setTranslateX( -2 );
		pane.setTranslateY( 1 );
		pane.pan( new Point2D( pane.getTranslateX(), pane.getTranslateY() ), new Point2D( 1, 1 ), 2, 0.5 );
		assertThat( pane.getTranslateX(), is( -1.0 ) );
		assertThat( pane.getTranslateY(), is( 0.5 ) );
	}

	@Test
	void checkZoomFactor() {
		assertThat( 1 * DesignPane.ZOOM_IN_FACTOR, is( 1.189207115002721 ) );
	}

	@Test
	void testZoomIn() {
		pane.zoom( 0, 0, ZOOM_IN );
		assertThat( pane.getTranslateX(), is( 0.0 ) );
		assertThat( pane.getTranslateY(), is( 0.0 ) );
		assertThat( pane.getScaleX(), closeTo( 1.0 * SCALE * DesignPane.ZOOM_IN_FACTOR, TOLERANCE ) );
		assertThat( pane.getScaleY(), closeTo( -1.0 * SCALE * DesignPane.ZOOM_IN_FACTOR, TOLERANCE ) );
	}

	@Test
	void testZoomInOffsetOrigin() {
		double ex = -1;
		double ey = -1;
		double otx = 0;
		double oty = 0;
		double dx = otx - ex;
		double dy = oty - ey;
		double ntx = ex + (dx * DesignPane.ZOOM_IN_FACTOR);
		double nty = ey + (dy * DesignPane.ZOOM_IN_FACTOR);
		pane.zoom( ex, ey, ZOOM_IN );
		assertThat( pane.getTranslateX(), is( ntx ) );
		assertThat( pane.getTranslateY(), is( nty ) );
		assertThat( pane.getScaleX(), closeTo( 1.0 * SCALE * DesignPane.ZOOM_IN_FACTOR, TOLERANCE ) );
		assertThat( pane.getScaleY(), closeTo( -1.0 * SCALE * DesignPane.ZOOM_IN_FACTOR, TOLERANCE ) );
	}

	@Test
	void testZoomOut() {
		pane.zoom( 0, 0, ZOOM_OUT );
		assertThat( pane.getTranslateX(), is( 0.0 ) );
		assertThat( pane.getTranslateY(), is( 0.0 ) );
		assertThat( pane.getScaleX(), closeTo( 1.0 * SCALE / DesignPane.ZOOM_IN_FACTOR, TOLERANCE ) );
		assertThat( pane.getScaleY(), closeTo( -1.0 * SCALE / DesignPane.ZOOM_IN_FACTOR, TOLERANCE ) );
		closeTo( 1, 0 );
	}

	@Test
	void testZoomOutOffsetOrigin() {
		double ex = -1;
		double ey = -1;
		double otx = 0;
		double oty = 0;
		double dx = otx - ex;
		double dy = oty - ey;
		double ntx = ex + (dx / DesignPane.ZOOM_IN_FACTOR);
		double nty = ey + (dy / DesignPane.ZOOM_IN_FACTOR);
		pane.zoom( -1, -1, ZOOM_OUT );
		assertThat( pane.getTranslateX(), is( ntx ) );
		assertThat( pane.getTranslateY(), is( nty ) );
		assertThat( pane.getScaleX(), closeTo( 1.0 * SCALE / DesignPane.ZOOM_IN_FACTOR, TOLERANCE ) );
		assertThat( pane.getScaleY(), closeTo( -1.0 * SCALE / DesignPane.ZOOM_IN_FACTOR, TOLERANCE ) );
	}

	@Test
	void testChangeDesignUnitCausesRescale() {
		design.setDesignUnit( DesignUnit.MILLIMETER );
		double scale = DesignUnit.INCH.from( DesignPane.DEFAULT_DPI, design.getDesignUnit() );
		assertThat( pane.getScaleX(), closeTo( 1.0 * scale, TOLERANCE ) );
		assertThat( pane.getScaleY(), closeTo( -1.0 * scale, TOLERANCE ) );
	}

}
