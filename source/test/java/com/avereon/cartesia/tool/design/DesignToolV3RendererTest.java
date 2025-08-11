package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.CartesiaTestTag;
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

import static com.avereon.cartesia.TestConstants.TIGHT_TOLERANCE;
import static com.avereon.cartesia.TestConstants.TOLERANCE;
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

	static final double width = 1000;

	static final double height = 1000;

	static final double gz = RenderConstants.DEFAULT_DPI * DesignUnit.CM.to( 1, DesignUnit.IN );

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
		renderer.resizeRelocate( 0, 0, width, height );
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
		renderer.layout();
	}

	@Test
	void internalPanelLayout() {
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

	/**
	 * This test ensures that FX geometry is resized when the DPI is changed in
	 * the renderer.
	 */
	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
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
	@Tag( CartesiaTestTag.WHITE_BOX )
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
	@Tag( CartesiaTestTag.WHITE_BOX )
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
	@Tag( CartesiaTestTag.WHITE_BOX )
	void shapeScaleX() {
		// given
		double dpi = 96;
		double unitScale = DesignUnit.CM.to( 1, DesignUnit.IN );
		double outputScale = 3;
		double expectedShapeScaleX = dpi * unitScale * outputScale;

		renderer.setDpi( dpi, dpi );
		renderer.setUnitScale( unitScale );
		renderer.setOutputScale( outputScale, outputScale );

		// when
		double shapeScaleX = renderer.getShapeScaleX();

		// then
		assertThat( shapeScaleX ).isEqualTo( expectedShapeScaleX, TOLERANCE );
	}

	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
	void shapeScaleY() {
		// given
		double dpi = 96;
		double unitScale = DesignUnit.CM.to( 1, DesignUnit.IN );
		double outputScale = 3;
		double expectedShapeScaleY = dpi * unitScale * outputScale;

		renderer.setDpi( dpi, dpi );
		renderer.setUnitScale( unitScale );
		renderer.setOutputScale( outputScale, outputScale );

		// when
		double shapeScaleY = renderer.getShapeScaleY();

		// then
		assertThat( shapeScaleY ).isEqualTo( expectedShapeScaleY, TOLERANCE );
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

	// FIXME This test does not show the "set view point" problem seen when changing the view point with the camera viewpoint tool

	@Test
	void outputScaleAppliedCorrectly() {
		// given
		double worldX = 1;
		double worldY = 1;
		double dpi = 96;
		double unitScale = DesignUnit.CM.to( 1, DesignUnit.IN );
		double outputScale = 3;
		double expectedShapeScaleX = dpi * unitScale * outputScale;
		double expectedShapeScaleY = dpi * unitScale * outputScale;

		renderer.setDpi( dpi, dpi );
		renderer.setUnitScale( unitScale );
		renderer.setOutputScale( outputScale, outputScale );

		// when
		// Compute the shape coordinates the renderer should use
		double shapeX = worldX * renderer.getShapeScaleX();
		double shapeY = worldY * renderer.getShapeScaleY();

		// then
		// Verify the shape coordinates are correctly scaled
		assertThat( shapeX ).isEqualTo( worldX * expectedShapeScaleX, TOLERANCE );
		assertThat( shapeY ).isEqualTo( worldY * expectedShapeScaleY, TOLERANCE );

		// given
		// Take out the output scale from the shape coordinates to make screen coordinates
		double shapeScreenX = shapeX / outputScale;
		double shapeScreenY = shapeY / outputScale;
		assertThat( shapeScreenX ).isCloseTo( worldX * dpi * unitScale, TOLERANCE );
		assertThat( shapeScreenY ).isCloseTo( worldY * dpi * unitScale, TOLERANCE );

		// when
		// Compute the shape screen coordinates to world coordinates
		Point2D worldPoint = renderer.screenToWorld( 500 + shapeScreenX, 500 - shapeScreenY );

		// then
		// Verify the screen coordinates are correctly converted to world coordinates
		assertThat( worldPoint.getX() ).isEqualTo( worldX, TOLERANCE );
		assertThat( worldPoint.getY() ).isEqualTo( worldY, TOLERANCE );
	}

	@Test
	void screenToWorldWithBounds() {
		// given
		assertThat( renderer.getDpiX() ).isEqualTo( RenderConstants.DEFAULT_DPI );

		// when
		Bounds bounds = renderer.screenToWorld( new BoundingBox( 400, 400, 200, 200 ) );

		// then
		assertThat( bounds.getMinX() ).isEqualTo( -100 / gz, TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( -100 / gz, TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( 100 / gz, TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( 100 / gz, TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( 200 / gz, TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( 200 / gz, TOLERANCE );
	}

	@Test
	void screenToWorldWithDoubleDouble() {
		// when
		Point2D worldPoint = renderer.screenToWorld( 600, 700 );

		// then
		assertThat( worldPoint.getX() ).isEqualTo( 100 / gz, TIGHT_TOLERANCE );
		assertThat( worldPoint.getY() ).isEqualTo( -200 / gz, TIGHT_TOLERANCE );
	}

	@Test
	void screenToWorldWithPoint2D() {
		// when
		Point2D worldPoint = renderer.screenToWorld( new Point2D( 700, 600 ) );

		// then
		assertThat( worldPoint.getX() ).isEqualTo( 200 / gz, TIGHT_TOLERANCE );
		assertThat( worldPoint.getY() ).isEqualTo( -100 / gz, TIGHT_TOLERANCE );
	}

	@Test
	void screenToWorldWithDoubleDoubleDouble() {
		// when
		Point3D worldPoint = renderer.screenToWorld( 600, 700, 300 );

		// then
		assertThat( worldPoint.getX() ).isEqualTo( 100 / gz, TIGHT_TOLERANCE );
		assertThat( worldPoint.getY() ).isEqualTo( -200 / gz, TIGHT_TOLERANCE );
		assertThat( worldPoint.getZ() ).isEqualTo( 300, TIGHT_TOLERANCE );
	}

	@Test
	void screenToWorldWithPoint3D() {
		// when
		Point3D worldPoint = renderer.screenToWorld( new Point3D( 700, 800, 100 ) );

		// then
		assertThat( worldPoint.getX() ).isEqualTo( 200 / gz, TOLERANCE );
		assertThat( worldPoint.getY() ).isEqualTo( -300 / gz, TOLERANCE );
		assertThat( worldPoint.getZ() ).isEqualTo( 100, TOLERANCE );
	}

	@ParameterizedTest
	@MethodSource
	void screenToWorldWithCenter( double centerX, double centerY, double screenX, double screenY, double worldX, double worldY ) {
		// given
		assertThat( renderer.screenToWorld( 500, 500 ).getX() ).isCloseTo( 0, TOLERANCE );
		assertThat( renderer.screenToWorld( 500, 500 ).getY() ).isCloseTo( 0, TOLERANCE );

		// when
		renderer.setViewCenter( centerX, centerY, 0 );

		// then
		assertThat( renderer.screenToWorld( screenX, screenY ).getX() ).isCloseTo( worldX, TOLERANCE );
		assertThat( renderer.screenToWorld( screenX, screenY ).getY() ).isCloseTo( worldY, TOLERANCE );
	}

	private static Stream<Arguments> screenToWorldWithCenter() {
		return Stream.of(
			Arguments.arguments( 2, 2, 500, 500, 2, 2 ),
			Arguments.arguments( 2, 2, 500, 500 - 2 * gz, 2, 4 ),
			Arguments.arguments( 2, 2, 500 + 2 * gz, 500 - 2 * gz, 4, 4 ),
			Arguments.arguments( 2, 2, 500 + 2 * gz, 500, 4, 2 ),
			Arguments.arguments( 2, 2, 500 + 2 * gz, 500 + 2 * gz, 4, 0 ),
			Arguments.arguments( 2, 2, 500, 500 + 2 * gz, 2, 0 ),
			Arguments.arguments( 2, 2, 500 - 2 * gz, 500 + 2 * gz, 0, 0 ),
			Arguments.arguments( 2, 2, 500 - 2 * gz, 500, 0, 2 ),
			Arguments.arguments( 2, 2, 500 - 2 * gz, 500 - 2 * gz, 0, 4 )
		);
	}

	@Test
	void screenToWorldWithZoom() {
		// given
		renderer.resizeRelocate( 0, 0, 1000, 800 );
		assertThat( renderer.screenToWorld( 500, 400 ).getX() ).isCloseTo( 0, TOLERANCE );
		assertThat( renderer.screenToWorld( 500, 400 ).getY() ).isCloseTo( 0, TOLERANCE );

		// when
		renderer.setViewZoom( new Point2D( 2, 2 ) );

		// then
		assertThat( renderer.screenToWorld( 500, 400 ).getX() ).isCloseTo( 0, TOLERANCE );
		assertThat( renderer.screenToWorld( 500, 400 ).getY() ).isCloseTo( 0, TOLERANCE );
	}

	//	@Test
	//	void screenToWorldWithRotate() {
	//		// given
	//		renderer.resizeRelocate( 0, 0, 1000, 800 );
	//		assertThat( renderer.screenToWorld( 500, 400 ).getX() ).isCloseTo( 0, TOLERANCE );
	//		assertThat( renderer.screenToWorld( 500, 400 ).getY() ).isCloseTo( 0, TOLERANCE );
	//
	//		// when
	//		renderer.setViewRotate( 20 );
	//
	//		// then
	//		assertThat( renderer.screenToWorld( 500, 400 ).getX() ).isCloseTo( 0, TOLERANCE );
	//		assertThat( renderer.screenToWorld( 500, 400 ).getY() ).isCloseTo( 0, TOLERANCE );
	//	}
	//
	@Test
	void screenToWorldWith() {
		// given
		assertThat( renderer.screenToWorld( 500, 500 ).getX() ).isCloseTo( 0, TOLERANCE );
		assertThat( renderer.screenToWorld( 500, 500 ).getY() ).isCloseTo( 0, TOLERANCE );

		// when
		Transform transform = renderer.getScreenToWorldTransform();
		Bounds bounds = transform.transform( new BoundingBox( 400, 400, 200, 200 ) );

		// then
		assertThat( bounds.getMinX() ).isEqualTo( -100 / gz, TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( -100 / gz, TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( 100 / gz, TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( 100 / gz, TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( 200 / gz, TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( 200 / gz, TOLERANCE );
	}

	@Test
	void screenToWorldZoom() {
		// given
		renderer.setViewZoom( 2, 2 );
		double scale = 0.5;

		// when
		Bounds bounds = renderer.screenToWorld( new BoundingBox( 400, 400, 200, 200 ) );

		// then
		assertThat( bounds.getMinX() ).isEqualTo( scale * -100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( scale * -100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( scale * 100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( scale * 100 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( scale * 200 / gz, TIGHT_TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( scale * 200 / gz, TIGHT_TOLERANCE );
	}

	@ParameterizedTest
	@MethodSource
	void screenToWorldWithRotate( double rotate, double centerX, double centerY, double screenX, double screenY, double worldX, double worldY ) {
		// given
		assertThat( renderer.screenToWorld( 500, 500 ).getX() ).isCloseTo( 0, TOLERANCE );
		assertThat( renderer.screenToWorld( 500, 500 ).getY() ).isCloseTo( 0, TOLERANCE );

		// when
		renderer.setViewCenter( centerX, centerY, 0 );
		renderer.setViewRotate( rotate );
		Point2D result = renderer.screenToWorld( new Point2D( screenX, screenY ) );

		// then
		assertThat( result.getX() ).isCloseTo( worldX, TOLERANCE );
		assertThat( result.getY() ).isCloseTo( worldY, TOLERANCE );
	}

	private static Stream<Arguments> screenToWorldWithRotate() {
		return Stream.of(
			Arguments.arguments( 0, 0, 0, 500, 500, 0, 0 ),
			// No angle should change the center
			//			Arguments.arguments( -180, 0, 0, 500, 500, 0, 0 ),
			//			Arguments.arguments( -135, 1, 2, 500, 500, 1, 2 ),
			//			Arguments.arguments( -90, 2, 3, 500, 500, 2, 3 ),
			//			Arguments.arguments( -45, 3, 4, 500, 500, 3, 4 ),
			Arguments.arguments( 0, 4, 5, 500, 500, 4, 5 ),
			//			Arguments.arguments( 45, 5, 6, 500, 500, 5, 6 ),
			//			Arguments.arguments( 90, 6, 7, 500, 500, 6, 7 ),
			//			Arguments.arguments( 135, 7, 8, 500, 500, 7, 8 ),
			//			Arguments.arguments( 180, 8, 9, 500, 500, 8, 9 ),

			Arguments.arguments( 45, 0, 0, 500, 500 - 1 * gz, Constants.SQRT_ONE_HALF, Constants.SQRT_ONE_HALF )
		);
	}

	@Test
	void worldToScreenWithBounds() {
		// given
		assertThat( renderer.worldToScreen( 0, 0 ).getX() ).isEqualTo( 500 );
		assertThat( renderer.worldToScreen( 0, 0 ).getY() ).isEqualTo( 500 );

		// when
		Bounds bounds = renderer.worldToScreen( new BoundingBox( -1, -1, 2, 2 ) );

		// then
		assertThat( bounds.getMinX() ).isCloseTo( 500 - 1 * gz, TOLERANCE );
		assertThat( bounds.getMinY() ).isCloseTo( 500 - 1 * gz, TOLERANCE );
		assertThat( bounds.getMaxX() ).isCloseTo( 500 + 1 * gz, TOLERANCE );
		assertThat( bounds.getMaxY() ).isCloseTo( 500 + 1 * gz, TOLERANCE );
		assertThat( bounds.getWidth() ).isCloseTo( 2 * gz, TOLERANCE );
		assertThat( bounds.getHeight() ).isCloseTo( 2 * gz, TOLERANCE );
	}

	@Test
	void worldToScreenWithDoubleDouble() {
		// given
		assertThat( renderer.worldToScreen( 0, 0 ).getX() ).isEqualTo( 500 );
		assertThat( renderer.worldToScreen( 0, 0 ).getY() ).isEqualTo( 500 );

		// when
		Point2D screenPoint = renderer.worldToScreen( 1, 2 );

		// then
		assertThat( screenPoint.getX() ).isCloseTo( 500 + 1 * gz, TOLERANCE );
		assertThat( screenPoint.getY() ).isCloseTo( 500 - 2 * gz, TOLERANCE );
	}

	@Test
	void worldToScreenWithPoint2D() {
		// given
		assertThat( renderer.worldToScreen( 0, 0 ).getX() ).isEqualTo( 500 );
		assertThat( renderer.worldToScreen( 0, 0 ).getY() ).isEqualTo( 500 );

		// when
		Point2D screenPoint = renderer.worldToScreen( new Point2D( 2, 1 ) );

		// then
		assertThat( screenPoint.getX() ).isCloseTo( 500 + 2 * gz, TOLERANCE );
		assertThat( screenPoint.getY() ).isCloseTo( 500 - 1 * gz, TOLERANCE );
	}

	@Test
	void worldToScreenWithDoubleDoubleDouble() {
		// given
		assertThat( renderer.worldToScreen( 0, 0 ).getX() ).isEqualTo( 500 );
		assertThat( renderer.worldToScreen( 0, 0 ).getY() ).isEqualTo( 500 );

		// when
		Point3D screenPoint = renderer.worldToScreen( 2, 3, 1 );

		// then
		assertThat( screenPoint.getX() ).isCloseTo( 500 + 2 * gz, TOLERANCE );
		assertThat( screenPoint.getY() ).isCloseTo( 500 - 3 * gz, TOLERANCE );
		assertThat( screenPoint.getZ() ).isCloseTo( 1, TOLERANCE );
	}

	@Test
	void worldToScreenWithPoint3D() {
		// given
		assertThat( renderer.worldToScreen( 0, 0 ).getX() ).isEqualTo( 500 );
		assertThat( renderer.worldToScreen( 0, 0 ).getY() ).isEqualTo( 500 );

		// when
		Point3D screenPoint = renderer.worldToScreen( new Point3D( 3, 2, 1 ) );

		// then
		assertThat( screenPoint.getX() ).isCloseTo( 500 + 3 * gz, TOLERANCE );
		assertThat( screenPoint.getY() ).isCloseTo( 500 - 2 * gz, TOLERANCE );
		assertThat( screenPoint.getZ() ).isCloseTo( 1, TOLERANCE );
	}

	@ParameterizedTest
	@MethodSource
	void worldToScreenWithRotate( double rotate, double centerX, double centerY, double worldX, double worldY, double screenX, double screenY ) {
		// given
		assertThat( renderer.worldToScreen( 0, 0 ).getX() ).isEqualTo( 500 );
		assertThat( renderer.worldToScreen( 0, 0 ).getY() ).isEqualTo( 500 );

		// when
		renderer.setViewRotate( rotate );
		renderer.setViewCenter( centerX, centerY );
		Point2D point2d = renderer.worldToScreen( worldX, worldY );

		// NOTE The x-coordinate is different than not rotating. If something
		// weren't happening it would still be 500, but it is out at 537.79...,
		// right where it should be without being rotated, but not where we wanted
		// it to be. The y-coordinate is still at 500, also right where it should
		// without being rotated, but not where we wanted it to be. So it appears
		// the rotation just isn't being applied at all.

		// then
		assertThat( point2d.getX() ).isCloseTo( screenX, TOLERANCE ); // 537.7952755905512 expecting 526.72529566689310
		assertThat( point2d.getY() ).isCloseTo( screenY, TOLERANCE ); // 500.0000000000000 expecting 473.27470433310685
	}

	private static Stream<Arguments> worldToScreenWithRotate() {
		return Stream.of(
			Arguments.arguments( 0, 0, 0, 0, 0, 500, 500 ),
			// No angle should change the center
			//			Arguments.arguments( -180, 0, 1, 0, 1, 500, 500 ),
			//			Arguments.arguments( -135, 1, 2, 1, 2, 500, 500 ),
			//			Arguments.arguments( -90, 2, 3, 2, 3, 500, 500 ),
			//			Arguments.arguments( -45, 3, 4, 3, 4, 500, 500 ),
			Arguments.arguments( 0, 4, 5, 4, 5, 500, 500 ),
			//			Arguments.arguments( 45, 5, 6, 5, 6, 500, 500 ),
			//			Arguments.arguments( 90, 6, 7, 6, 7, 500, 500 ),
			//			Arguments.arguments( 135, 7, 8, 7, 8, 500, 500 ),
			//			Arguments.arguments( 180, 8, 9, 8, 9, 500, 500 ),

			Arguments.arguments( 45, 0, 0, 1, 0, 500 + 1 * gz * Constants.SQRT_ONE_HALF, 500 - 1 * gz * Constants.SQRT_ONE_HALF )
		);
	}

	@Test
	void worldToScreenWithZoom() {
		// given
		renderer.resizeRelocate( 0, 0, 1000, 800 );
		assertThat( renderer.worldToScreen( 0, 0 ).getX() ).isEqualTo( 500 );
		assertThat( renderer.worldToScreen( 0, 0 ).getY() ).isEqualTo( 400 );

		// when
		renderer.setViewZoom( new Point2D( 2, 2 ) );

		// then
		assertThat( renderer.worldToScreen( 0, 0 ).getX() ).isEqualTo( 500 );
		assertThat( renderer.worldToScreen( 0, 0 ).getY() ).isEqualTo( 400 );
	}

	@Test
	void worldToScreenWithCenter() {
		// given
		assertThat( renderer.worldToScreen( 0, 0 ).getX() ).isEqualTo( 500 );
		assertThat( renderer.worldToScreen( 0, 0 ).getY() ).isEqualTo( 500 );

		// when
		renderer.setViewCenter( 2, 2, 0 );

		// then
		assertThat( renderer.worldToScreen( 0, 0 ).getX() ).isCloseTo( 500 - 2 * gz, TOLERANCE );
		assertThat( renderer.worldToScreen( 0, 0 ).getY() ).isEqualTo( 500 + 2 * gz, TOLERANCE );
	}

	@ParameterizedTest
	@MethodSource
	void worldToScreenDoesNotChangeWithDifferentOutputScales( double outputScale, Point2D point, Point2D expected ) {
		// given
		renderer.resizeRelocate( 0, 0, 1000, 1000 );
		renderer.setDesign( new Design2D() );
		renderer.setWorkplane( new Workplane() );
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
