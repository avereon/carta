package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.Design2D;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.RenderConstants;
import com.avereon.cartesia.tool.Workplane;
import com.avereon.curve.math.Constants;
import javafx.beans.value.ChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.ref.WeakReference;

import static com.avereon.cartesia.TestConstants.EXTRA_LOOSE_TOLERANCE;
import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static com.avereon.cartesia.tool.RenderConstants.DEFAULT_DPI;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith( MockitoExtension.class )
public class DesignToolV3RendererTest {

	private DesignToolV3Renderer renderer;

	@Mock
	private ChangeListener<Number> xListener;

	@Mock
	private ChangeListener<Number> yListener;

	@Mock
	private ChangeListener<Number> zListener;

	@Mock
	private ChangeListener<Number> valueListener;

	@BeforeEach
	void setUp() {
		renderer = new DesignToolV3Renderer();
	}

	@Test
	void setWorkplane() {
		Workplane workplane = new Workplane();
		renderer.setWorkplane( workplane );
		assertThat( renderer.getWorkplane() ).isEqualTo( workplane );
	}

	@Test
	void setDesign() {
		Design design = new Design2D();
		renderer.setDesign( design );
		assertThat( renderer.getDesign() ).isEqualTo( design );
	}

	@Test
	void defaultDpi() {
		assertThat( renderer.getDpiX() ).isEqualTo( DEFAULT_DPI );
		assertThat( renderer.getDpiY() ).isEqualTo( DEFAULT_DPI );
	}

	@Test
	void defaultView() {
		assertThat( renderer.getViewCenter() ).isEqualTo( RenderConstants.DEFAULT_CENTER );
		assertThat( renderer.getViewRotate() ).isEqualTo( RenderConstants.DEFAULT_ROTATE );
		assertThat( renderer.getViewZoom() ).isEqualTo( RenderConstants.DEFAULT_ZOOM );
	}

	@Test
	void setDpi() {
		renderer.setDpi( 300 );
		assertThat( renderer.getDpiX() ).isEqualTo( 300 );
		assertThat( renderer.getDpiY() ).isEqualTo( 300 );

		renderer.setDpi( 150, 200 );
		assertThat( renderer.getDpiX() ).isEqualTo( 150 );
		assertThat( renderer.getDpiY() ).isEqualTo( 200 );

		renderer.setDpi( new Point2D( 100, 250 ) );
		assertThat( renderer.getDpiX() ).isEqualTo( 100 );
		assertThat( renderer.getDpiY() ).isEqualTo( 250 );
	}

	@Test
	void setDpiWithListeners() {
		// given
		renderer.dpiXProperty().addListener( xListener );
		renderer.dpiYProperty().addListener( yListener );

		// when
		renderer.setDpi( 200, 300 );

		// then
		verify( xListener, times( 1 ) ).changed( any(), any(), eq( 200.0 ) );
		verify( yListener, times( 1 ) ).changed( any(), any(), eq( 300.0 ) );
	}

	@Test
	void setViewCenter() {
		renderer.setViewCenter( 5, 10, 20 );
		assertThat( renderer.getViewCenterX() ).isEqualTo( 5 );
		assertThat( renderer.getViewCenterY() ).isEqualTo( 10 );
		assertThat( renderer.getViewCenterZ() ).isEqualTo( 20 );
		assertThat( renderer.getViewCenter() ).isEqualTo( new Point3D( 5, 10, 20 ) );

		renderer.setViewCenter( new Point3D( 10, 20, 30 ) );
		assertThat( renderer.getViewCenterX() ).isEqualTo( 10 );
		assertThat( renderer.getViewCenterY() ).isEqualTo( 20 );
		assertThat( renderer.getViewCenterZ() ).isEqualTo( 30 );
		assertThat( renderer.getViewCenter() ).isEqualTo( new Point3D( 10, 20, 30 ) );
	}

