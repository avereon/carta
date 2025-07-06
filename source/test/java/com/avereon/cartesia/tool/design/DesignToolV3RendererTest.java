package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.Design2D;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.RenderConstants;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
	private ChangeListener<DesignUnit> unitListener;

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
	void setLayerVisible() {
		// given
		Design design = new Design2D();
		DesignLayer layer0 = new DesignLayer().setName("layer0").setOrder( 0 );
		DesignLayer layer1 = new DesignLayer().setName("layer1").setOrder( 1 );
		DesignLayer layer2 = new DesignLayer().setName("layer2").setOrder( 2 );
		DesignLayer layer3 = new DesignLayer().setName("layer3").setOrder( 3 );
		DesignLayer layer4 = new DesignLayer().setName("layer4").setOrder( 4 );
		design.getLayers().addLayer( layer0 );
		design.getLayers().addLayer( layer1 );
		design.getLayers().addLayer( layer2 );
		design.getLayers().addLayer( layer3 );
		design.getLayers().addLayer( layer4 );
		renderer.setDesign( design );
		assertThat(design.getAllLayers().size()).isEqualTo( 5 );
		assertThat(renderer.layersPane().getChildren().size()).isEqualTo( 0 );
		
		// when
		renderer.setLayerVisible( layer1, true );
		renderer.setLayerVisible( layer3, true );
		// then
		assertThat(renderer.layersPane().getChildren().indexOf((Pane)layer1.getValue(DesignToolV3Renderer.FX_SHAPE))).isEqualTo(1);
		assertThat(renderer.layersPane().getChildren().indexOf((Pane)layer3.getValue(DesignToolV3Renderer.FX_SHAPE))).isEqualTo(0);
		assertThat(renderer.layersPane().getChildren().size()).isEqualTo( 2 );

		// when
		renderer.setLayerVisible( layer2, true );
		assertThat(renderer.layersPane().getChildren().indexOf((Pane)layer1.getValue(DesignToolV3Renderer.FX_SHAPE))).isEqualTo(2);
		assertThat(renderer.layersPane().getChildren().indexOf((Pane)layer2.getValue(DesignToolV3Renderer.FX_SHAPE))).isEqualTo(1);
		assertThat(renderer.layersPane().getChildren().indexOf((Pane)layer3.getValue(DesignToolV3Renderer.FX_SHAPE))).isEqualTo(0);
		assertThat(renderer.layersPane().getChildren().size()).isEqualTo( 3 );

		// when
		renderer.setLayerVisible( layer4, true );
		assertThat(renderer.layersPane().getChildren().indexOf((Pane)layer1.getValue(DesignToolV3Renderer.FX_SHAPE))).isEqualTo(3);
		assertThat(renderer.layersPane().getChildren().indexOf((Pane)layer2.getValue(DesignToolV3Renderer.FX_SHAPE))).isEqualTo(2);
		assertThat(renderer.layersPane().getChildren().indexOf((Pane)layer3.getValue(DesignToolV3Renderer.FX_SHAPE))).isEqualTo(1);
		assertThat(renderer.layersPane().getChildren().indexOf((Pane)layer4.getValue(DesignToolV3Renderer.FX_SHAPE))).isEqualTo(0);
		assertThat(renderer.layersPane().getChildren().size()).isEqualTo( 4 );

		// when
		renderer.setLayerVisible( layer4, false );
		// then
		assertThat(renderer.layersPane().getChildren().indexOf((Pane)layer1.getValue(DesignToolV3Renderer.FX_SHAPE))).isEqualTo(2);
		assertThat(renderer.layersPane().getChildren().indexOf((Pane)layer2.getValue(DesignToolV3Renderer.FX_SHAPE))).isEqualTo(1);
		assertThat(renderer.layersPane().getChildren().indexOf((Pane)layer3.getValue(DesignToolV3Renderer.FX_SHAPE))).isEqualTo(0);
		assertThat(renderer.layersPane().getChildren().size()).isEqualTo( 3 );
	}

}
