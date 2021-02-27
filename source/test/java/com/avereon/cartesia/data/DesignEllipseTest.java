package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.avereon.cartesia.match.Near.near;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class DesignEllipseTest {

	@Test
	void testModify() {
		DesignEllipse line = new DesignEllipse( new Point3D( 0, 0, 0 ), 1.0 );
		assertTrue( line.isModified() );
		line.setModified( false );
		assertFalse( line.isModified() );

		line.setOrigin( new Point3D( 0, 0, 0 ) );
		line.setRadius( 1.0 );
		assertFalse( line.isModified() );

		line.setOrigin( new Point3D( 1, 1, 0 ) );
		assertTrue( line.isModified() );
		line.setModified( false );
		assertFalse( line.isModified() );

		line.setRadius( 2.0 );
		assertTrue( line.isModified() );
		line.setModified( false );
		assertFalse( line.isModified() );
	}

	@Test
	void testOrigin() {
		DesignEllipse arc = new DesignEllipse( new Point3D( 0, 0, 0 ), 2.0 );
		assertThat( arc.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );

		arc.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void testRadius() {
		DesignEllipse arc = new DesignEllipse( new Point3D( 0, 0, 0 ), 3.0 );
		assertThat( arc.getRadius(), is( 3.0 ) );

		arc.setRadius( 3.5 );
		assertThat( arc.getRadius(), is( 3.5 ) );
	}

	@Test
	void testToMapWithCircle() {
		DesignEllipse arc = new DesignEllipse( new Point3D( 1, 2, 3 ), 4.0 );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignEllipse.SHAPE ), is( DesignEllipse.CIRCLE ) );
		assertThat( map.get( DesignEllipse.ORIGIN ), is( new Point3D( 1, 2, 3 ) ) );
		assertThat( map.get( DesignEllipse.RADIUS ), is( 4.0 ) );
		assertThat( map.get( DesignEllipse.X_RADIUS ), is( 4.0 ) );
		assertThat( map.get( DesignEllipse.Y_RADIUS ), is( 4.0 ) );
		assertNull( map.get( DesignEllipse.ROTATE ) );
	}

	@Test
	void testToMapWithEllipse() {
		DesignEllipse arc = new DesignEllipse( new Point3D( 1, 2, 3 ), 4.0, 5.0 );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignEllipse.SHAPE ), is( DesignEllipse.ELLIPSE ) );
		assertThat( map.get( DesignEllipse.ORIGIN ), is( new Point3D( 1, 2, 3 ) ) );
		assertThat( map.get( DesignEllipse.X_RADIUS ), is( 4.0 ) );
		assertThat( map.get( DesignEllipse.Y_RADIUS ), is( 5.0 ) );
		assertNull( map.get( DesignEllipse.ROTATE ) );
	}

	@Test
	void testToMapWithRotatedEllipse() {
		DesignEllipse arc = new DesignEllipse( new Point3D( 1, 2, 3 ), 6.0, 7.0, 8.0 );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignEllipse.SHAPE ), is( DesignEllipse.ELLIPSE ) );
		assertThat( map.get( DesignEllipse.ORIGIN ), is( new Point3D( 1, 2, 3 ) ) );
		assertThat( map.get( DesignEllipse.X_RADIUS ), is( 6.0 ) );
		assertThat( map.get( DesignEllipse.Y_RADIUS ), is( 7.0 ) );
		assertThat( map.get( DesignEllipse.ROTATE ), is( 8.0 ) );
	}

	@Test
	void testUpdateFromCircle() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignEllipse.SHAPE, DesignEllipse.CIRCLE );
		map.put( DesignEllipse.ORIGIN, "0,0,0" );
		map.put( DesignEllipse.RADIUS, 4.0 );

		DesignEllipse arc = new DesignEllipse();
		arc.updateFrom( map );

		assertThat( arc.getOrigin(), is( Point3D.ZERO ) );
		assertThat( arc.getRadius(), is( 4.0 ) );
		assertThat( arc.getXRadius(), is( 4.0 ) );
		assertThat( arc.getYRadius(), is( 4.0 ) );
		assertThat( arc.calcRotate(), is( 0.0 ) );
		assertNull( arc.getRotate() );
	}

	@Test
	void testUpdateFromEllipse() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignEllipse.SHAPE, DesignEllipse.ELLIPSE );
		map.put( DesignEllipse.ORIGIN, "0,0,0" );
		map.put( DesignEllipse.X_RADIUS, 4.0 );
		map.put( DesignEllipse.Y_RADIUS, 5.0 );

		DesignEllipse arc = new DesignEllipse();
		arc.updateFrom( map );

		assertThat( arc.getOrigin(), is( Point3D.ZERO ) );
		assertNull( arc.getRadius() );
		assertThat( arc.getXRadius(), is( 4.0 ) );
		assertThat( arc.getYRadius(), is( 5.0 ) );
		assertThat( arc.calcRotate(), is( 0.0 ) );
		assertNull( arc.getRotate() );
	}

	@Test
	void testUpdateFromRotatedEllipse() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignEllipse.SHAPE, DesignEllipse.ELLIPSE );
		map.put( DesignEllipse.ORIGIN, "0,0,0" );
		map.put( DesignEllipse.X_RADIUS, 6.0 );
		map.put( DesignEllipse.Y_RADIUS, 7.0 );
		map.put( DesignEllipse.ROTATE, 8.0 );

		DesignEllipse arc = new DesignEllipse();
		arc.updateFrom( map );

		assertThat( arc.getOrigin(), is( Point3D.ZERO ) );
		assertNull( arc.getRadius() );
		assertThat( arc.getXRadius(), is( 6.0 ) );
		assertThat( arc.getYRadius(), is( 7.0 ) );
		assertThat( arc.calcRotate(), is( 8.0 ) );
		assertThat( arc.getRotate(), is( 8.0 ) );
	}

	@Test
	void testDistanceTo() {
		// Test circles
		DesignEllipse arc = new DesignEllipse( new Point3D( 5, 0, 0 ), 1.0 );
		assertThat( arc.distanceTo( new Point3D( 0, 0, 0 ) ), is( 4.0 ) );
		assertThat( arc.distanceTo( new Point3D( 5, 0, 0 ) ), is( 1.0 ) );

		// TODO Test ellipses
	}

	@Test
	void testLocalTransform() {
		DesignEllipse ellipse = new DesignEllipse( new Point3D( 0, 0, 0 ), 2.0, 1.0 );
		assertThat( ellipse.getLocalTransform().apply( new Point3D( 1, 1, 0 ) ), near( new Point3D( 1, 2, 0 ) ) );

		ellipse = new DesignEllipse( new Point3D( 0, 0, 0 ), 2.0, 4.0 );
		assertThat( ellipse.getLocalTransform().apply( new Point3D( 1, 1, 0 ) ), near( new Point3D( 2, 1, 0 ) ) );
	}

	@Test
	void testLocalTransformWithRotationScaleAndTranslate() {
		double root2 = Math.sqrt( 2 );
		DesignEllipse ellipse = new DesignEllipse( new Point3D( -3.0, 3.0, 0 ), 2.0, 1.0, 45.0 );
		assertThat( ellipse.getLocalTransform().apply( ellipse.getOrigin() ), near( new Point3D( 0, 0, 0 ) ) );
		assertThat( ellipse.getLocalTransform().apply( new Point3D( -1, -1, 0 ) ), near( new Point3D( -1 * root2, -6 * root2, 0 ) ) );

		ellipse = new DesignEllipse( new Point3D( -3.0, -3.0, 0 ), 2.0, 4.0, 270.0 );
		assertThat( ellipse.getLocalTransform().apply( ellipse.getOrigin() ), near( new Point3D( 0, 0, 0 ) ) );
		assertThat( ellipse.getLocalTransform().apply( new Point3D( -1, -1, 0 ) ), near( new Point3D( -4, 2, 0 ) ) );
	}

}