	@Test
	void setViewCenterWithListeners() {
		// given
		renderer.viewCenterXProperty().addListener( xListener );
		renderer.viewCenterYProperty().addListener( yListener );
		renderer.viewCenterZProperty().addListener( zListener );

		// when
		renderer.setViewCenter( 5, 10, 20 );

		// then
		verify( xListener, times( 1 ) ).changed( any(), any(), eq( 5.0 ) );
		verify( yListener, times( 1 ) ).changed( any(), any(), eq( 10.0 ) );
		verify( zListener, times( 1 ) ).changed( any(), any(), eq( 20.0 ) );
	}

	@Test
	void setViewCenterX() {
		double centerX = 5.0;
		renderer.setViewCenterX( centerX );
		assertThat( renderer.getViewCenterX() ).isEqualTo( centerX );
	}

	@Test
	void setViewCenterXWithListeners() {
		// given
		renderer.viewCenterXProperty().addListener( xListener );

		// when
		renderer.setViewCenterX( 5.0 );

		// then
		verify( xListener, times( 1 ) ).changed( any(), any(), eq( 5.0 ) );
	}

	@Test
	void setViewCenterY() {
		double centerY = 10.0;
		renderer.setViewCenterY( centerY );
		assertThat( renderer.getViewCenterY() ).isEqualTo( centerY );
	}

	@Test
	void setViewCenterYWithListeners() {
		// given
		renderer.viewCenterYProperty().addListener( yListener );

		// when
		renderer.setViewCenterY( 10.0 );

		// then
		verify( yListener, times( 1 ) ).changed( any(), any(), eq( 10.0 ) );
	}

	@Test
	void setViewCenterZ() {
		double centerZ = 20.0;
		renderer.setViewCenterZ( centerZ );
		assertThat( renderer.getViewCenterZ() ).isEqualTo( centerZ );
	}

	@Test
	void setViewCenterZWithListeners() {
		// given
		renderer.viewCenterZProperty().addListener( zListener );

		// when
		renderer.setViewCenterZ( 20.0 );

		// then
		verify( zListener, times( 1 ) ).changed( any(), any(), eq( 20.0 ) );
	}

	@Test
	void setViewRotate() {
		double rotate = 45.0;
		renderer.setViewRotate( rotate );
		assertThat( renderer.getViewRotate() ).isEqualTo( rotate );
	}

	@Test
	void setViewRotateWithListeners() {
		// given
		renderer.viewRotateProperty().addListener( valueListener );

		// when
		renderer.setViewRotate( 90.0 );

		// then
		verify( valueListener, times( 1 ) ).changed( any(), any(), eq( 90.0 ) );
	}

	@Test
	void setViewZoom() {
		renderer.setViewZoom( 2.0 );
		assertThat( renderer.getViewZoomX() ).isEqualTo( 2.0 );
		assertThat( renderer.getViewZoomY() ).isEqualTo( 2.0 );
		assertThat( renderer.getViewZoom() ).isEqualTo( new Point2D( 2.0, 2.0 ) );

		renderer.setViewZoom( 3, 4 );
		assertThat( renderer.getViewZoomX() ).isEqualTo( 3.0 );
		assertThat( renderer.getViewZoomY() ).isEqualTo( 4.0 );
		assertThat( renderer.getViewZoom() ).isEqualTo( new Point2D( 3.0, 4.0 ) );

		renderer.setViewZoom( new Point2D( 5, 6 ) );
		assertThat( renderer.getViewZoomX() ).isEqualTo( 5.0 );
		assertThat( renderer.getViewZoomY() ).isEqualTo( 6.0 );
		assertThat( renderer.getViewZoom() ).isEqualTo( new Point2D( 5.0, 6.0 ) );
	}

	@Test
	void setViewZoomWithListeners() {
		// given
		renderer.viewZoomXProperty().addListener( xListener );
		renderer.viewZoomYProperty().addListener( yListener );

		// when
		renderer.setViewZoom( 2.0, 3.0 );

		// then
		verify( xListener, times( 1 ) ).changed( any(), any(), eq( 2.0 ) );
		verify( yListener, times( 1 ) ).changed( any(), any(), eq( 3.0 ) );
	}

	@Test
	void setViewZoomX() {
		double zoomX = 2.0;
		renderer.setViewZoomX( zoomX );
		assertThat( renderer.getViewZoomX() ).isEqualTo( zoomX );
	}

