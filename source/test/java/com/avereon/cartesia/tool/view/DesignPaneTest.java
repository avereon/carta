package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.DesignUnit;
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

import static com.avereon.cartesia.match.Near.near;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DesignPaneTest implements TestTimeouts {

	private static final double PARENT_WIDTH = 1600.0;

	private static final double PARENT_HEIGHT = 900.0;

	private static final double PARENT_HALF_WIDTH = 0.5 * PARENT_WIDTH;

	private static final double PARENT_HALF_HEIGHT = 0.5 * PARENT_HEIGHT;

	private static final double SCALE = DesignUnit.INCH.from( DesignPane.DEFAULT_DPI, DesignUnit.CENTIMETER );

	private Design design;

	private DesignPane pane;

	@BeforeEach
	public void setup() {
		JavaFxStarter.startAndWait( FX_STARTUP_TIMEOUT );

		design = new Design2D();
		pane = new DesignPane().setDesign( design );

		StackPane parent = new StackPane();
		parent.resize( PARENT_WIDTH, PARENT_HEIGHT );
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
		layer.addShape( new DesignLine( new Point3D( -1, 1, 0 ), new Point3D( 1, -1, 0 ) ) );
		Fx.waitForWithExceptions( FX_WAIT_TIMEOUT );

		// Get the line that was added for use later
		DesignPaneLayer layers = pane.getLayerPane();
		DesignPaneLayer construction = (DesignPaneLayer)layers.getChildren().get( 0 );
		assertTrue( construction.isVisible() );
		Group group = (Group)construction.getChildren().get( 0 );
		Line line = (Line)group.getChildren().get( 0 );
		assertThat( line.getStartX(), is( -1.0 ) );
		assertThat( line.getStartY(), is( 1.0 ) );
		assertThat( line.getEndX(), is( 1.0 ) );
		assertThat( line.getEndY(), is( -1.0 ) );

		assertTrue( pane.worldPointSelect( new Point3D( 1, 1, 0 ), 0.1 ).isEmpty() );
		assertThat( pane.worldPointSelect( new Point3D( 0, 0, 0 ), 0.1 ), contains( line ) );
		assertTrue( pane.worldPointSelect( new Point3D( -1, -1, 0 ), 0.1 ).isEmpty() );
	}

	@Test
	void testWindowSelectWithIntersect() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.getRootLayer().addLayer( layer );
		layer.addShape( new DesignLine( new Point3D( -1, 1, 0 ), new Point3D( 1, -1, 0 ) ) );
		Fx.waitForWithExceptions( FX_WAIT_TIMEOUT );

		// Get the line that was added for use later
		DesignPaneLayer layers = pane.getLayerPane();
		DesignPaneLayer construction = (DesignPaneLayer)layers.getChildren().get( 0 );
		assertTrue( construction.isVisible() );
		Group group = (Group)construction.getChildren().get( 0 );
		Line line = (Line)group.getChildren().get( 0 );
		assertThat( line.getStartX(), is( -1.0 ) );
		assertThat( line.getStartY(), is( 1.0 ) );
		assertThat( line.getEndX(), is( 1.0 ) );
		assertThat( line.getEndY(), is( -1.0 ) );

		assertTrue( pane.windowSelect( new Point3D( 1, 1, 0 ), new Point3D( 2, 2, 0 ), false ).isEmpty() );
		assertThat( pane.windowSelect( new Point3D( 0, 0, 0 ), new Point3D( 1, 1, 0 ), false ), contains( line ) );
		assertThat( pane.windowSelect( new Point3D( -1, -1, 0 ), new Point3D( 0, 0, 0 ), false ), contains( line ) );
		assertTrue( pane.windowSelect( new Point3D( -2, -2, 0 ), new Point3D( -1, -1, 0 ), false ).isEmpty() );

		assertThat( pane.windowSelect( new Point3D( -0.5, -0.5, 0 ), new Point3D( 0.5, 0.5, 0 ), false ), contains( line ) );
		assertThat( pane.windowSelect( new Point3D( -1, -1, 0 ), new Point3D( 1, 1, 0 ), false ), contains( line ) );

		assertThat( pane.windowSelect( new Point3D( -2, -2, 0 ), new Point3D( 2, 2, 0 ), false ), contains( line ) );
		assertThat( pane.windowSelect( new Point3D( -2, 2, 0 ), new Point3D( 2, -2, 0 ), false ), contains( line ) );
	}

	@Test
	void testWindowSelectWithContains() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.getRootLayer().addLayer( layer );
		layer.addShape( new DesignLine( new Point3D( -1, 1, 0 ), new Point3D( 1, -1, 0 ) ) );
		Fx.waitForWithExceptions( FX_WAIT_TIMEOUT );

		// Get the line that was added for use later
		DesignPaneLayer layers = pane.getLayerPane();
		DesignPaneLayer construction = (DesignPaneLayer)layers.getChildren().get( 0 );
		assertTrue( construction.isVisible() );
		Group group = (Group)construction.getChildren().get( 0 );
		Line line = (Line)group.getChildren().get( 0 );
		assertThat( line.getStartX(), is( -1.0 ) );
		assertThat( line.getStartY(), is( 1.0 ) );
		assertThat( line.getEndX(), is( 1.0 ) );
		assertThat( line.getEndY(), is( -1.0 ) );

		assertTrue( pane.windowSelect( new Point3D( 1, 1, 0 ), new Point3D( 2, 2, 0 ), true ).isEmpty() );
		assertTrue( pane.windowSelect( new Point3D( 0, 0, 0 ), new Point3D( 1, 1, 0 ), true ).isEmpty() );
		assertTrue( pane.windowSelect( new Point3D( -1, -1, 0 ), new Point3D( 0, 0, 0 ), true ).isEmpty() );
		assertTrue( pane.windowSelect( new Point3D( -2, -2, 0 ), new Point3D( -1, -1, 0 ), true ).isEmpty() );

		assertTrue( pane.windowSelect( new Point3D( -0.5, -0.5, 0 ), new Point3D( 0.5, 0.5, 0 ), true ).isEmpty() );
		// This does not contain the line because the line has width and stroke caps
		assertTrue( pane.windowSelect( new Point3D( -1, -1, 0 ), new Point3D( 1, 1, 0 ), true ).isEmpty() );
		// This should just barely contain the line
		double d = line.getBoundsInLocal().getMaxX();
		assertThat( pane.windowSelect( new Point3D( -d, -d, 0 ), new Point3D( d, d, 0 ), true ), contains( line ) );

		assertThat( pane.windowSelect( new Point3D( -2, -2, 0 ), new Point3D( 2, 2, 0 ), true ), contains( line ) );
		assertThat( pane.windowSelect( new Point3D( -2, 2, 0 ), new Point3D( 2, -2, 0 ), true ), contains( line ) );
	}

	@Test
	void testAddLayer() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.getRootLayer().addLayer( layer );
		Fx.waitForWithExceptions( FX_WAIT_TIMEOUT );

		DesignPaneLayer layers = pane.getLayerPane();
		assertThat( layers.getChildren().size(), is( 1 ) );
	}

	@Test
	void testAddLine() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.getRootLayer().addLayer( layer );
		layer.addShape( new DesignLine( new Point3D( 1, 2, 0 ), new Point3D( 3, 4, 0 ) ) );
		Fx.waitForWithExceptions( FX_WAIT_TIMEOUT );

		// Now there should be a line in the pane
		DesignPaneLayer layers = pane.getLayerPane();
		DesignPaneLayer construction = (DesignPaneLayer)layers.getChildren().get( 0 );
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
		assertThat( pane.getViewPoint(), near( new Point3D( x, y, 0 ) ) );
	}

	@Test
	void testRotate() {
		pane.setViewRotate( 0.0 );
		assertThat( pane.getViewRotate(), near( 0.0 ) );

		pane.setViewRotate( 45.0 );
		assertThat( pane.getViewRotate(), near( 45.0 ) );
	}

	@Test
	void checkZoomFactor() {
		assertThat( 1 * DesignPane.ZOOM_IN_FACTOR, is( 1.189207115002721 ) );
	}

	@Test
	void testZoomIn() {
		Point3D point = pane.parentToLocal( PARENT_HALF_WIDTH, PARENT_HALF_HEIGHT, 0 );
		pane.zoom( point, DesignPane.ZOOM_IN_FACTOR );
		assertThat( pane.getTranslateX(), is( PARENT_HALF_WIDTH ) );
		assertThat( pane.getTranslateY(), is( PARENT_HALF_HEIGHT ) );
		assertThat( pane.getScaleX(), near( SCALE * DesignPane.ZOOM_IN_FACTOR ) );
		assertThat( pane.getScaleY(), near( -SCALE * DesignPane.ZOOM_IN_FACTOR ) );
	}

	@Test
	void testZoomOut() {
		Point3D point = pane.parentToLocal( PARENT_HALF_WIDTH, PARENT_HALF_HEIGHT, 0 );
		pane.zoom( point, DesignPane.ZOOM_OUT_FACTOR );
		assertThat( pane.getTranslateX(), is( PARENT_HALF_WIDTH ) );
		assertThat( pane.getTranslateY(), is( PARENT_HALF_HEIGHT ) );
		assertThat( pane.getScaleX(), near( 1.0 * SCALE / DesignPane.ZOOM_IN_FACTOR ) );
		assertThat( pane.getScaleY(), near( -1.0 * SCALE / DesignPane.ZOOM_IN_FACTOR ) );
	}

	@Test
	void testZoomInOffsetOrigin() {
		double worldX = -1;
		double worldY = -1;
		double ex = PARENT_HALF_WIDTH + worldX * pane.getScaleX();
		double ey = PARENT_HALF_HEIGHT + worldY * pane.getScaleY();
		assertThat( pane.localToParent( worldX, worldY, 0 ), is( new Point3D( ex, ey, 0 ) ) );

		Point3D point = pane.parentToLocal( ex, ey, 0 );
		pane.zoom( point, DesignPane.ZOOM_IN_FACTOR );

		double newScale = SCALE * DesignPane.ZOOM_IN_FACTOR;
		double offset = newScale - SCALE;
		assertThat( pane.getScaleX(), near( newScale ) );
		assertThat( -pane.getScaleY(), near( newScale ) );
		assertThat( pane.getTranslateX(), near( PARENT_HALF_WIDTH + offset ) );
		assertThat( pane.getTranslateY(), near( PARENT_HALF_HEIGHT - offset, 1 ) );

		// The mouse coords for the world point should still be the same
		assertThat( pane.localToParent( worldX, worldY, 0 ), is( new Point3D( ex, ey, 0 ) ) );
	}

	@Test
	void testZoomOutOffsetOrigin() {
		double worldX = -1;
		double worldY = -1;
		double ex = PARENT_HALF_WIDTH + worldX * pane.getScaleX();
		double ey = PARENT_HALF_HEIGHT + worldY * pane.getScaleY();
		assertThat( pane.localToParent( worldX, worldY, 0 ), is( new Point3D( ex, ey, 0 ) ) );

		Point3D point = pane.parentToLocal( ex, ey, 0 );
		pane.zoom( point, DesignPane.ZOOM_OUT_FACTOR );

		double newScale = SCALE * DesignPane.ZOOM_OUT_FACTOR;
		double offset = newScale - SCALE;
		assertThat( pane.getScaleX(), near( newScale ) );
		assertThat( -pane.getScaleY(), near( newScale ) );
		assertThat( pane.getTranslateX(), near( PARENT_HALF_WIDTH + offset ) );
		assertThat( pane.getTranslateY(), near( PARENT_HALF_HEIGHT - offset, 1 ) );

		// The mouse coords for the world point should still be the same
		assertThat( pane.localToParent( worldX, worldY, 0 ), is( new Point3D( ex, ey, 0 ) ) );
	}

	@Test
	void testChangeDesignUnitCausesRescale() {
		design.setDesignUnit( DesignUnit.MILLIMETER );
		double scale = DesignUnit.INCH.from( DesignPane.DEFAULT_DPI, design.calcDesignUnit() );
		assertThat( pane.getScaleX(), near( 1.0 * scale ) );
		assertThat( pane.getScaleY(), near( -1.0 * scale ) );
	}

}
