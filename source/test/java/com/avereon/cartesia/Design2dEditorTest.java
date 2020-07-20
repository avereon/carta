package com.avereon.cartesia;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class Design2dEditorTest {

	private static final double ZOOM_IN = 20;

	private static final double ZOOM_OUT = -ZOOM_IN;

	private Node geometry;

	@BeforeEach
	void setup() {
		geometry = new Pane();
		assertThat( geometry.getScaleX(), is( 1.0 ) );
		assertThat( geometry.getScaleY(), is( 1.0 ) );
		assertThat( geometry.getTranslateX(), is( 0.0 ) );
		assertThat( geometry.getTranslateY(), is( 0.0 ) );
	}

	@Test
	void checkScaleFactor() {
		assertThat( 1 * DesignTool.ZOOM_FACTOR, is( 1.189207115002721 ) );
	}

	@Test
	void testZoomIn() {
		DesignTool.zoom( geometry, ZOOM_IN, 0, 0, 0 );
		assertThat( geometry.getScaleX(), is( 1.0 * DesignTool.ZOOM_FACTOR ) );
		assertThat( geometry.getScaleY(), is( 1.0 * DesignTool.ZOOM_FACTOR ) );
		assertThat( geometry.getTranslateX(), is( 0.0 ) );
		assertThat( geometry.getTranslateY(), is( 0.0 ) );
	}

	@Test
	void testZoomInOffsetOrigin() {
		double ex = -1;
		double ey = -1;
		double otx = 0;
		double oty = 0;
		double dx = otx - ex;
		double dy = oty - ey;
		double ntx = ex + (dx * DesignTool.ZOOM_FACTOR);
		double nty = ey + (dy * DesignTool.ZOOM_FACTOR);
		DesignTool.zoom( geometry, ZOOM_IN, ex, ey, 0 );
		assertThat( geometry.getTranslateX(), is( ntx ) );
		assertThat( geometry.getTranslateY(), is( nty ) );
		assertThat( geometry.getScaleX(), is( 1.0 * DesignTool.ZOOM_FACTOR ) );
		assertThat( geometry.getScaleY(), is( 1.0 * DesignTool.ZOOM_FACTOR ) );
	}

	@Test
	void testZoomOut() {
		DesignTool.zoom( geometry, ZOOM_OUT, 0, 0, 0 );
		assertThat( geometry.getTranslateX(), is( 0.0 ) );
		assertThat( geometry.getTranslateY(), is( 0.0 ) );
		assertThat( geometry.getScaleX(), is( 1.0 / DesignTool.ZOOM_FACTOR ) );
		assertThat( geometry.getScaleY(), is( 1.0 / DesignTool.ZOOM_FACTOR ) );
	}

	@Test
	void testZoomOutOffsetOrigin() {
		double ex = -1;
		double ey = -1;
		double otx = 0;
		double oty = 0;
		double dx = otx - ex;
		double dy = oty - ey;
		double ntx = ex + (dx / DesignTool.ZOOM_FACTOR);
		double nty = ey + (dy / DesignTool.ZOOM_FACTOR);
		DesignTool.zoom( geometry, ZOOM_OUT, -1, -1, 0 );
		assertThat( geometry.getTranslateX(), is( ntx ) );
		assertThat( geometry.getTranslateY(), is( nty ) );
		assertThat( geometry.getScaleX(), is( 1.0 / DesignTool.ZOOM_FACTOR ) );
		assertThat( geometry.getScaleY(), is( 1.0 / DesignTool.ZOOM_FACTOR ) );
	}

	private static class MockDesignTool extends DesignTool {

		public MockDesignTool() {
			super( null, null );
		}

		@Override
		protected Point3D mouseToWorld( MouseEvent event ) {
			return null;
		}
	}

}
