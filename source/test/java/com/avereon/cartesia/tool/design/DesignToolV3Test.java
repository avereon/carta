package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignToolBaseTest;
import com.avereon.cartesia.tool.DesignPortal;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignToolV3Test extends DesignToolBaseTest {

	private DesignToolV3 tool;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		tool = new DesignToolV3( module, asset );
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
		assertThat( tool.getViewCenter() ).isEqualTo( portal.viewpoint() );
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
}
