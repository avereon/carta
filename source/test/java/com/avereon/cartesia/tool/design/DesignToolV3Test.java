package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignToolBaseTest;
import com.avereon.cartesia.cursor.Reticle;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.tool.DesignPortal;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignToolV3Test extends DesignToolBaseTest {

	private DesignToolV3 tool;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		Fx.run( () -> tool = new DesignToolV3( module, asset ) );
		Fx.waitFor( 2, TimeUnit.SECONDS );
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
		assertThat( result ).isEqualTo( DesignToolV3.DEFAULT_VIEWPOINT );
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
		assertThat( result ).isEqualTo( DesignToolV3.DEFAULT_ZOOM );
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

}
