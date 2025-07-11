package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.Design2dAssetType;
import com.avereon.cartesia.DesignToolBaseTest;
import com.avereon.cartesia.cursor.Reticle;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.tool.DesignPortal;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignToolV3Test extends DesignToolBaseTest {

	private DesignToolV3 tool;

	private Design model;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();

		model = ExampleDesigns.redBlueX();
		Asset asset = new Asset( new Design2dAssetType( getProgram() ), URI.create( "new://test" ) ).setModel( model );

		Fx.run( () -> tool = new DesignToolV3( module, asset ) );
		Fx.waitFor( 1, TimeUnit.SECONDS );

		OpenAssetRequest request = new OpenAssetRequest();
		request.setAsset( asset );
		tool.ready( request );

		assertThat( (Design)tool.getAsset().getModel() ).isEqualTo( model );
		assertThat( tool.getDesign() ).isNotNull();
	}

	@Test
	void constructor() {
		assertThat( tool ).isNotNull();
	}

	@Test
	void defaultViewpoint() {
		// when
		Point3D result = tool.getViewCenter();

		// then
		assertThat( result ).isEqualTo( DesignToolV3.DEFAULT_CENTER );
	}

	@Test
	void setViewpoint() {
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
	void defaultZoom() {
		// when
		double result = tool.getViewZoom();

		// then
		assertThat( result ).isEqualTo( DesignToolV3.DEFAULT_ZOOM.getX() );
	}

	@Test
	void setZoom() {
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

		// TODO Finish DesignToolV3Test.setWorldViewport()

		//		// when
		//		tool.setWorldViewport( new BoundingBox( 0, 0, 1, 1 ) );
		//
		//		// then
		//		assertThat( tool.getWidth() ).isEqualTo( 0 );
		//		assertThat( tool.getHeight() ).isEqualTo( 0 );
		//		assertThat( tool.getViewCenter() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		//		assertThat( tool.getViewZoom() ).isEqualTo( 1 );
	}

	/**
	 * This test ensures that FX geometry is resized when the design unit is changed
	 * in the tool.
	 */
	@Test
	void updateDesignUnit() {
		// given
		// Verify the FX geometry in the renderer
		DesignToolV3Renderer renderer = (DesignToolV3Renderer)tool.getScreenDesignRenderer();
		Pane layers = renderer.layersPane();
		Pane construction = (Pane)layers.getChildren().getFirst();
		Line redLine = (Line)construction.getChildren().get( 0 );
		Line greenLine = (Line)construction.getChildren().get( 1 );
		// Verify the original bounds (these are DPI-dependent)
		assertThat( renderer.getVisualBounds( redLine ) ).isEqualTo( new BoundingBox( -207.87400817871094, -207.87400817871094, 415.7480163574219, 415.7480163574219 ) );
		assertThat( renderer.getVisualBounds( greenLine ) ).isEqualTo( new BoundingBox( -207.87400817871094, -207.87400817871094, 415.7480163574219, 415.7480163574219 ) );
		assertThat( renderer.getVisualBounds( construction ) ).isEqualTo( new BoundingBox( -207.87400817871094, -207.87400817871094, 415.7480163574219, 415.7480163574219 ) );

		// when
		Fx.run( ()-> model.setDesignUnit( "mm" ) );
		Fx.waitFor( 1, TimeUnit.SECONDS );

		// then
		// The FX geometry should have changed in the renderer
		assertThat( renderer.getVisualBounds( redLine ) ).isEqualTo( new BoundingBox( -20.78740119934082, -20.78740119934082, 41.57480239868164, 41.57480239868164 ) );
		assertThat( renderer.getVisualBounds( greenLine ) ).isEqualTo( new BoundingBox( -20.78740119934082, -20.78740119934082, 41.57480239868164, 41.57480239868164 ) );
		assertThat( renderer.getVisualBounds( construction ) ).isEqualTo( new BoundingBox( -20.78740119934082, -20.78740119934082, 41.57480239868164, 41.57480239868164 ) );
	}

}
