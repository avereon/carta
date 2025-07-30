package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.CartesiaTestTag;
import com.avereon.cartesia.Design2dAssetType;
import com.avereon.cartesia.DesignToolBaseTest;
import com.avereon.cartesia.cursor.Reticle;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.DesignPortal;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DesignToolV3Test extends DesignToolBaseTest {

	private Design model;

	private DesignToolV3 tool;

	private DesignToolV3Renderer renderer;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();

		model = ExampleDesigns.redBlueX();
		Asset asset = new Asset( new Design2dAssetType( getProgram() ), URI.create( "new://test" ) ).setModel( model );

		renderer = Mockito.spy( new DesignToolV3Renderer() );

		Fx.run( () -> tool = new DesignToolV3( module, asset, renderer ) );
		Fx.waitFor( 1, TimeUnit.SECONDS );

		OpenAssetRequest request = new OpenAssetRequest();
		request.setAsset( asset );
		tool.ready( request );

		assertThat( (Design)tool.getAsset().getModel() ).isEqualTo( model );
		assertThat( tool.getDesign() ).isNotNull();

		lenient().doCallRealMethod().when( renderer ).setDpi( anyDouble() );
	}

	@Test
	void constructor() {
		assertThat( tool ).isNotNull();
	}

	@Test
	void defaultViewCenter() {
		// when
		Point3D result = tool.getViewCenter();

		// then
		assertThat( result ).isEqualTo( DesignToolV3.DEFAULT_CENTER );
	}

	@Test
	void setViewCenter() {
		// given
		Point3D center = new Point3D( 1, 2, 3 );

		// when
		tool.setViewCenter( center );

		// then
		assertThat( tool.getViewCenter() ).isEqualTo( center );
	}

	@Test
	void defaultRotate() {
		// when
		double result = tool.getViewRotate();

		// then
		assertThat( result ).isEqualTo( DesignToolV3.DEFAULT_ROTATE );
	}

	@Test
	void setViewRotate() {
		// given
		double rotate = 123.45;

		// when
		tool.setViewRotate( rotate );

		// then
		assertThat( tool.getViewRotate() ).isEqualTo( rotate );
	}

	@Test
	void defaultViewZoom() {
		// when
		double result = tool.getViewZoom();

		// then
		assertThat( result ).isEqualTo( DesignToolV3.DEFAULT_ZOOM.getX() );
	}

	@Test
	void setViewZoom() {
		// given
		double zoom = 123.45;

		// when
		tool.setViewZoom( zoom );

		// then
		assertThat( tool.getViewZoom() ).isEqualTo( zoom );
	}

	@Test
	void setViewWithDesignPortal() {
		// given
		DesignPortal portal = new DesignPortal( new Point3D( 1, 2, 3 ), 123.45, 123.45 );

		// when
		tool.setView( portal );

		// then
		assertThat( tool.getViewCenter() ).isEqualTo( portal.center() );
		assertThat( tool.getViewZoom() ).isEqualTo( portal.zoom() );
		assertThat( tool.getViewRotate() ).isEqualTo( portal.rotate() );
	}

	@Test
	void setViewWithViewpointZoom() {
		// given
		Point3D center = new Point3D( 1, 2, 3 );
		double zoom = 123.46;

		// when
		tool.setView( center, zoom );

		// then
		assertThat( tool.getViewCenter() ).isEqualTo( center );
		assertThat( tool.getViewZoom() ).isEqualTo( zoom );
		assertThat( tool.getViewRotate() ).isEqualTo( DesignToolV3.DEFAULT_ROTATE );
	}

	@Test
	void setViewWithViewpointRotateZoom() {
		// given
		Point3D center = new Point3D( 1, 2, 3 );
		double rotate = 123.45;
		double zoom = 678.90;

		// when
		tool.setView( center, zoom, rotate );

		// then
		assertThat( tool.getViewCenter() ).isEqualTo( center );
		assertThat( tool.getViewRotate() ).isEqualTo( rotate );
		assertThat( tool.getViewZoom() ).isEqualTo( zoom );
	}

	@Test
	void defaultDpi() {
		// when
		double result = tool.getDpi();

		// then
		assertThat( result ).isEqualTo( DesignToolV3.DEFAULT_DPI );
	}

	@Test
	void setDpi() {
		// given
		double dpi = 123.45;

		// when
		tool.setDpi( dpi );

		// then
		assertThat( tool.getDpi() ).isEqualTo( dpi );
	}

	/**
	 * This test ensures that FX geometry is resized when the DPI is changed
	 * in the tool. This test relies on the somewhat complicated implementation of
	 * the renderer, even though the API is exposed here at the tool level.
	 */
	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
	void updateDpi() {
		tool.setDpi( 2 * DesignToolV3.DEFAULT_DPI );

		// Check that it delegates to the renderer
		verify( renderer, times( 1 ) ).setDpi( 2 * DesignToolV3.DEFAULT_DPI );
	}

	@Test
	void defaultCursor() {
		// when
		Cursor result = tool.getCursor();

		// then
		assertThat( result.toString() ).isEqualTo( DesignToolV3.DEFAULT_RETICLE.name() );
		assertThat( result ).isInstanceOf( ReticleCursor.class );
	}

	@Test
	void setCursor() throws Exception {
		// given
		CompletableFuture<Cursor> future = new CompletableFuture<>();
		Fx.run( () -> future.complete( tool.getCursor() ) );
		Cursor cursor = future.get();
		assertThat( cursor.toString() ).isEqualTo( Reticle.DUPLEX.name() );

		// when
		tool.setCursor( cursor );
		Cursor result = tool.getCursor();

		// then
		assertThat( result.toString() ).isEqualTo( cursor.toString() );
		assertThat( result ).isInstanceOf( ReticleCursor.class );
	}

	@Test
	void setWorldViewportWithToolWidthAndHeightAtZero() {
		// given
		assertThat( tool.getWidth() ).isEqualTo( 0 );
		assertThat( tool.getHeight() ).isEqualTo( 0 );
		assertThat( tool.getViewCenter() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( tool.getViewZoom() ).isEqualTo( 1 );

		// when
		tool.setWorldViewport( new BoundingBox( 0, 0, 1, 1 ) );

		// then
		assertThat( tool.getWidth() ).isEqualTo( 0 );
		assertThat( tool.getHeight() ).isEqualTo( 0 );
		assertThat( tool.getViewCenter() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( tool.getViewZoom() ).isEqualTo( 1 );
	}

	@Test
	void setWorldViewport() {
		// given
		tool.resize( 1000, 1000 );
		assertThat( tool.getWidth() ).isEqualTo( 1000 );
		assertThat( tool.getHeight() ).isEqualTo( 1000 );
		assertThat( tool.getViewCenter() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( tool.getViewZoom() ).isEqualTo( 1 );

		// when
		tool.setWorldViewport( new BoundingBox( 400, 500, 100, 100 ) );

		// then
		assertThat( tool.getViewCenter() ).isEqualTo( new Point3D( 450, 550, 0 ) );
		assertThat( tool.getViewZoom() ).isEqualTo( 0.26458333333333334 );
		assertThat( tool.getWidth() ).isEqualTo( 1000 );
		assertThat( tool.getHeight() ).isEqualTo( 1000 );
	}

	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
	void getVisibleLayers() {
		tool.getVisibleLayers();

		// Check that it delegates to the renderer
		verify( renderer, times( 1 ) ).getVisibleLayers();
	}

	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
	void setVisibleLayers() {
		DesignLayer layer = new DesignLayer().setName( "layer-0" );
		tool.setVisibleLayers( Set.of( layer ) );

		// Check that it delegates to the renderer
		verify( renderer, times( 1 ) ).setVisibleLayers( Set.of( layer ) );
	}

	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
	void screenToWorldWithPoint2D() {
		tool.screenToWorld( new Point2D( 1, 2 ) );

		// Check that it delegates to the renderer
		verify( renderer, times( 1 ) ).screenToWorld( new Point2D( 1, 2 ) );
	}

	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
	void screenToWorldWithDoubleDouble() {
		tool.screenToWorld( 3, 4 );

		// Check that it delegates to the renderer
		verify( renderer, times( 1 ) ).screenToWorld( 3, 4 );
	}

	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
	void screenToWorldWithPoint3D() {
		tool.screenToWorld( new Point3D( 1, 2, 3 ) );

		// Check that it delegates to the renderer
		verify( renderer, times( 1 ) ).screenToWorld( new Point3D( 1, 2, 3 ) );
	}

	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
	void screenToWorldWithDoubleDoubleDouble() {
		tool.screenToWorld( 3, 4, 5 );

		// Check that it delegates to the renderer
		verify( renderer, times( 1 ) ).screenToWorld( 3, 4, 5 );
	}

	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
	void screenToWorldWithBounds() {
		tool.screenToWorld( new BoundingBox( 1, 2, 3, 4 ) );

		// Check that it delegates to the renderer
		verify( renderer, times( 1 ) ).screenToWorld( new BoundingBox( 1, 2, 3, 4 ) );
	}

	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
	void worldToScreenWithPoint2D() {
		tool.worldToScreen( new Point2D( 1, 2 ) );

		// Check that it delegates to the renderer
		verify( renderer, times( 1 ) ).worldToScreen( new Point2D( 1, 2 ) );
	}

	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
	void worldToScreenWithDoubleDouble() {
		tool.worldToScreen( 3, 4 );

		// Check that it delegates to the renderer
		verify( renderer, times( 1 ) ).worldToScreen( 3, 4 );
	}

	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
	void worldToScreenWithPoint3D() {
		tool.worldToScreen( new Point3D( 1, 2, 3 ) );

		// Check that it delegates to the renderer
		verify( renderer, times( 1 ) ).worldToScreen( new Point3D( 1, 2, 3 ) );
	}

	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
	void worldToScreenWithDoubleDoubleDouble() {
		tool.worldToScreen( 3, 4, 5 );

		// Check that it delegates to the renderer
		verify( renderer, times( 1 ) ).worldToScreen( 3, 4, 5 );
	}

	@Test
	@Tag( CartesiaTestTag.WHITE_BOX )
	void worldToScreenWithBounds() {
		tool.worldToScreen( new BoundingBox( 4, 3, 2, 1 ) );

		// Check that it delegates to the renderer
		verify( renderer, times( 1 ) ).worldToScreen( new BoundingBox( 4, 3, 2, 1 ) );
	}

}