	@Test
	void setViewZoomXWithListeners() {
		// given
		renderer.viewZoomXProperty().addListener( xListener );

		// when
		renderer.setViewZoomX( 2.0 );

		// then
		verify( xListener, times( 1 ) ).changed( any(), any(), eq( 2.0 ) );
	}

	@Test
	void setViewZoomY() {
		double zoomY = 3.0;
		renderer.setViewZoomY( zoomY );
		assertThat( renderer.getViewZoomY() ).isEqualTo( zoomY );
	}

	@Test
	void setViewZoomYWithListeners() {
		// given
		renderer.viewZoomYProperty().addListener( yListener );

		// when
		renderer.setViewZoomY( 3.0 );

		// then
		verify( yListener, times( 1 ) ).changed( any(), any(), eq( 3.0 ) );
	}

	@Test
	void getVisibleLayers() {

	}

	@Test
	void setVisibleLayers() {

	}

	@Test
	void setLayerVisible() {
		// given
		Design design = new Design2D();
		DesignLayer layer0 = new DesignLayer().setName( "layer0" ).setOrder( 0 );
		DesignLayer layer1 = new DesignLayer().setName( "layer1" ).setOrder( 1 );
		DesignLayer layer2 = new DesignLayer().setName( "layer2" ).setOrder( 2 );
		DesignLayer layer3 = new DesignLayer().setName( "layer3" ).setOrder( 3 );
		DesignLayer layer4 = new DesignLayer().setName( "layer4" ).setOrder( 4 );
		design.getLayers().addLayer( layer0 );
		design.getLayers().addLayer( layer1 );
		design.getLayers().addLayer( layer2 );
		design.getLayers().addLayer( layer3 );
		design.getLayers().addLayer( layer4 );
		renderer.setDesign( design );
		assertThat( design.getAllLayers().size() ).isEqualTo( 5 );
		assertThat( renderer.layersPane().getChildren().size() ).isEqualTo( 0 );

		// when
		renderer.setLayerVisible( layer1, true );
		renderer.setLayerVisible( layer3, true );
		// then
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer0 ) ).isEqualTo( -1 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer1 ) ).isEqualTo( 1 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer2 ) ).isEqualTo( -1 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer3 ) ).isEqualTo( 0 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer4 ) ).isEqualTo( -1 );
		assertThat( renderer.layersPane().getChildren().size() ).isEqualTo( 2 );

		// when
		renderer.setLayerVisible( layer2, true );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer0 ) ).isEqualTo( -1 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer1 ) ).isEqualTo( 2 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer2 ) ).isEqualTo( 1 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer3 ) ).isEqualTo( 0 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer4 ) ).isEqualTo( -1 );
		assertThat( renderer.layersPane().getChildren().size() ).isEqualTo( 3 );

		// when
		renderer.setLayerVisible( layer4, true );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer0 ) ).isEqualTo( -1 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer1 ) ).isEqualTo( 3 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer2 ) ).isEqualTo( 2 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer3 ) ).isEqualTo( 1 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer4 ) ).isEqualTo( 0 );
		assertThat( renderer.layersPane().getChildren().size() ).isEqualTo( 4 );

		// when
		renderer.setLayerVisible( layer4, false );
		// then
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer0 ) ).isEqualTo( -1 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer1 ) ).isEqualTo( 2 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer2 ) ).isEqualTo( 1 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer3 ) ).isEqualTo( 0 );
		assertThat( paneIndexOfDesignLayer( renderer.layersPane(), layer4 ) ).isEqualTo( -1 );
		assertThat( renderer.layersPane().getChildren().size() ).isEqualTo( 3 );
	}

	@Test
	void isLayerVisible() {
		Design design = new Design2D();
		DesignLayer layer0 = new DesignLayer().setName( "layer0" ).setOrder( 0 );
		DesignLayer layer1 = new DesignLayer().setName( "layer1" ).setOrder( 1 );
		DesignLayer layer2 = new DesignLayer().setName( "layer2" ).setOrder( 2 );
		DesignLayer layer3 = new DesignLayer().setName( "layer3" ).setOrder( 3 );
		DesignLayer layer4 = new DesignLayer().setName( "layer4" ).setOrder( 4 );
		design.getLayers().addLayer( layer0 );
		design.getLayers().addLayer( layer1 );
		design.getLayers().addLayer( layer2 );
		design.getLayers().addLayer( layer3 );
		design.getLayers().addLayer( layer4 );
		renderer.setDesign( design );
		assertThat( design.getAllLayers().size() ).isEqualTo( 5 );
		assertThat( renderer.layersPane().getChildren().size() ).isEqualTo( 0 );

		assertThat( renderer.isLayerVisible( layer0 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer1 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer2 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer3 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer4 ) ).isFalse();

		renderer.setLayerVisible( layer1, true );
		assertThat( renderer.isLayerVisible( layer0 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer1 ) ).isTrue();
		assertThat( renderer.isLayerVisible( layer2 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer3 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer4 ) ).isFalse();

		renderer.setLayerVisible( layer3, true );
		assertThat( renderer.isLayerVisible( layer0 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer1 ) ).isTrue();
		assertThat( renderer.isLayerVisible( layer2 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer3 ) ).isTrue();
		assertThat( renderer.isLayerVisible( layer4 ) ).isFalse();

		renderer.setLayerVisible( layer1, false );
		assertThat( renderer.isLayerVisible( layer0 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer1 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer2 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer3 ) ).isTrue();
		assertThat( renderer.isLayerVisible( layer4 ) ).isFalse();

		renderer.setLayerVisible( layer3, false );
		assertThat( renderer.isLayerVisible( layer0 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer1 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer2 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer3 ) ).isFalse();
		assertThat( renderer.isLayerVisible( layer4 ) ).isFalse();
	}

	/**
	 * This test ensures that FX geometry is resized when the DPI is changed in
	 * the renderer.
	 */
	@Test
	@Tag( "WhiteBox" )
	void updateDpi() {
		// given
		Design design = ExampleDesigns.redBlueX();
		renderer.setDesign( design );
		renderer.setLayerVisible( design.getLayers().getLayers().getFirst(), true );

		// Verify the FX geometry in the renderer
		Pane construction = (Pane)renderer.layersPane().getChildren().getFirst();
		Line redLine = (Line)construction.getChildren().get( 0 );
		Line greenLine = (Line)construction.getChildren().get( 1 );

		// The test values are based on 96 DPI and a design unit of CM
		// Verify the original bounds (these are design unit and DPI-dependent)
		Assertions.assertThat( renderer.getVisualBounds( redLine ) ).isEqualTo( new BoundingBox( -207.87400817871094, -207.87400817871094, 415.7480163574219, 415.7480163574219 ) );
		Assertions.assertThat( renderer.getVisualBounds( greenLine ) ).isEqualTo( new BoundingBox( -207.87400817871094, -207.87400817871094, 415.7480163574219, 415.7480163574219 ) );
		Assertions.assertThat( renderer.getVisualBounds( construction ) ).isEqualTo( new BoundingBox( -207.87400817871094, -207.87400817871094, 415.7480163574219, 415.7480163574219 ) );

		// when
		renderer.setDpi( 2 * DesignToolV3.DEFAULT_DPI );

		// then
		// The FX geometry should have changed in the renderer
		assertThat( renderer.getVisualBounds( redLine ) ).isEqualTo( new BoundingBox( -415.7480163574219, -415.7480163574219, 831.4960327148438, 831.4960327148438 ) );
		assertThat( renderer.getVisualBounds( greenLine ) ).isEqualTo( new BoundingBox( -415.7480163574219, -415.7480163574219, 831.4960327148438, 831.4960327148438 ) );
		assertThat( renderer.getVisualBounds( construction ) ).isEqualTo( new BoundingBox( -415.7480163574219, -415.7480163574219, 831.4960327148438, 831.4960327148438 ) );
	}

	/**
	 * This test ensures that FX geometry is resized when the design unit is changed
	 * in the design.
	 */
	@Test
	@Tag( "WhiteBox" )
	void updateDesignUnit() {
		// given
		Design design = ExampleDesigns.redBlueX();
		renderer.setDesign( design );
		renderer.setLayerVisible( design.getLayers().getLayers().getFirst(), true );

		// Verify the FX geometry in the renderer
		Pane construction = (Pane)renderer.layersPane().getChildren().getFirst();
		Line redLine = (Line)construction.getChildren().get( 0 );
		Line greenLine = (Line)construction.getChildren().get( 1 );

		// The test values are based on 96 DPI and a design unit of CM
		// Verify the original bounds (these are design unit and DPI-dependent)
		Assertions.assertThat( renderer.getVisualBounds( redLine ) ).isEqualTo( new BoundingBox( -207.87400817871094, -207.87400817871094, 415.7480163574219, 415.7480163574219 ) );
		Assertions.assertThat( renderer.getVisualBounds( greenLine ) ).isEqualTo( new BoundingBox( -207.87400817871094, -207.87400817871094, 415.7480163574219, 415.7480163574219 ) );
		Assertions.assertThat( renderer.getVisualBounds( construction ) ).isEqualTo( new BoundingBox( -207.87400817871094, -207.87400817871094, 415.7480163574219, 415.7480163574219 ) );

		// when
		design.setDesignUnit( DesignUnit.MM );

		// then
		// The FX geometry should have changed in the renderer
		Assertions.assertThat( renderer.getVisualBounds( redLine ) ).isEqualTo( new BoundingBox( -20.78740119934082, -20.78740119934082, 41.57480239868164, 41.57480239868164 ) );
		Assertions.assertThat( renderer.getVisualBounds( greenLine ) ).isEqualTo( new BoundingBox( -20.78740119934082, -20.78740119934082, 41.57480239868164, 41.57480239868164 ) );
		Assertions.assertThat( renderer.getVisualBounds( construction ) ).isEqualTo( new BoundingBox( -20.78740119934082, -20.78740119934082, 41.57480239868164, 41.57480239868164 ) );
	}

	@Test
	void screenToWorld() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );

		// when
		Bounds bounds = renderer.screenToWorld( new BoundingBox( -100, -100, 200, 200 ) );

		// then
		assertThat( bounds.getMinX() ).isEqualTo( -100 / gz );
		assertThat( bounds.getMinY() ).isEqualTo( -100 / gz );
		assertThat( bounds.getMaxX() ).isEqualTo( 100 / gz );
		assertThat( bounds.getMaxY() ).isEqualTo( 100 / gz );
		assertThat( bounds.getWidth() ).isEqualTo( 200 / gz );
		assertThat( bounds.getHeight() ).isEqualTo( 200 / gz );
	}

	@Test
	void screenToWorldWithScale() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		renderer.setViewZoom( 2 );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );
		double scale = 0.5;

		Bounds bounds = renderer.screenToWorld( new BoundingBox( -100, -100, 200, 200 ) );

		assertThat( bounds.getMinX() ).isEqualTo( scale * -100 / gz, TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( scale * -100 / gz, TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( scale * 100 / gz, TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( scale * 100 / gz, TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( scale * 200 / gz, TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( scale * 200 / gz, TOLERANCE );
	}

	@Test
	void screenToWorldWithRotation() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		renderer.setViewRotate(45 );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );
		double scale = Constants.SQRT_TWO;

		Bounds bounds = renderer.screenToWorld( new BoundingBox( -100, -100, 200, 200 ) );

		assertThat( bounds.getMinX() ).isEqualTo( scale * -100 / gz, EXTRA_LOOSE_TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( scale * -100 / gz, EXTRA_LOOSE_TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( scale * 100 / gz, EXTRA_LOOSE_TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( scale * 100 / gz, EXTRA_LOOSE_TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( scale * 200 / gz, EXTRA_LOOSE_TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( scale * 200 / gz, EXTRA_LOOSE_TOLERANCE );
	}

	private int paneIndexOfDesignLayer( Pane pane, DesignLayer layer ) {
		WeakReference<Pane> weakLayer = layer.getValue( DesignToolV3Renderer.FX_SHAPE );
		if( weakLayer == null ) return -1;
		return pane.getChildren().indexOf( weakLayer.get() );
	}
}
