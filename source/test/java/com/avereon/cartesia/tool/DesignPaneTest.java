package com.avereon.cartesia.tool;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.NumericTest;
import com.avereon.cartesia.TestTimeouts;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.Design2D;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.CsaLine;
import com.avereon.zerra.javafx.FxUtil;
import com.avereon.zerra.javafx.JavaFxStarter;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class DesignPaneTest implements NumericTest, TestTimeouts {

	private static final boolean ZOOM_IN = true;

	private static final boolean ZOOM_OUT = false;

	private static final double SCALE = DesignUnit.INCH.from( DesignPane.DEFAULT_DPI, DesignUnit.CENTIMETER );

	private StackPane parent;

	private Design design;

	private DesignPane pane;

	@BeforeEach
	public void setup() {
		JavaFxStarter.startAndWait( FX_STARTUP_TIMEOUT );
		parent = new StackPane();
		design = new Design2D();
		pane = new DesignPane( design );
		parent.getChildren().add( pane );
		parent.resize( 1600, 900 );

		assertThat( parent.getWidth(), is( 1600.0 ) );
		assertThat( parent.getHeight(), is( 900.0 ) );
		assertThat( pane.getScaleX(), is( 1.0 * SCALE ) );
		assertThat( pane.getScaleY(), is( -1.0 * SCALE ) );
		assertThat( pane.getTranslateX(), is( 0.0 ) );
		assertThat( pane.getTranslateY(), is( 0.0 ) );
	}

	@Test
	void testAddLayer() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.addLayer( layer );
		FxUtil.fxWait( FX_WAIT_TIMEOUT );

		StackPane layers = (StackPane)pane.getChildren().get( 0 );
		assertThat( layers.getChildren().size(), is( 1 ) );
	}

	@Test
	void testAddLine() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.addLayer( layer ).setCurrentLayer( layer );
		design.getCurrentLayer().addShape( new CsaLine( new Point3D( 1, 2, 0 ), new Point3D( 3, 4, 0 ) ) );
		FxUtil.fxWait( FX_WAIT_TIMEOUT );

		// Now there should be a line in the pane
		StackPane layers = (StackPane)pane.getChildren().get( 0 );
		Pane construction = (Pane)layers.getChildren().get( 0 );
		Line line = (Line)construction.getChildren().get( 0 );
		assertThat( line.getStartX(), is( 1.0 ) );
		assertThat( line.getStartY(), is( 2.0 ) );
		assertThat( line.getEndX(), is( 3.0 ) );
		assertThat( line.getEndY(), is( 4.0 ) );
	}

	@Test
	void testPanAbsolute() {
		double offset = DesignUnit.CENTIMETER.to( 1, DesignUnit.INCH ) * pane.getDpi();

		pane.setPan( Point3D.ZERO );
		assertThat( pane.getTranslateX(), is( 800.0 ) );
		assertThat( pane.getTranslateY(), is( 450.0 ) );

		pane.setPan( new Point3D( 1, 1, 0 ) );
		assertThat( pane.getTranslateX(), is( 800.0 - offset ) );
		assertThat( pane.getTranslateY(), is( 450.0 + offset ) );
	}

	@Test
	void testPanRelative() {
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
