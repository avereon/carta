package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.Point2DAssert;
import com.avereon.cartesia.PointAssert;
import com.avereon.cartesia.TestTimeouts;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.Design2D;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.zarra.javafx.Fx;
import com.avereon.zerra.BaseFxPlatformTestCase;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class DesignPaneTest extends BaseFxPlatformTestCase implements TestTimeouts {

	private static final double PARENT_WIDTH = 1600.0;

	private static final double PARENT_HEIGHT = 900.0;

	private static final double PARENT_HALF_WIDTH = 0.5 * PARENT_WIDTH;

	private static final double PARENT_HALF_HEIGHT = 0.5 * PARENT_HEIGHT;

	private static final double SCALE = DesignUnit.INCH.from( DesignPane.DEFAULT_DPI, DesignUnit.CENTIMETER );

	private static final double POINT_TOLERANCE = 1e-4;

	private Design design;

	private DesignPane pane;

	@BeforeEach
	public void setup() throws Exception {
		super.setup();

		design = new Design2D();
		pane = new DesignPane().setDesign( design );

		StackPane parent = new StackPane();
		parent.resize( PARENT_WIDTH, PARENT_HEIGHT );
		parent.getChildren().add( pane );

		assertThat( parent.getWidth() ).isEqualTo( PARENT_WIDTH );
		assertThat( parent.getHeight() ).isEqualTo( PARENT_HEIGHT );
		assertThat( pane.getScaleX() ).isEqualTo( 1.0 * SCALE );
		assertThat( pane.getScaleY() ).isEqualTo( -1.0 * SCALE );
		assertThat( pane.getTranslateX() ).isEqualTo( PARENT_HALF_WIDTH );
		assertThat( pane.getTranslateY() ).isEqualTo( PARENT_HALF_HEIGHT );
	}

	@Test
	void testApertureSelect() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.getLayers().addLayer( layer );
		layer.addShape( new DesignLine( new Point3D( -1, 1, 0 ), new Point3D( 1, -1, 0 ) ) );
		Fx.waitForWithExceptions( FX_WAIT_TIMEOUT );

		// Get the line that was added for use later
		DesignLayerPane layers = pane.getLayerPane();
		DesignLayerPane construction = (DesignLayerPane)layers.getChildren().get( 0 );
		assertThat( construction.isVisible() ).isTrue();
		Group group = (Group)construction.getChildren().get( 0 );
		Line line = (Line)group.getChildren().get( 0 );
		assertThat( line.getStartX() ).isEqualTo( -1.0 );
		assertThat( line.getStartY() ).isEqualTo( 1.0 );
		assertThat( line.getEndX() ).isEqualTo( 1.0 );
		assertThat( line.getEndY() ).isEqualTo( -1.0 );

		assertThat( pane.worldPointSelect( new Point3D( 1, 1, 0 ), 0.1 ).isEmpty() ).isTrue();
		assertThat( pane.worldPointSelect( new Point3D( 0, 0, 0 ), 0.1 ) ).contains( line );
		assertThat( pane.worldPointSelect( new Point3D( -1, -1, 0 ), 0.1 ).isEmpty() ).isTrue();
	}

	@Test
	void testWindowSelectWithIntersect() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.getLayers().addLayer( layer );
		layer.addShape( new DesignLine( new Point3D( -1, 1, 0 ), new Point3D( 1, -1, 0 ) ) );
		Fx.waitForWithExceptions( FX_WAIT_TIMEOUT );

		// Get the line that was added for use later
		DesignLayerPane layers = pane.getLayerPane();
		DesignLayerPane construction = (DesignLayerPane)layers.getChildren().get( 0 );
		assertThat( construction.isVisible() ).isTrue();
		Group group = (Group)construction.getChildren().get( 0 );
		Line line = (Line)group.getChildren().get( 0 );
		assertThat( line.getStartX() ).isEqualTo( -1.0 );
		assertThat( line.getStartY() ).isEqualTo( 1.0 );
		assertThat( line.getEndX() ).isEqualTo( 1.0 );
		assertThat( line.getEndY() ).isEqualTo( -1.0 );

		assertThat( pane.worldWindowSelect( new Point3D( 1, 1, 0 ), new Point3D( 2, 2, 0 ), false ).isEmpty() ).isTrue();
		assertThat( pane.worldWindowSelect( new Point3D( 0, 0, 0 ), new Point3D( 1, 1, 0 ), false ) ).contains( line );
		assertThat( pane.worldWindowSelect( new Point3D( -1, -1, 0 ), new Point3D( 0, 0, 0 ), false ) ).contains( line );
		assertThat( pane.worldWindowSelect( new Point3D( -2, -2, 0 ), new Point3D( -1, -1, 0 ), false ).isEmpty() ).isTrue();

		assertThat( pane.worldWindowSelect( new Point3D( -0.5, -0.5, 0 ), new Point3D( 0.5, 0.5, 0 ), false ) ).contains( line );
		assertThat( pane.worldWindowSelect( new Point3D( -1, -1, 0 ), new Point3D( 1, 1, 0 ), false ) ).contains( line );

		assertThat( pane.worldWindowSelect( new Point3D( -2, -2, 0 ), new Point3D( 2, 2, 0 ), false ) ).contains( line );
		assertThat( pane.worldWindowSelect( new Point3D( -2, 2, 0 ), new Point3D( 2, -2, 0 ), false ) ).contains( line );
	}

	@Test
	void testWindowSelectWithContains() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.getLayers().addLayer( layer );
		layer.addShape( new DesignLine( new Point3D( -1, 1, 0 ), new Point3D( 1, -1, 0 ) ) );
		Fx.waitForWithExceptions( FX_WAIT_TIMEOUT );

		// Get the line that was added for use later
		DesignLayerPane layers = pane.getLayerPane();
		DesignLayerPane construction = (DesignLayerPane)layers.getChildren().get( 0 );
		assertThat( construction.isVisible() ).isTrue();
		Group group = (Group)construction.getChildren().get( 0 );
		Line line = (Line)group.getChildren().get( 0 );
		assertThat( line.getStartX() ).isEqualTo( -1.0 );
		assertThat( line.getStartY() ).isEqualTo( 1.0 );
		assertThat( line.getEndX() ).isEqualTo( 1.0 );
		assertThat( line.getEndY() ).isEqualTo( -1.0 );

		assertThat( pane.worldWindowSelect( new Point3D( 1, 1, 0 ), new Point3D( 2, 2, 0 ), true ).isEmpty() ).isTrue();
		assertThat( pane.worldWindowSelect( new Point3D( 0, 0, 0 ), new Point3D( 1, 1, 0 ), true ).isEmpty() ).isTrue();
		assertThat( pane.worldWindowSelect( new Point3D( -1, -1, 0 ), new Point3D( 0, 0, 0 ), true ).isEmpty() ).isTrue();
		assertThat( pane.worldWindowSelect( new Point3D( -2, -2, 0 ), new Point3D( -1, -1, 0 ), true ).isEmpty() ).isTrue();

		assertThat( pane.worldWindowSelect( new Point3D( -0.5, -0.5, 0 ), new Point3D( 0.5, 0.5, 0 ), true ).isEmpty() ).isTrue();
		// This does not contain the line because the line has width and stroke caps
		assertThat( pane.worldWindowSelect( new Point3D( -1, -1, 0 ), new Point3D( 1, 1, 0 ), true ).isEmpty() ).isTrue();
		// This should just barely contain the line
		double d = line.getBoundsInLocal().getMaxX();
		assertThat( pane.worldWindowSelect( new Point3D( -d, -d, 0 ), new Point3D( d, d, 0 ), true ) ).contains( line );

		assertThat( pane.worldWindowSelect( new Point3D( -2, -2, 0 ), new Point3D( 2, 2, 0 ), true ) ).contains( line );
		assertThat( pane.worldWindowSelect( new Point3D( -2, 2, 0 ), new Point3D( 2, -2, 0 ), true ) ).contains( line );
	}

	@Test
	void testAddLayer() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.getLayers().addLayer( layer );
		Fx.waitForWithExceptions( FX_WAIT_TIMEOUT );

		DesignLayerPane layers = pane.getLayerPane();
		assertThat( layers.getChildren().size() ).isEqualTo( 1 );
	}

	@Test
	void testAddLine() throws Exception {
		DesignLayer layer = new DesignLayer().setName( "Test Layer" );
		design.getLayers().addLayer( layer );
		layer.addShape( new DesignLine( new Point3D( 1, 2, 0 ), new Point3D( 3, 4, 0 ) ) );
		Fx.waitForWithExceptions( FX_WAIT_TIMEOUT );

		// Now there should be a line in the pane
		DesignLayerPane layers = pane.getLayerPane();
		DesignLayerPane construction = (DesignLayerPane)layers.getChildren().get( 0 );
		Group group = (Group)construction.getChildren().get( 0 );
		Line line = (Line)group.getChildren().get( 0 );
		assertThat( line.getStartX() ).isEqualTo( 1.0 );
		assertThat( line.getStartY() ).isEqualTo( 2.0 );
		assertThat( line.getEndX() ).isEqualTo( 3.0 );
		assertThat( line.getEndY() ).isEqualTo( 4.0 );
	}

	@Test
	void testPanAbsolute() {
		double offset = DesignUnit.CENTIMETER.to( 1, DesignUnit.INCH ) * pane.getDpi();
		double cx = PARENT_HALF_WIDTH;
		double cy = PARENT_HALF_HEIGHT;

		pane.setViewPoint( Point3D.ZERO );
		assertThat( pane.getTranslateX() ).isEqualTo( cx );
		assertThat( pane.getTranslateY() ).isEqualTo( cy );

		pane.setViewPoint( new Point3D( 1, 1, 0 ) );
		assertThat( pane.getTranslateX() ).isEqualTo( cx - offset );
		assertThat( pane.getTranslateY() ).isEqualTo( cy + offset );
	}

	@Test
	void testPan() {
		Point3D vp = pane.getViewPoint();
		pane.pan( 1, 1 );
		assertThat( pane.getViewPoint() ).isEqualTo( vp.add( 1, 1, 0 ) );
	}

	@Test
	void testPanOffsetOrigin() {
		Point3D viewAnchor = new Point3D( -2, -1, 0 );
		Point3D dragAnchor = new Point3D( 1, 1, 0 );
		Point3D mouse = new Point3D( 2, 0.5, 0 );
		pane.mousePan( viewAnchor, dragAnchor.multiply( SCALE ), mouse.multiply( SCALE ).getX(), mouse.multiply( SCALE ).getY() );

		double x = viewAnchor.getX() + (dragAnchor.getX() - mouse.getX());
		double y = viewAnchor.getY() - (dragAnchor.getY() - mouse.getY());
		PointAssert.assertThat( pane.getViewPoint() ).isCloseTo( new Point3D( x, y, 0 ) );
	}

	@Test
	void testRotate() {
		pane.setViewRotate( 0.0 );
		assertThat( pane.getViewRotate() ).isCloseTo( 0.0, TOLERANCE );

		pane.setViewRotate( 45.0 );
		assertThat( pane.getViewRotate() ).isCloseTo( 45.0, TOLERANCE );
	}

	@Test
	void checkZoomFactor() {
		assertThat( 1 * DesignPane.ZOOM_IN_FACTOR ).isEqualTo( 1.189207115002721 );
	}

	@Test
	void testZoomIn() {
		Point3D point = pane.parentToLocal( PARENT_HALF_WIDTH, PARENT_HALF_HEIGHT, 0 );
		pane.zoom( point, DesignPane.ZOOM_IN_FACTOR );
		assertThat( pane.getTranslateX() ).isEqualTo( PARENT_HALF_WIDTH );
		assertThat( pane.getTranslateY() ).isEqualTo( PARENT_HALF_HEIGHT );
		assertThat( pane.getScaleX() ).isCloseTo( SCALE * DesignPane.ZOOM_IN_FACTOR, TOLERANCE );
		assertThat( pane.getScaleY() ).isCloseTo( -SCALE * DesignPane.ZOOM_IN_FACTOR, TOLERANCE );
	}

	@Test
	void testZoomOut() {
		Point3D point = pane.parentToLocal( PARENT_HALF_WIDTH, PARENT_HALF_HEIGHT, 0 );
		pane.zoom( point, DesignPane.ZOOM_OUT_FACTOR );
		assertThat( pane.getTranslateX() ).isEqualTo( PARENT_HALF_WIDTH );
		assertThat( pane.getTranslateY() ).isEqualTo( PARENT_HALF_HEIGHT );
		assertThat( pane.getScaleX() ).isCloseTo( 1.0 * SCALE / DesignPane.ZOOM_IN_FACTOR, TOLERANCE );
		assertThat( pane.getScaleY() ).isCloseTo( -1.0 * SCALE / DesignPane.ZOOM_IN_FACTOR, TOLERANCE );
	}

	@Test
	void testZoomInOffsetOrigin() {
		double worldX = -1;
		double worldY = -1;
		double ex = PARENT_HALF_WIDTH + worldX * pane.getScaleX();
		double ey = PARENT_HALF_HEIGHT + worldY * pane.getScaleY();
		assertThat( pane.localToParent( worldX, worldY, 0 ) ).isEqualTo( new Point3D( ex, ey, 0 ) );

		Point3D point = pane.parentToLocal( ex, ey, 0 );
		pane.zoom( point, DesignPane.ZOOM_IN_FACTOR );

		double newScale = SCALE * DesignPane.ZOOM_IN_FACTOR;
		double offset = newScale - SCALE;
		assertThat( pane.getScaleX() ).isCloseTo( newScale, TOLERANCE );
		assertThat( -pane.getScaleY() ).isCloseTo( newScale, TOLERANCE );
		assertThat( pane.getTranslateX() ).isCloseTo( PARENT_HALF_WIDTH + offset, TOLERANCE );
		assertThat( pane.getTranslateY() ).isCloseTo( PARENT_HALF_HEIGHT - offset, Offset.offset( 1.0 ) );

		// The mouse coords for the world point should still be the same
		assertThat( pane.localToParent( worldX, worldY, 0 ) ).isEqualTo( new Point3D( ex, ey, 0 ) );
	}

	@Test
	void testZoomOutOffsetOrigin() {
		double worldX = -1;
		double worldY = -1;
		double ex = PARENT_HALF_WIDTH + worldX * pane.getScaleX();
		double ey = PARENT_HALF_HEIGHT + worldY * pane.getScaleY();
		assertThat( pane.localToParent( worldX, worldY, 0 ) ).isEqualTo( new Point3D( ex, ey, 0 ) );

		Point3D point = pane.parentToLocal( ex, ey, 0 );
		pane.zoom( point, DesignPane.ZOOM_OUT_FACTOR );

		double newScale = SCALE * DesignPane.ZOOM_OUT_FACTOR;
		double offset = newScale - SCALE;
		assertThat( pane.getScaleX() ).isCloseTo( newScale, TOLERANCE );
		assertThat( -pane.getScaleY() ).isCloseTo( newScale, TOLERANCE );
		assertThat( pane.getTranslateX() ).isCloseTo( PARENT_HALF_WIDTH + offset, TOLERANCE );
		assertThat( pane.getTranslateY() ).isCloseTo( PARENT_HALF_HEIGHT - offset, Offset.offset( 1.0 ) );

		// The mouse coords for the world point should still be the same
		assertThat( pane.localToParent( worldX, worldY, 0 ) ).isEqualTo( new Point3D( ex, ey, 0 ) );
	}

	@Test
	void testChangeDesignUnitCausesRescale() {
		design.setDesignUnit( DesignUnit.MILLIMETER );
		double scale = DesignUnit.INCH.from( DesignPane.DEFAULT_DPI, design.calcDesignUnit() );
		assertThat( pane.getScaleX() ).isCloseTo( 1.0 * scale, TOLERANCE );
		assertThat( pane.getScaleY() ).isCloseTo( -1.0 * scale, TOLERANCE );
	}

	@Test
	void testLocalToParent() {
		double width = PARENT_WIDTH;
		double height = PARENT_HEIGHT;

		double offset = DesignUnit.CENTIMETER.to( pane.getDpi() * pane.getZoom(), DesignUnit.INCH );

		assertThat( pane.localToParent( 0, 0 ) ).isEqualTo( new Point2D( 0.5 * width, 0.5 * height ) );
		Point2DAssert.assertThat( pane.localToParent( 1, 1 ) ).isCloseTo( new Point2D( 0.5 * width + offset, 0.5 * height - offset ), POINT_TOLERANCE );
		Point2DAssert.assertThat( pane.localToParent( -1, 1 ) ).isCloseTo( new Point2D( 0.5 * width - offset, 0.5 * height - offset ), POINT_TOLERANCE );
		Point2DAssert.assertThat( pane.localToParent( -1, -1 ) ).isCloseTo( new Point2D( 0.5 * width - offset, 0.5 * height + offset ), POINT_TOLERANCE );
		Point2DAssert.assertThat( pane.localToParent( 1, -1 ) ).isCloseTo( new Point2D( 0.5 * width + offset, 0.5 * height + offset ), POINT_TOLERANCE );
	}

	@Test
	void testLocalToParentWithViewpoint() {
		double width = PARENT_WIDTH;
		double height = PARENT_HEIGHT;
		pane.setView( new Point3D( 1, 0, 0 ), 2, 90 );

		double offset = DesignUnit.CENTIMETER.to( pane.getDpi() * 2, DesignUnit.INCH );

		Point2DAssert.assertThat( pane.localToParent( 0, 0 ) ).isCloseTo( new Point2D( 0.5 * width, 0.5 * height + offset ), POINT_TOLERANCE );

		Point2DAssert.assertThat( pane.localToParent( 1, 1 ) ).isCloseTo( new Point2D( 0.5 * width - offset, 0.5 * height ), POINT_TOLERANCE );
		Point2DAssert.assertThat( pane.localToParent( -1, 1 ) ).isCloseTo( new Point2D( 0.5 * width - offset, 0.5 * height + 2 * offset ), POINT_TOLERANCE );
		Point2DAssert.assertThat( pane.localToParent( -1, -1 ) ).isCloseTo( new Point2D( 0.5 * width + offset, 0.5 * height + 2 * offset ), POINT_TOLERANCE );
		Point2DAssert.assertThat( pane.localToParent( 1, -1 ) ).isCloseTo( new Point2D( 0.5 * width + offset, 0.5 * height ), POINT_TOLERANCE );
	}

	@Test
	void testParentToLocal() {
		double width = PARENT_WIDTH;
		double height = PARENT_HEIGHT;

		double offset = DesignUnit.CENTIMETER.to( pane.getDpi() * pane.getZoom(), DesignUnit.INCH );

		assertThat( pane.parentToLocal( 0.5 * width, 0.5 * height ) ).isEqualTo( new Point2D( 0, 0 ) );
		Point2DAssert.assertThat( pane.parentToLocal( 0.5 * width + offset, 0.5 * height - offset ) ).isCloseTo( new Point2D( 1, 1 ) );
		Point2DAssert.assertThat( pane.parentToLocal( 0.5 * width - offset, 0.5 * height - offset ) ).isCloseTo( new Point2D( -1, 1 ) );
		Point2DAssert.assertThat( pane.parentToLocal( 0.5 * width - offset, 0.5 * height + offset ) ).isCloseTo( new Point2D( -1, -1 ) );
		Point2DAssert.assertThat( pane.parentToLocal( 0.5 * width + offset, 0.5 * height + offset ) ).isCloseTo( new Point2D( 1, -1 ) );
	}

}
