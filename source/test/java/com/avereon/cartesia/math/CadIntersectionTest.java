package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

public class CadIntersectionTest {

	@Test
	void testIntersectLineLine() {
		DesignLine a = new DesignLine().setPoint( new Point3D( 1, 1, 0 ) ).setOrigin( new Point3D( 0, 0, 0 ) );
		DesignLine b = new DesignLine().setPoint( new Point3D( 1, 0, 0 ) ).setOrigin( new Point3D( 0, 1, 0 ) );
		assertThat( CadIntersection.intersectLineLine( a, b ), is( List.of( new Point3D( 0.5, 0.5, 0 ) ) ) );
	}

	@Test
	void testIntersectLineLineParallel() {
		DesignLine a = new DesignLine().setPoint( new Point3D( 1, 0, 0 ) ).setOrigin( new Point3D( 0, 0, 0 ) );
		DesignLine b = new DesignLine().setPoint( new Point3D( 1, 1, 0 ) ).setOrigin( new Point3D( 0, 1, 0 ) );
		assertThat( CadIntersection.intersectLineLine( a, b ), is( List.of() ) );
	}

	@Test
	void testIntersectLineLineSkew() {
		DesignLine a = new DesignLine().setPoint( new Point3D( 1, 1, 1 ) ).setOrigin( new Point3D( 0, 0, 0 ) );
		DesignLine b = new DesignLine().setPoint( new Point3D( 1, 0, 0 ) ).setOrigin( new Point3D( 0, 1, 0 ) );
		assertThat( CadIntersection.intersectLineLine( a, b ), is( List.of() ) );
	}

	@Test
	void testIntersectLineLineShort() {
		DesignLine a = new DesignLine().setPoint( new Point3D( 0.5, 1, 0 ) ).setOrigin( new Point3D( 0, 1, 0 ) );
		DesignLine b = new DesignLine().setPoint( new Point3D( 1, 0.5, 0 ) ).setOrigin( new Point3D( 1, 0, 0 ) );
		assertThat( CadIntersection.intersectLineLine( a, b ), is( List.of( new Point3D( 1, 1, 0 ) ) ) );
	}

	@Test
	void testIntersectLineCircle() {
		DesignLine a = new DesignLine( new Point3D( 2, 0, 0 ), new Point3D( -2, 0, 0 ) );
		DesignEllipse b = new DesignEllipse( new Point3D( 0, 0, 0 ), 1.0 );
		assertThat( CadIntersection.getIntersections( a, b ), containsInAnyOrder( new Point3D( -1, 0, 0 ), new Point3D( 1, 0, 0 ) ) );
	}
}
