package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class DesignCircleTest {

	@Test
	void testModify() {
		DesignCircle line = new DesignCircle( new Point3D( 0, 0, 0 ), 1 );
		assertTrue( line.isModified() );
		line.setModified( false );
		assertFalse( line.isModified() );

		line.setOrigin( new Point3D( 0, 0, 0 ) );
		line.setRadius( 1 );
		assertFalse( line.isModified() );

		line.setOrigin( new Point3D( 1, 1, 0 ) );
		assertTrue( line.isModified() );
		line.setModified( false );
		assertFalse( line.isModified() );

		line.setRadius( 2 );
		assertTrue( line.isModified() );
		line.setModified( false );
		assertFalse( line.isModified() );
	}

	@Test
	void testOrigin() {
		DesignCircle arc = new DesignCircle( new Point3D( 0, 0, 0 ), 2 );
		assertThat( arc.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );

		arc.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void testRadius() {
		DesignCircle arc = new DesignCircle( new Point3D( 0, 0, 0 ), 3 );
		assertThat( arc.getRadius(), is( 3.0 ) );

		arc.setRadius( 3.5 );
		assertThat( arc.getRadius(), is( 3.5 ) );
	}

	@Test
	void testToMapWithCircle() {
		DesignCircle arc = new DesignCircle( new Point3D( 1, 2, 3 ), 4 );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignCircle.SHAPE ), is( DesignCircle.CIRCLE ) );
		assertThat( map.get( DesignCircle.ORIGIN ), is( new Point3D( 1, 2, 3 ) ) );
		assertThat( map.get( DesignCircle.RADIUS ), is( 4.0 ) );
		assertThat( map.get( DesignCircle.X_RADIUS ), is( 4.0 ) );
		assertThat( map.get( DesignCircle.Y_RADIUS ), is( 4.0 ) );
		assertThat( map.get( DesignCircle.START ), is( 0.0 ) );
		assertThat( map.get( DesignCircle.EXTENT ), is( 360.0 ) );
		assertThat( map.get( DesignCircle.ROTATE ), is( 0.0 ) );
		assertThat( map.get( DesignCircle.TYPE ), is( DesignCircle.Type.FULL ) );
	}

	@Test
	void testToMapWithCircleArc() {
		DesignCircle arc = new DesignCircle( new Point3D( 1, 2, 3 ), 4, 0, 90, DesignCircle.Type.OPEN  );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignCircle.SHAPE ), is( DesignCircle.ARC ) );
		assertThat( map.get( DesignCircle.ORIGIN ), is( new Point3D( 1, 2, 3 ) ) );
		assertThat( map.get( DesignCircle.RADIUS ), is( 4.0 ) );
		assertThat( map.get( DesignCircle.X_RADIUS ), is( 4.0 ) );
		assertThat( map.get( DesignCircle.Y_RADIUS ), is( 4.0 ) );
		assertThat( map.get( DesignCircle.START ), is( 0.0 ) );
		assertThat( map.get( DesignCircle.EXTENT ), is( 90.0 ) );
		assertThat( map.get( DesignCircle.ROTATE ), is( 0.0 ) );
		assertThat( map.get( DesignCircle.TYPE ), is( DesignCircle.Type.OPEN ) );
	}

	@Test
	void testToMapWithEllipse() {
		DesignCircle arc = new DesignCircle( new Point3D( 1, 2, 3 ), 4, 5, 0, 360 );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignCircle.SHAPE ), is( DesignCircle.ELLIPSE ) );
		assertThat( map.get( DesignCircle.ORIGIN ), is( new Point3D( 1, 2, 3 ) ) );
		assertThat( map.get( DesignCircle.X_RADIUS ), is( 4.0 ) );
		assertThat( map.get( DesignCircle.Y_RADIUS ), is( 5.0 ) );
		assertThat( map.get( DesignCircle.START ), is( 0.0 ) );
		assertThat( map.get( DesignCircle.EXTENT ), is( 360.0 ) );
		assertThat( map.get( DesignCircle.ROTATE ), is( 0.0 ) );
		assertThat( map.get( DesignCircle.TYPE ), is( DesignCircle.Type.FULL ) );
	}

	@Test
	void testToMapWithEllipseArc() {
		DesignCircle arc = new DesignCircle( new Point3D( 1, 2, 3 ), 4, 5, 6, 7, 8, DesignCircle.Type.CHORD );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignCircle.SHAPE ), is( DesignCircle.ARC ) );
		assertThat( map.get( DesignCircle.ORIGIN ), is( new Point3D( 1, 2, 3 ) ) );
		assertThat( map.get( DesignCircle.X_RADIUS ), is( 4.0 ) );
		assertThat( map.get( DesignCircle.Y_RADIUS ), is( 5.0 ) );
		assertThat( map.get( DesignCircle.START ), is( 6.0 ) );
		assertThat( map.get( DesignCircle.EXTENT ), is( 7.0 ) );
		assertThat( map.get( DesignCircle.ROTATE ), is( 8.0 ) );
		assertThat( map.get( DesignCircle.TYPE ), is( DesignCircle.Type.CHORD ) );
	}

	@Test
	void testUpdateFromCircle() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignCircle.SHAPE, DesignCircle.CIRCLE );
		map.put( DesignCircle.ORIGIN, "0,0,0" );
		map.put( DesignCircle.RADIUS, 4.0 );

		DesignCircle arc = new DesignCircle();
		arc.updateFrom( map );

		assertThat( arc.getOrigin(), is( Point3D.ZERO ) );
		assertThat( arc.getRadius(), is( 4.0 ) );
		assertThat( arc.getXRadius(), is( 4.0 ) );
		assertThat( arc.getYRadius(), is( 4.0 ) );
		assertNull( arc.getStart() );
		assertNull( arc.getExtent() );
		assertNull( arc.getRotate() );
		assertNull( arc.getType() );
	}

	@Test
	void testUpdateFromCircleArc() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignCircle.SHAPE, DesignCircle.ARC );
		map.put( DesignCircle.ORIGIN, "0,0,0" );
		map.put( DesignCircle.RADIUS, 4.0 );
		map.put( DesignCircle.START, 180.0 );
		map.put( DesignCircle.EXTENT, 17.0 );

		DesignCircle arc = new DesignCircle();
		arc.updateFrom( map );

		assertThat( arc.getOrigin(), is( Point3D.ZERO ) );
		assertThat( arc.getRadius(), is( 4.0 ) );
		assertThat( arc.getXRadius(), is( 4.0 ) );
		assertThat( arc.getYRadius(), is( 4.0 ) );
		assertThat( arc.getStart(), is( 180.0 ) );
		assertThat( arc.getExtent(), is( 17.0 ) );
		assertNull( arc.getRotate() );
		assertNull( arc.getType() );
	}

	@Test
	void testUpdateFromEllipse() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignCircle.SHAPE, DesignCircle.ELLIPSE );
		map.put( DesignCircle.ORIGIN, "0,0,0" );
		map.put( DesignCircle.X_RADIUS, 4.0 );
		map.put( DesignCircle.Y_RADIUS, 5.0 );

		DesignCircle arc = new DesignCircle();
		arc.updateFrom( map );

		assertThat( arc.getOrigin(), is( Point3D.ZERO ) );
		assertNull( arc.getRadius() );
		assertThat( arc.getXRadius(), is( 4.0 ) );
		assertThat( arc.getYRadius(), is( 5.0 ) );
		assertNull( arc.getStart() );
		assertNull( arc.getExtent() );
		assertNull( arc.getRotate() );
		assertNull( arc.getType() );
	}

	@Test
	void testUpdateFromEllipseArc() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignCircle.SHAPE, DesignCircle.ARC );
		map.put( DesignCircle.ORIGIN, "0,0,0" );
		map.put( DesignCircle.X_RADIUS, 4.0 );
		map.put( DesignCircle.Y_RADIUS, 5.0 );
		map.put( DesignCircle.START, 6.0 );
		map.put( DesignCircle.EXTENT, 7.0 );
		map.put( DesignCircle.ROTATE, 8.0 );
		map.put( DesignCircle.TYPE, DesignCircle.Type.CHORD.name().toLowerCase() );

		DesignCircle arc = new DesignCircle();
		arc.updateFrom( map );

		assertThat( arc.getOrigin(), is( Point3D.ZERO ) );
		assertNull( arc.getRadius() );
		assertThat( arc.getXRadius(), is( 4.0 ) );
		assertThat( arc.getYRadius(), is( 5.0 ) );
		assertThat( arc.getStart(), is( 6.0 ) );
		assertThat( arc.getExtent(), is( 7.0 ) );
		assertThat( arc.getRotate(), is( 8.0 ) );
		assertThat( arc.getType(), is( DesignCircle.Type.CHORD ) );
	}

	@Test
	void testDistanceTo() {
		// Test circles
		DesignCircle arc = new DesignCircle( new Point3D( 5, 0, 0 ), 1 );
		assertThat( arc.distanceTo( new Point3D( 0, 0, 0 ) ), is( 4.0 ) );
		assertThat( arc.distanceTo( new Point3D( 5, 0, 0 ) ), is( 1.0 ) );

		// TODO Test circle arcs

		// TODO Test ellipses

		// TODO Test ellipse arcs
	}

}
