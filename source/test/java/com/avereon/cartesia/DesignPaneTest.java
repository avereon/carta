package com.avereon.cartesia;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DesignPaneTest {

	private static final boolean ZOOM_IN = true;

	private static final boolean ZOOM_OUT = false;

	private DesignPane pane;

	@BeforeEach
	void setup() {
		pane = new DesignPane();
		assertThat( pane.getScaleX(), is( 1.0 ) );
		assertThat( pane.getScaleY(), is( -1.0 ) );
		assertThat( pane.getTranslateX(), is( 0.0 ) );
		assertThat( pane.getTranslateY(), is( 0.0 ) );
	}

	@Test
	void testPan() {
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
		assertThat( 1 * DesignPane.ZOOM_FACTOR, is( 1.189207115002721 ) );
	}

	@Test
	void testZoomIn() {
		pane.zoom( 0, 0, ZOOM_IN );
		assertThat( pane.getTranslateX(), is( 0.0 ) );
		assertThat( pane.getTranslateY(), is( 0.0 ) );
		assertThat( pane.getScaleX(), is( 1.0 * DesignPane.ZOOM_FACTOR ) );
		assertThat( pane.getScaleY(), is( -1.0 * DesignPane.ZOOM_FACTOR ) );
	}

	@Test
	void testZoomInOffsetOrigin() {
		double ex = -1;
		double ey = -1;
		double otx = 0;
		double oty = 0;
		double dx = otx - ex;
		double dy = oty - ey;
		double ntx = ex + (dx * DesignPane.ZOOM_FACTOR);
		double nty = ey + (dy * DesignPane.ZOOM_FACTOR);
		pane.zoom( ex, ey, ZOOM_IN );
		assertThat( pane.getTranslateX(), is( ntx ) );
		assertThat( pane.getTranslateY(), is( nty ) );
		assertThat( pane.getScaleX(), is( 1.0 * DesignPane.ZOOM_FACTOR ) );
		assertThat( pane.getScaleY(), is( -1.0 * DesignPane.ZOOM_FACTOR ) );
	}

	@Test
	void testZoomOut() {
		pane.zoom( 0, 0, ZOOM_OUT );
		assertThat( pane.getTranslateX(), is( 0.0 ) );
		assertThat( pane.getTranslateY(), is( 0.0 ) );
		assertThat( pane.getScaleX(), is( 1.0 / DesignPane.ZOOM_FACTOR ) );
		assertThat( pane.getScaleY(), is( -1.0 / DesignPane.ZOOM_FACTOR ) );
	}

	@Test
	void testZoomOutOffsetOrigin() {
		double ex = -1;
		double ey = -1;
		double otx = 0;
		double oty = 0;
		double dx = otx - ex;
		double dy = oty - ey;
		double ntx = ex + (dx / DesignPane.ZOOM_FACTOR);
		double nty = ey + (dy / DesignPane.ZOOM_FACTOR);
		pane.zoom( -1, -1, ZOOM_OUT );
		assertThat( pane.getTranslateX(), is( ntx ) );
		assertThat( pane.getTranslateY(), is( nty ) );
		assertThat( pane.getScaleX(), is( 1.0 / DesignPane.ZOOM_FACTOR ) );
		assertThat( pane.getScaleY(), is( -1.0 / DesignPane.ZOOM_FACTOR ) );
	}

}
