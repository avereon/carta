package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadConstants;
import com.avereon.cartesia.test.Point3DAssert;
import javafx.geometry.Point3D;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignCubicTest {

	@Test
	void testModify() {
		DesignCubic curve = new DesignCubic( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( curve.isModified() ).isTrue();
		curve.setModified( false );
		assertThat( curve.isModified() ).isFalse();

		curve.setOrigin( new Point3D( 0, 0, 0 ) );
		curve.setPoint( new Point3D( 0, 0, 0 ) );
		assertThat( curve.isModified() ).isFalse();

		curve.setOrigin( new Point3D( 1, 1, 0 ) );
		assertThat( curve.isModified() ).isTrue();
		curve.setModified( false );
		assertThat( curve.isModified() ).isFalse();

		curve.setPoint( new Point3D( 2, 2, 0 ) );
		assertThat( curve.isModified() ).isTrue();
		curve.setModified( false );
		assertThat( curve.isModified() ).isFalse();
	}

	@Test
	void testOrigin() {
		DesignCubic curve = new DesignCubic( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( curve.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		curve.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( curve.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void getOriginControl() {
		DesignCubic curve = new DesignCubic( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( curve.getOriginControl() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		curve.setOriginControl( new Point3D( 1, 2, 3 ) );
		assertThat( curve.getOriginControl() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void getPointControl() {
		DesignCubic curve = new DesignCubic( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( curve.getPointControl() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		curve.setPointControl( new Point3D( 1, 2, 3 ) );
		assertThat( curve.getPointControl() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testPoint() {
		DesignCubic curve = new DesignCubic( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( curve.getPoint() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		curve.setPoint( new Point3D( 1, 2, 3 ) );
		assertThat( curve.getPoint() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testPathLength() {
		DesignCubic curve = new DesignCubic( new Point3D( 0, 0, 0 ), new Point3D( 0, 1, 0 ), new Point3D( 1, 1, 0 ), new Point3D( 1, 0, 0 ) );
		assertThat( curve.pathLength() ).isEqualTo( 2.0, Offset.offset( CadConstants.RESOLUTION_LENGTH ) );

		curve = new DesignCubic( new Point3D( 0, 0, 0 ), new Point3D( 0, 1, 0 ), new Point3D( 1, 1, 0 ), new Point3D( 2, 1, 0 ) );
		assertThat( curve.pathLength() ).isEqualTo( 2.550645, Offset.offset( CadConstants.RESOLUTION_LENGTH ) );
	}

	@Test
	void testToMap() {
		DesignCubic curve = new DesignCubic( new Point3D( 0, 0, 0 ), new Point3D( 0.5, 0.5, 0 ), new Point3D( 0.5, -0.5, 0 ), new Point3D( 1, 0, 0 ) );
		Map<String, Object> map = curve.asMap();

		assertThat( map.get( DesignCubic.SHAPE ) ).isEqualTo( DesignCubic.CURVE );
		assertThat( map.get( DesignCubic.ORIGIN ) ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( map.get( DesignCubic.ORIGIN_CONTROL ) ).isEqualTo( new Point3D( 0.5, 0.5, 0 ) );
		assertThat( map.get( DesignCubic.POINT_CONTROL ) ).isEqualTo( new Point3D( 0.5, -0.5, 0 ) );
		assertThat( map.get( DesignCubic.POINT ) ).isEqualTo( new Point3D( 1, 0, 0 ) );
	}

	@Test
	void testUpdateFrom() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignCubic.SHAPE, DesignCubic.CURVE );
		map.put( DesignCubic.ORIGIN, "0,0,0" );
		map.put( DesignCubic.ORIGIN_CONTROL, "0.5,0.5,0" );
		map.put( DesignCubic.POINT_CONTROL, "0.5,-0.5,0" );
		map.put( DesignCubic.POINT, "1,0,0" );

		DesignCubic curve = new DesignCubic();
		curve.updateFrom( map );

		assertThat( curve.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( curve.getOriginControl() ).isEqualTo( new Point3D( 0.5, 0.5, 0 ) );
		assertThat( curve.getPointControl() ).isEqualTo( new Point3D( 0.5, -0.5, 0 ) );
		assertThat( curve.getPoint() ).isEqualTo( new Point3D( 1, 0, 0 ) );
	}

	@Test
	void getReferencePoints() {
		// given
		DesignCubic curve = new DesignCubic( new Point3D( 0, 0, 0 ), new Point3D( 0.5, 0.5, 0 ), new Point3D( 0.5, -0.5, 0 ), new Point3D( 1, 0, 0 ) );

		// when
		List<Point3D> points = curve.getReferencePoints();

		// then
		Point3DAssert.assertThat( points.getFirst() ).isCloseTo( new Point3D( 0, 0, 0 ) );
		Point3DAssert.assertThat( points.get( 1 ) ).isCloseTo( new Point3D( 0.5, 0.5, 0 ) );
		Point3DAssert.assertThat( points.get( 2 ) ).isCloseTo( new Point3D( 0.5, -0.5, 0 ) );
		Point3DAssert.assertThat( points.get( 3 ) ).isCloseTo( new Point3D( 1, 0, 0 ) );
	}

}
