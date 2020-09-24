package com.avereon.cartesia.tool;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.NumericTest;
import com.avereon.cartesia.TestTimeouts;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.Design2D;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.zerra.javafx.Fx;
import com.avereon.zerra.javafx.JavaFxStarter;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DesignPaneTest implements NumericTest, TestTimeouts {

	private static final double PARENT_WIDTH = 1600.0;

	private static final double PARENT_HEIGHT = 900.0;

	private static final double PARENT_HALF_WIDTH = 0.5 * PARENT_WIDTH;

	private static final double PARENT_HALF_HEIGHT = 0.5 * PARENT_HEIGHT;

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
		parent.resize( PARENT_WIDTH, PARENT_HEIGHT );

		design = new Design2D();
		pane = new DesignPane().loadDesign( design );
		parent.getChildren().add( pane );

		assertThat( parent.getWidth(), is( PARENT_WIDTH ) );
		assertThat( parent.getHeight(), is( PARENT_HEIGHT ) );
		assertThat( pane.getScaleX(), is( 1.0 * SCALE ) );
		assertThat( pane.getScaleY(), is( -1.0 * SCALE ) );
		assertThat( pane.getTranslateX(), is( PARENT_HALF_WIDTH ) );
		assertThat( pane.getTranslateY(), is( PARENT_HALF_HEIGHT ) );
	}

	@Test
	void testApertureSelect() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.getRootLayer().addLayer( layer );
		design.setCurrentLayer( layer );
		design.getCurrentLayer().addShape( new DesignLine( new Point3D( -1, 1, 0 ), new Point3D( 1, -1, 0 ) ) );
		Fx.waitForWithInterrupt( FX_WAIT_TIMEOUT );

		// Get the line that was added for use later
		DesignPane.Layer layers = (DesignPane.Layer)pane.getChildren().get( 0 );
		DesignPane.Layer construction = (DesignPane.Layer)layers.getChildren().get( 0 );
		assertTrue( construction.isVisible() );
		Group group = (Group)construction.getChildren().get( 0 );
		Line line = (Line)group.getChildren().get( 0 );
		assertThat( line.getStartX(), is( -1.0 ) );
		assertThat( line.getStartY(), is( 1.0 ) );
		assertThat( line.getEndX(), is( 1.0 ) );
		assertThat( line.getEndY(), is( -1.0 ) );

		assertTrue( pane.selectByAperture( new Point3D( 1, 1, 0 ), 0.1 ).isEmpty() );
		assertThat( pane.selectByAperture( new Point3D( 0, 0, 0 ), 0.1 ), contains( line ) );
		assertTrue( pane.selectByAperture( new Point3D( -1, -1, 0 ), 0.1 ).isEmpty() );
	}

	@Test
	void testWindowSelectWithIntersect() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.getRootLayer().addLayer( layer );
		design.setCurrentLayer( layer );
		design.getCurrentLayer().addShape( new DesignLine( new Point3D( -1, 1, 0 ), new Point3D( 1, -1, 0 ) ) );
		Fx.waitForWithInterrupt( FX_WAIT_TIMEOUT );

		// Get the line that was added for use later
		DesignPane.Layer layers = (DesignPane.Layer)pane.getChildren().get( 0 );
		DesignPane.Layer construction = (DesignPane.Layer)layers.getChildren().get( 0 );
		assertTrue( construction.isVisible() );
		Group group = (Group)construction.getChildren().get( 0 );
		Line line = (Line)group.getChildren().get( 0 );
		assertThat( line.getStartX(), is( -1.0 ) );
		assertThat( line.getStartY(), is( 1.0 ) );
		assertThat( line.getEndX(), is( 1.0 ) );
		assertThat( line.getEndY(), is( -1.0 ) );

		assertTrue( pane.selectByWindow( new Point3D( 1, 1, 0 ), new Point3D( 2, 2, 0 ), false ).isEmpty() );
		assertThat( pane.selectByWindow( new Point3D( 0, 0, 0 ), new Point3D( 1, 1, 0 ), false ), contains( line ) );
		assertThat( pane.selectByWindow( new Point3D( -1, -1, 0 ), new Point3D( 0, 0, 0 ), false ), contains( line ) );
		assertTrue( pane.selectByWindow( new Point3D( -2, -2, 0 ), new Point3D( -1, -1, 0 ), false ).isEmpty() );

		assertThat( pane.selectByWindow( new Point3D( -0.5, -0.5, 0 ), new Point3D( 0.5, 0.5, 0 ), false ), contains( line ) );
		assertThat( pane.selectByWindow( new Point3D( -1, -1, 0 ), new Point3D( 1, 1, 0 ), false ), contains( line ) );

		assertThat( pane.selectByWindow( new Point3D( -2, -2, 0 ), new Point3D( 2, 2, 0 ), false ), contains( line ) );
		assertThat( pane.selectByWindow( new Point3D( -2, 2, 0 ), new Point3D( 2, -2, 0 ), false ), contains( line ) );
	}

	@Test
	void testWindowSelectWithContains() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.getRootLayer().addLayer( layer );
		design.setCurrentLayer( layer );
		design.getCurrentLayer().addShape( new DesignLine( new Point3D( -1, 1, 0 ), new Point3D( 1, -1, 0 ) ) );
		Fx.waitForWithInterrupt( FX_WAIT_TIMEOUT );

		// Get the line that was added for use later
		DesignPane.Layer layers = (DesignPane.Layer)pane.getChildren().get( 0 );
		DesignPane.Layer construction = (DesignPane.Layer)layers.getChildren().get( 0 );
		assertTrue( construction.isVisible() );
		Group group = (Group)construction.getChildren().get( 0 );
		Line line = (Line)group.getChildren().get( 0 );
		assertThat( line.getStartX(), is( -1.0 ) );
		assertThat( line.getStartY(), is( 1.0 ) );
		assertThat( line.getEndX(), is( 1.0 ) );
		assertThat( line.getEndY(), is( -1.0 ) );

		assertTrue( pane.selectByWindow( new Point3D( 1, 1, 0 ), new Point3D( 2, 2, 0 ), true ).isEmpty() );
		assertTrue( pane.selectByWindow( new Point3D( 0, 0, 0 ), new Point3D( 1, 1, 0 ), true ).isEmpty() );
		assertTrue( pane.selectByWindow( new Point3D( -1, -1, 0 ), new Point3D( 0, 0, 0 ), true ).isEmpty() );
		assertTrue( pane.selectByWindow( new Point3D( -2, -2, 0 ), new Point3D( -1, -1, 0 ), true ).isEmpty() );

		assertTrue( pane.selectByWindow( new Point3D( -0.5, -0.5, 0 ), new Point3D( 0.5, 0.5, 0 ), true ).isEmpty() );
		// This does not contain the line because the line has width and stroke caps
		assertTrue( pane.selectByWindow( new Point3D( -1, -1, 0 ), new Point3D( 1, 1, 0 ), true ).isEmpty() );
		// This should just barely contain the line
		double d = line.getBoundsInLocal().getMaxX();
		assertThat( pane.selectByWindow( new Point3D( -d, -d, 0 ), new Point3D( d, d, 0 ), true ), contains( line ) );

		assertThat( pane.selectByWindow( new Point3D( -2, -2, 0 ), new Point3D( 2, 2, 0 ), true ), contains( line ) );
		assertThat( pane.selectByWindow( new Point3D( -2, 2, 0 ), new Point3D( 2, -2, 0 ), true ), contains( line ) );
	}

	@Test
	void testAddLayer() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.getRootLayer().addLayer( layer );
		Fx.waitForWithInterrupt( FX_WAIT_TIMEOUT );

		DesignPane.Layer layers = (DesignPane.Layer)pane.getChildren().get( 0 );
		assertThat( layers.getChildren().size(), is( 1 ) );
	}

	@Test
	void testAddLine() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.getRootLayer().addLayer( layer );
		design.setCurrentLayer( layer );
		design.getCurrentLayer().addShape( new DesignLine( new Point3D( 1, 2, 0 ), new Point3D( 3, 4, 0 ) ) );
		Fx.waitForWithInterrupt( FX_WAIT_TIMEOUT );

		// Now there should be a line in the pane
		DesignPane.Layer layers = (DesignPane.Layer)pane.getChildren().get( 0 );
		DesignPane.Layer construction = (DesignPane.Layer)layers.getChildren().get( 0 );
		Group group = (Group)construction.getChildren().get( 0 );
		Line line = (Line)group.getChildren().get( 0 );
		assertThat( line.getStartX(), is( 1.0 ) );
		assertThat( line.getStartY(), is( 2.0 ) );
		assertThat( line.getEndX(), is( 3.0 ) );
		assertThat( line.getEndY(), is( 4.0 ) );
	}

	@Test
	void testPanAbsolute() {
		double offset = DesignUnit.CENTIMETER.to( 1, DesignUnit.INCH ) * pane.getDpi();
		double cx = PARENT_HALF_WIDTH;
		double cy = PARENT_HALF_HEIGHT;

		pane.setViewPoint( Point3D.ZERO );
		assertThat( pane.getTranslateX(), is( cx ) );
		assertThat( pane.getTranslateY(), is( cy ) );

		pane.setViewPoint( new Point3D( 1, 1, 0 ) );
		assertThat( pane.getTranslateX(), is( cx - offset ) );
		assertThat( pane.getTranslateY(), is( cy + offset ) );
	}

	@Test
	void testPan() {
		Point3D vp = pane.getViewPoint();
		pane.pan( 1, 1 );
		assertThat( pane.getViewPoint(), is( vp.add( 1, 1, 0 ) ) );
	}

	@Test
	void testPanOffsetOrigin() {
		Point3D viewAnchor = new Point3D( -2, -1, 0 );
		Point3D dragAnchor = new Point3D( 1, 1, 0 );
		Point3D mouse = new Point3D( 2, 0.5, 0 );
		pane.mousePan( viewAnchor, dragAnchor.multiply( SCALE ), mouse.multiply( SCALE ).getX(), mouse.multiply( SCALE ).getY() );

		double x = viewAnchor.getX() + (dragAnchor.getX() - mouse.getX());
		double y = viewAnchor.getY() - (dragAnchor.getY() - mouse.getY());
		assertThat( pane.getViewPoint(), is( new Point3D( x, y, 0 ) ) );
	}

	@Test
	void checkZoomFactor() {
		assertThat( 1 * DesignPane.ZOOM_IN_FACTOR, is( 1.189207115002721 ) );
	}

	@Test
	void testZoomIn() {
		pane.mouseZoom( PARENT_HALF_WIDTH, PARENT_HALF_HEIGHT, ZOOM_IN );
		assertThat( pane.getTranslateX(), is( PARENT_HALF_WIDTH ) );
		assertThat( pane.getTranslateY(), is( PARENT_HALF_HEIGHT ) );
		assertThat( pane.getScaleX(), closeTo( SCALE * DesignPane.ZOOM_IN_FACTOR, TOLERANCE ) );
		assertThat( pane.getScaleY(), closeTo( -SCALE * DesignPane.ZOOM_IN_FACTOR, TOLERANCE ) );
	}

	@Test
	void testZoomInOffsetOrigin() {
		double worldX = -1;
		double worldY = -1;
		double ex = PARENT_HALF_WIDTH + worldX * pane.getScaleX();
		double ey = PARENT_HALF_HEIGHT + worldY * pane.getScaleY();
		assertThat( pane.worldToMouse( worldX, worldY, 0 ), is( new Point3D( ex, ey, 0 ) ) );

		pane.mouseZoom( ex, ey, ZOOM_IN );

		double newScale = SCALE * DesignPane.ZOOM_IN_FACTOR;
		double offset = newScale - SCALE;
		assertThat( pane.getScaleX(), closeTo( newScale, TOLERANCE ) );
		assertThat( -pane.getScaleY(), closeTo( newScale, TOLERANCE ) );
		assertThat( pane.getTranslateX(), closeTo( PARENT_HALF_WIDTH + offset, TOLERANCE ) );
		assertThat( pane.getTranslateY(), closeTo( PARENT_HALF_HEIGHT - offset, 1 ) );

		// The mouse coords for the world point should still be the same
		assertThat( pane.worldToMouse( worldX, worldY, 0 ), is( new Point3D( ex, ey, 0 ) ) );
	}

	@Test
	void testZoomOut() {
		pane.mouseZoom( PARENT_HALF_WIDTH, PARENT_HALF_HEIGHT, ZOOM_OUT );
		assertThat( pane.getTranslateX(), is( PARENT_HALF_WIDTH ) );
		assertThat( pane.getTranslateY(), is( PARENT_HALF_HEIGHT ) );
		assertThat( pane.getScaleX(), closeTo( 1.0 * SCALE / DesignPane.ZOOM_IN_FACTOR, TOLERANCE ) );
		assertThat( pane.getScaleY(), closeTo( -1.0 * SCALE / DesignPane.ZOOM_IN_FACTOR, TOLERANCE ) );
	}

	@Test
	void testZoomOutOffsetOrigin() {
		double worldX = -1;
		double worldY = -1;
		double ex = PARENT_HALF_WIDTH + worldX * pane.getScaleX();
		double ey = PARENT_HALF_HEIGHT + worldY * pane.getScaleY();
		assertThat( pane.worldToMouse( worldX, worldY, 0 ), is( new Point3D( ex, ey, 0 ) ) );

		pane.mouseZoom( ex, ey, ZOOM_OUT );

		double newScale = SCALE * DesignPane.ZOOM_OUT_FACTOR;
		double offset = newScale - SCALE;
		assertThat( pane.getScaleX(), closeTo( newScale, TOLERANCE ) );
		assertThat( -pane.getScaleY(), closeTo( newScale, TOLERANCE ) );
		assertThat( pane.getTranslateX(), closeTo( PARENT_HALF_WIDTH + offset, TOLERANCE ) );
		assertThat( pane.getTranslateY(), closeTo( PARENT_HALF_HEIGHT - offset, 1 ) );

		// The mouse coords for the world point should still be the same
		assertThat( pane.worldToMouse( worldX, worldY, 0 ), is( new Point3D( ex, ey, 0 ) ) );
	}

	@Test
	void testChangeDesignUnitCausesRescale() {
		design.setDesignUnit( DesignUnit.MILLIMETER );
		double scale = DesignUnit.INCH.from( DesignPane.DEFAULT_DPI, design.getDesignUnit() );
		assertThat( pane.getScaleX(), closeTo( 1.0 * scale, TOLERANCE ) );
		assertThat( pane.getScaleY(), closeTo( -1.0 * scale, TOLERANCE ) );
	}

}
