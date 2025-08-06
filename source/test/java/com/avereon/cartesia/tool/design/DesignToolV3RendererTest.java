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
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.transform.Transform;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.ref.WeakReference;
import java.util.stream.Stream;

import static com.avereon.cartesia.TestConstants.*;
import static com.avereon.cartesia.tool.RenderConstants.DEFAULT_DPI;
import static com.avereon.cartesia.tool.RenderConstants.DEFAULT_OUTPUT_SCALE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
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
	void internalPanelLayout() {
		// given
		double width = 1000;
		double height = 1000;
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );

		// when
		renderer.resizeRelocate( 0, 0, width, height );
		renderer.layout();

		// then
		assertBounds( renderer.getGrid(), 0, 0, width, height );
		assertBounds( renderer.getLayers(), 0, 0, width, height );
		assertBounds( renderer.getPreview(), 0, 0, width, height );
		assertBounds( renderer.getReference(), 0, 0, width, height );
		assertBounds( renderer.getWorld(), 0, 0, width, height );
		assertBounds( renderer, 0, 0, width, height );
	}

	@Test
	void setDesign() {
		Design design = new Design2D();
		renderer.setDesign( design );
		assertThat( renderer.getDesign() ).isEqualTo( design );
	}

	@Test
	void setDesignWithNull() {
		// given
		Design design = new Design2D();
		renderer.setDesign( design );
		assertThat( renderer.getDesign() ).isEqualTo( design );

		// when
		renderer.setDesign( null );

		// then
		assertThat( renderer.getDesign() ).isEqualTo( null );
	}

	@Test
	void setWorkplane() {
		Workplane workplane = new Workplane();
		renderer.setWorkplane( workplane );
		assertThat( renderer.getWorkplane() ).isEqualTo( workplane );
	}

	@Test
	void setWorkplaneWithNull() {
		// given
		Workplane workplane = new Workplane();
		renderer.setWorkplane( workplane );
		assertThat( renderer.getWorkplane() ).isEqualTo( workplane );

		// when
		renderer.setWorkplane( null );

		// then
		assertThat( renderer.getWorkplane() ).isEqualTo( null );
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
		renderer.setDpi( 150, 200 );
		assertThat( renderer.getDpiX() ).isEqualTo( 150 );
		assertThat( renderer.getDpiY() ).isEqualTo( 200 );
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
		renderer.setViewZoom( 3, 4 );
		assertThat( renderer.getViewZoomX() ).isEqualTo( 3.0 );
		assertThat( renderer.getViewZoomY() ).isEqualTo( 4.0 );

		renderer.setViewZoom( new Point2D( 5, 6 ) );
		assertThat( renderer.getViewZoomX() ).isEqualTo( 5.0 );
		assertThat( renderer.getViewZoomY() ).isEqualTo( 6.0 );
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
	void zoomIn() {
		// given
		double factor = DesignToolV3.ZOOM_IN_FACTOR;

		// when
		renderer.zoom( new Point3D( -2, -2, 0 ), factor );

		// then
		assertThat( renderer.getViewZoomX() ).isEqualTo( 1.189207115002721 );
		assertThat( renderer.getViewZoomY() ).isEqualTo( 1.189207115002721 );
		assertThat( renderer.getViewCenterX() ).isEqualTo( -0.3182071694925708 );
		assertThat( renderer.getViewCenterY() ).isEqualTo( -0.3182071694925708 );
		assertThat( renderer.getViewCenterZ() ).isEqualTo( 0.0 );
	}

	@Test
	void zoomOut() {
		// given
		double factor = DesignToolV3.ZOOM_OUT_FACTOR;

		// when
		renderer.zoom( new Point3D( -2, -2, 0 ), factor );

		// then
		assertThat( renderer.getViewZoomX() ).isEqualTo( 0.8408964152537146 );
		assertThat( renderer.getViewZoomY() ).isEqualTo( 0.8408964152537146 );
		assertThat( renderer.getViewCenterX() ).isEqualTo( 0.37841423000544205 );
		assertThat( renderer.getViewCenterY() ).isEqualTo( 0.37841423000544205 );
		assertThat( renderer.getViewCenterZ() ).isEqualTo( 0.0 );
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
		renderer.setDpi( 2 * DesignToolV3.DEFAULT_DPI, 2 * DesignToolV3.DEFAULT_DPI );

		// then
		// The FX geometry should have changed in the renderer
		assertThat( renderer.getVisualBounds( redLine ) ).isEqualTo( new BoundingBox( -415.7480163574219, -415.7480163574219, 831.4960327148438, 831.4960327148438 ) );
		assertThat( renderer.getVisualBounds( greenLine ) ).isEqualTo( new BoundingBox( -415.7480163574219, -415.7480163574219, 831.4960327148438, 831.4960327148438 ) );
		assertThat( renderer.getVisualBounds( construction ) ).isEqualTo( new BoundingBox( -415.7480163574219, -415.7480163574219, 831.4960327148438, 831.4960327148438 ) );
	}

	@Test
	@Tag( "WhiteBox" )
	void updateOutputScale() {
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
		renderer.setOutputScale( 1.5 * DEFAULT_OUTPUT_SCALE, 1.5 * DEFAULT_OUTPUT_SCALE );

		// then
		// The FX geometry should have changed in the renderer
		assertThat( renderer.getVisualBounds( redLine ) ).isEqualTo( new BoundingBox( -311.81103515625, -311.81103515625, 623.6220703125, 623.6220703125 ) );
		assertThat( renderer.getVisualBounds( greenLine ) ).isEqualTo( new BoundingBox( -311.81103515625, -311.81103515625, 623.6220703125, 623.6220703125 ) );
		assertThat( renderer.getVisualBounds( construction ) ).isEqualTo( new BoundingBox( -311.81103515625, -311.81103515625, 623.6220703125, 623.6220703125 ) );
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
		assertThat( bounds.getMinX() ).isEqualTo( -100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( -100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( 100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( 100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( 200 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( 200 / gz, TIGHT_TOLERANCE );
	}

	@Test
	void screenToWorldWithDoubleDouble() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );

		// when
		Point2D worldPoint = renderer.screenToWorld( 100, 200 );

		// then
		assertThat( worldPoint.getX() ).isEqualTo( 100 / gz, TIGHT_TOLERANCE );
		assertThat( worldPoint.getY() ).isEqualTo( -200 / gz, TIGHT_TOLERANCE );
	}

	@Test
	void screenToWorldWithPoint2D() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );

		// when
		Point2D worldPoint = renderer.screenToWorld( new Point2D( 200, 100 ) );

		// then
		assertThat( worldPoint.getX() ).isEqualTo( 200 / gz, TIGHT_TOLERANCE );
		assertThat( worldPoint.getY() ).isEqualTo( -100 / gz, TIGHT_TOLERANCE );
	}

	@Test
	void screenToWorldWithDoubleDoubleDouble() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );

		// when
		Point3D worldPoint = renderer.screenToWorld( 100, 200, 300 );

		// then
		assertThat( worldPoint.getX() ).isEqualTo( 100 / gz, TIGHT_TOLERANCE );
		assertThat( worldPoint.getY() ).isEqualTo( -200 / gz, TIGHT_TOLERANCE );
		assertThat( worldPoint.getZ() ).isEqualTo( 300, TIGHT_TOLERANCE );
	}

	@Test
	void screenToWorldWithPoint3D() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );

		// when
		Point3D worldPoint = renderer.screenToWorld( new Point3D( 200, 300, 100 ) );

		// then
		assertThat( worldPoint.getX() ).isEqualTo( 200 / gz, TIGHT_TOLERANCE );
		assertThat( worldPoint.getY() ).isEqualTo( -300 / gz, TIGHT_TOLERANCE );
		assertThat( worldPoint.getZ() ).isEqualTo( 100, TIGHT_TOLERANCE );
	}

	@Test
	void screenToWorldWithTransformButNotSize() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );
		// An output scale should not alter the test results
		renderer.setOutputScale( 1.5, 1.5 );

		// when
		Transform transform = renderer.getScreenToWorldTransform();
		Bounds bounds = transform.transform( new BoundingBox( -100, -100, 200, 200 ) );

		// then
		assertThat( bounds.getMinX() ).isEqualTo( -100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( -100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( 100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( 100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( 200 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( 200 / gz, TIGHT_TOLERANCE );
	}

	@Test
	void screenToWorldWithZoomButNotSize() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		renderer.setViewZoom( 2, 2 );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );
		double scale = 0.5;

		Bounds bounds = renderer.screenToWorld( new BoundingBox( -100, -100, 200, 200 ) );

		assertThat( bounds.getMinX() ).isEqualTo( scale * -100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( scale * -100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( scale * 100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( scale * 100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( scale * 200 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( scale * 200 / gz, TIGHT_TOLERANCE );
	}

	@Test
	void screenToWorldWithRotateButNotSize() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		renderer.setViewRotate( 45 );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );
		double scale = Constants.SQRT_TWO;

		Bounds bounds = renderer.screenToWorld( new BoundingBox( -100, -100, 200, 200 ) );

		assertThat( bounds.getMinX() ).isCloseTo( scale * -100 / gz, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMinY() ).isCloseTo( scale * -100 / gz, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMaxX() ).isCloseTo( scale * 100 / gz, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMaxY() ).isCloseTo( scale * 100 / gz, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getWidth() ).isCloseTo( scale * 200 / gz, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getHeight() ).isCloseTo( scale * 200 / gz, TOLERANCE_PERCENT_LOOSE );
	}

	@Test
	void worldToScreen() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );

		// when
		Bounds bounds = renderer.worldToScreen( new BoundingBox( -100, -100, 200, 200 ) );

		// then
		assertThat( bounds.getMinX() ).isEqualTo( -100 * gz );
		assertThat( bounds.getMinY() ).isEqualTo( -100 * gz );
		assertThat( bounds.getMaxX() ).isEqualTo( 100 * gz );
		assertThat( bounds.getMaxY() ).isEqualTo( 100 * gz );
		assertThat( bounds.getWidth() ).isEqualTo( 200 * gz );
		assertThat( bounds.getHeight() ).isEqualTo( 200 * gz );
	}

	@Test
	void worldToScreenWithDoubleDouble() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );

		// when
		Point2D screenPoint = renderer.worldToScreen( 100, 200 );

		// then
		assertThat( screenPoint.getX() ).isEqualTo( 100 * gz );
		assertThat( screenPoint.getY() ).isEqualTo( -200 * gz );
	}

	@Test
	void worldToScreenWithPoint2D() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );

		// when
		Point2D screenPoint = renderer.worldToScreen( new Point2D( 200, 100 ) );

		// then
		assertThat( screenPoint.getX() ).isEqualTo( 200 * gz );
		assertThat( screenPoint.getY() ).isEqualTo( -100 * gz );
	}

	@Test
	void worldToScreenWithDoubleDoubleDouble() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );

		// when
		Point3D screenPoint = renderer.worldToScreen( 200, 300, 100 );

		// then
		assertThat( screenPoint.getX() ).isEqualTo( 200 * gz );
		assertThat( screenPoint.getY() ).isEqualTo( -300 * gz );
		assertThat( screenPoint.getZ() ).isEqualTo( 100 );
	}

	@Test
	void worldToScreenWithPoint3D() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );

		// when
		Point3D screenPoint = renderer.worldToScreen( new Point3D( 300, 200, 100 ) );

		// then
		assertThat( screenPoint.getX() ).isEqualTo( 300 * gz );
		assertThat( screenPoint.getY() ).isEqualTo( -200 * gz );
		assertThat( screenPoint.getZ() ).isEqualTo( 100 );
	}

	@Test
	void worldToScreenTransform() {
		// given
		double width = 1000;
		double height = 800;
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		renderer.resizeRelocate( 0, 0, width, height );
		renderer.layout();

		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );

		assertThat( renderer.worldToScreen( 0, 0 ).getX() ).isEqualTo( 500 );
		assertThat( renderer.worldToScreen( 0, 0 ).getY() ).isEqualTo( 400 );

		// when
		renderer.setViewCenter( 2, 2, 0 );

		// then
		assertThat( renderer.worldToScreen( 0, 0 ).getX() ).isCloseTo( 500 - 2 * gz, TOLERANCE );
		assertThat( renderer.worldToScreen( 0, 0 ).getY() ).isEqualTo( 400 + 2 * gz, TOLERANCE );
	}

	@Test
	void worldToScreenZoom() {
		// given
		double width = 1000;
		double height = 800;
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		renderer.resizeRelocate( 0, 0, width, height );
		renderer.layout();

		assertThat( renderer.worldToScreen( 0, 0 ).getX() ).isEqualTo( 500 );
		assertThat( renderer.worldToScreen( 0, 0 ).getY() ).isEqualTo( 400 );

		// when
		renderer.setViewZoom( new Point2D( 2, 2 ) );

		// then
		assertThat( renderer.worldToScreen( 0, 0 ).getX() ).isEqualTo( 500 );
		assertThat( renderer.worldToScreen( 0, 0 ).getY() ).isEqualTo( 400 );
	}

	@Test
	void worldToScreenWithTransformButNotSize() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );
		// An output scale should not alter the test results
		renderer.setOutputScale( 1.5, 1.5 );

		// when
		Transform transform = renderer.getWorldToScreenTransform();
		Bounds bounds = transform.transform( new BoundingBox( -100, -100, 200, 200 ) );

		// then
		assertThat( bounds.getMinX() ).isEqualTo( -100 * gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( -100 * gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( 100 * gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( 100 * gz, TIGHT_TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( 200 * gz, TIGHT_TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( 200 * gz, TIGHT_TOLERANCE );
	}

	@Test
	void worldToScreenWithZoomButNotSize() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		renderer.setViewZoom( 2, 2 );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );
		double scale = 2;

		// when
		Bounds bounds = renderer.worldToScreen( new BoundingBox( -100, -100, 200, 200 ) );

		// then
		assertThat( bounds.getMinX() ).isEqualTo( scale * -100 * gz );
		assertThat( bounds.getMinY() ).isEqualTo( scale * -100 * gz );
		assertThat( bounds.getMaxX() ).isEqualTo( scale * 100 * gz );
		assertThat( bounds.getMaxY() ).isEqualTo( scale * 100 * gz );
		assertThat( bounds.getWidth() ).isEqualTo( scale * 200 * gz );
		assertThat( bounds.getHeight() ).isEqualTo( scale * 200 * gz );
	}

	@Test
	void worldToScreenWithRotateButNotSize() {
		// given
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		renderer.setViewRotate( 45 );
		double gz = 96 * DesignUnit.CM.to( 1, DesignUnit.IN );
		double scale = Constants.SQRT_TWO;

		Bounds bounds = renderer.worldToScreen( new BoundingBox( -100, -100, 200, 200 ) );

		assertThat( bounds.getMinX() ).isCloseTo( scale * -100 * gz, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMinY() ).isCloseTo( scale * -100 * gz, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMaxX() ).isCloseTo( scale * 100 * gz, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMaxY() ).isCloseTo( scale * 100 * gz, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getWidth() ).isCloseTo( scale * 200 * gz, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getHeight() ).isCloseTo( scale * 200 * gz, TOLERANCE_PERCENT_LOOSE );
	}

	@ParameterizedTest
	@MethodSource
	void worldToScreenDoesNotChangeWithDifferentOutputScales( double outputScale, Point2D point, Point2D expected ) {
		// given
		double width = 1000;
		double height = 1000;
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		renderer.resizeRelocate( 0, 0, width, height );
		renderer.layout();

		// when
		renderer.setOutputScale( outputScale, outputScale );
		Point2D actual = renderer.worldToScreen( point );

		// then
		assertThat( actual.getX() ).isCloseTo( expected.getX(), TOLERANCE );
		assertThat( actual.getY() ).isCloseTo( expected.getY(), TOLERANCE );
	}

	@Test
	void updateGridFxGeometryDoesNotThrowNpe() {
		Throwable throwable = catchThrowable( () -> renderer.updateGridFxGeometry() );
		assertThat( throwable ).isNull();
	}

	private static Stream<Arguments> worldToScreenDoesNotChangeWithDifferentOutputScales() {
		return Stream.of(
			Arguments.of( 1, new Point2D( -1, 0 ), new Point2D( 462.20472440944883, 500 ) ),
			Arguments.of( 1.25, new Point2D( -1, 0 ), new Point2D( 462.20472440944883, 500 ) ),
			Arguments.of( 1.5, new Point2D( -1, 0 ), new Point2D( 462.20472440944883, 500 ) ),
			Arguments.of( 1.75, new Point2D( -1, 0 ), new Point2D( 462.20472440944883, 500 ) ),
			Arguments.of( 2, new Point2D( -1, 0 ), new Point2D( 462.20472440944883, 500 ) ),
			Arguments.of( 2.25, new Point2D( -1, 0 ), new Point2D( 462.20472440944883, 500 ) ),
			Arguments.of( 2.5, new Point2D( -1, 0 ), new Point2D( 462.20472440944883, 500 ) ),
			Arguments.of( 2.75, new Point2D( -1, 0 ), new Point2D( 462.20472440944883, 500 ) ),
			Arguments.of( 3, new Point2D( -1, 0 ), new Point2D( 462.20472440944883, 500 ) )
		);
	}

	private int paneIndexOfDesignLayer( Pane pane, DesignLayer layer ) {
		WeakReference<Pane> weakLayer = layer.getValue( DesignToolV3Renderer.FX_SHAPE );
		if( weakLayer == null ) return -1;
		return pane.getChildren().indexOf( weakLayer.get() );
	}

	private void assertBounds( Region region, double x, double y, double width, double height ) {
		assertThat( region.getLayoutX() ).isEqualTo( 0 );
		assertThat( region.getLayoutY() ).isEqualTo( 0 );
		assertThat( region.getWidth() ).isEqualTo( width );
		assertThat( region.getHeight() ).isEqualTo( height );
	}

}
