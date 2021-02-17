package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DesignCurveTest {

	@Test
	void testModify() {
		DesignCurve curve = new DesignCurve( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertTrue( curve.isModified() );
		curve.setModified( false );
		assertFalse( curve.isModified() );

		curve.setOrigin( new Point3D( 0, 0, 0 ) );
		curve.setPoint( new Point3D( 0, 0, 0 ) );
		assertFalse( curve.isModified() );

		curve.setOrigin( new Point3D( 1, 1, 0 ) );
		assertTrue( curve.isModified() );
		curve.setModified( false );
		assertFalse( curve.isModified() );

		curve.setPoint( new Point3D( 2, 2, 0 ) );
		assertTrue( curve.isModified() );
		curve.setModified( false );
		assertFalse( curve.isModified() );
	}

	@Test
	void testOrigin() {
		DesignCurve curve = new DesignCurve( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( curve.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );

		curve.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( curve.getOrigin(), is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void testPoint() {
		DesignCurve curve = new DesignCurve( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( curve.getPoint(), is( new Point3D( 0, 0, 0 ) ) );

		curve.setPoint( new Point3D( 1, 2, 3 ) );
		assertThat( curve.getPoint(), is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void testToMap() {
		DesignCurve curve = new DesignCurve( new Point3D( 0, 0, 0 ), new Point3D( 0.5, 0.5, 0 ), new Point3D( 0.5, -0.5, 0 ), new Point3D( 1, 0, 0 ) );
		Map<String, Object> map = curve.asMap();

		assertThat( map.get( DesignCurve.SHAPE ), is( DesignCurve.CURVE ) );
		assertThat( map.get( DesignCurve.ORIGIN ), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( map.get( DesignCurve.ORIGIN_CONTROL ), is( new Point3D( 0.5, 0.5, 0 ) ) );
		assertThat( map.get( DesignCurve.POINT_CONTROL ), is( new Point3D( 0.5, -0.5, 0 ) ) );
		assertThat( map.get( DesignCurve.POINT ), is( new Point3D( 1, 0, 0 ) ) );
	}

	@Test
	void testUpdateFrom() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignCurve.SHAPE, DesignCurve.CURVE );
		map.put( DesignCurve.ORIGIN, "0,0,0" );
		map.put( DesignCurve.ORIGIN_CONTROL, "0.5,0.5,0" );
		map.put( DesignCurve.POINT_CONTROL, "0.5,-0.5,0" );
		map.put( DesignCurve.POINT, "1,0,0" );

		DesignCurve curve = new DesignCurve();
		curve.updateFrom( map );

		assertThat( curve.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( curve.getOriginControl(), is( new Point3D( 0.5, 0.5, 0 ) ) );
		assertThat( curve.getPointControl(), is( new Point3D( 0.5, -0.5, 0 ) ) );
		assertThat( curve.getPoint(), is( new Point3D( 1, 0, 0 ) ) );
	}

}
