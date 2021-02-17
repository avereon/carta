package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DesignArcTest {

	@Test
	void testModify() {
		DesignArc line = new DesignArc( new Point3D( 0, 0, 0 ), 1 );
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
		DesignArc arc = new DesignArc( new Point3D( 0, 0, 0 ), 2 );
		assertThat( arc.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );

		arc.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void testRadius() {
		DesignArc arc = new DesignArc( new Point3D( 0, 0, 0 ), 3 );
		assertThat( arc.getRadius(), is( 3.0 ) );

		arc.setRadius( 3.5 );
		assertThat( arc.getRadius(), is( 3.5 ) );
	}

	@Test
	void testToMap() {
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 3 ), 4, 5, 6, 7, 8, DesignArc.Type.CHORD );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignArc.SHAPE ), is( DesignArc.ARC ) );
		assertThat( map.get( DesignArc.ORIGIN ), is( new Point3D( 1, 2, 3 ) ) );
		assertThat( map.get( DesignArc.X_RADIUS ), is( 4.0 ) );
		assertThat( map.get( DesignArc.Y_RADIUS ), is( 5.0 ) );
		assertThat( map.get( DesignArc.START ), is( 6.0 ) );
		assertThat( map.get( DesignArc.EXTENT ), is( 7.0 ) );
		assertThat( map.get( DesignArc.ROTATE ), is( 8.0 ) );
		assertThat( map.get( DesignArc.TYPE ), is( DesignArc.Type.CHORD ) );
	}

	@Test
	void testUpdateFrom() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignArc.SHAPE, DesignArc.ARC );
		map.put( DesignArc.ORIGIN, "0,0,0" );
		map.put( DesignArc.X_RADIUS, 4.0 );
		map.put( DesignArc.Y_RADIUS, 5.0 );
		map.put( DesignArc.START, 6.0 );
		map.put( DesignArc.EXTENT, 7.0 );
		map.put( DesignArc.ROTATE, 8.0 );
		map.put( DesignArc.TYPE, DesignArc.Type.CHORD.name().toLowerCase() );

		DesignArc arc = new DesignArc();
		arc.updateFrom( map );

		assertThat( arc.getType(), is( DesignArc.Type.CHORD ) );
	}

	@Test
	void testDistanceTo() {
		// Test circles
		DesignArc arc = new DesignArc( new Point3D( 5, 0, 0 ), 1 );
		assertThat( arc.distanceTo( new Point3D( 0, 0, 0 ) ), is( 4.0 ) );
		assertThat( arc.distanceTo( new Point3D( 5, 0, 0 ) ), is( 1.0 ) );

		// TODO Test circle arcs

		// TODO Test ellipses

		// TODO Test ellipse arcs
	}

}
