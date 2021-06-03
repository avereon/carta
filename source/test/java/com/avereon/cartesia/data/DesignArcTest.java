package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadTransform;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.avereon.cartesia.match.Near.near;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class DesignArcTest {

	@Test
	void testModify() {
		DesignArc line = new DesignArc( new Point3D( 0, 0, 0 ), 1.0, 0.0, 45.0, DesignArc.Type.OPEN );
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
		DesignArc arc = new DesignArc( new Point3D( 0, 0, 0 ), 2.0, 0.0, 45.0, DesignArc.Type.OPEN );
		assertThat( arc.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );

		arc.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( arc.getOrigin(), is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void testRadius() {
		DesignArc arc = new DesignArc( new Point3D( 0, 0, 0 ), 3.0, 0.0, 45.0, DesignArc.Type.OPEN );
		assertThat( arc.getRadius(), is( 3.0 ) );

		arc.setRadius( 3.5 );
		assertThat( arc.getRadius(), is( 3.5 ) );
	}

	@Test
	void testToMapWithCircleArc() {
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 3 ), 4.0, 0.0, 90.0, DesignArc.Type.PIE );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignArc.SHAPE ), is( DesignArc.ARC ) );
		assertThat( map.get( DesignArc.ORIGIN ), is( new Point3D( 1, 2, 3 ) ) );
		assertThat( map.get( DesignArc.RADIUS ), is( 4.0 ) );
		assertNull( map.get( DesignArc.X_RADIUS ) );
		assertNull( map.get( DesignArc.Y_RADIUS ) );
		assertThat( map.get( DesignArc.START ), is( 0.0 ) );
		assertThat( map.get( DesignArc.EXTENT ), is( 90.0 ) );
		assertNull( map.get( DesignArc.ROTATE ) );
		assertThat( map.get( DesignArc.TYPE ), is( DesignArc.Type.PIE ) );
	}

	@Test
	void testToMapWithEllipseArc() {
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 3 ), 4.0, 5.0, 0.0, 360.0, DesignArc.Type.OPEN );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignArc.SHAPE ), is( DesignArc.ARC ) );
		assertThat( map.get( DesignArc.ORIGIN ), is( new Point3D( 1, 2, 3 ) ) );
		assertThat( map.get( DesignArc.X_RADIUS ), is( 4.0 ) );
		assertThat( map.get( DesignArc.Y_RADIUS ), is( 5.0 ) );
		assertThat( map.get( DesignArc.START ), is( 0.0 ) );
		assertThat( map.get( DesignArc.EXTENT ), is( 360.0 ) );
		assertNull( map.get( DesignArc.ROTATE ) );
		assertThat( map.get( DesignArc.TYPE ), is( DesignArc.Type.OPEN ) );
	}

	@Test
	void testToMapWithRotatedEllipseArc() {
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 3 ), 4.0, 5.0, 6.0, 7.0, 8.0, DesignArc.Type.CHORD );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignArc.SHAPE ), is( DesignArc.ARC ) );
		assertThat( map.get( DesignArc.ORIGIN ), is( new Point3D( 1, 2, 3 ) ) );
		assertThat( map.get( DesignArc.X_RADIUS ), is( 4.0 ) );
		assertThat( map.get( DesignArc.Y_RADIUS ), is( 5.0 ) );
		assertThat( map.get( DesignArc.ROTATE ), is( 6.0 ) );
		assertThat( map.get( DesignArc.START ), is( 7.0 ) );
		assertThat( map.get( DesignArc.EXTENT ), is( 8.0 ) );
		assertThat( map.get( DesignArc.TYPE ), is( DesignArc.Type.CHORD ) );
	}

	@Test
	void testUpdateFromCircleArc() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignArc.SHAPE, DesignArc.ARC );
		map.put( DesignArc.ORIGIN, "0,0,0" );
		map.put( DesignArc.RADIUS, 4.0 );
		map.put( DesignArc.START, 180.0 );
		map.put( DesignArc.EXTENT, 17.0 );

		DesignArc arc = new DesignArc();
		arc.updateFrom( map );

		assertThat( arc.getOrigin(), is( Point3D.ZERO ) );
		assertThat( arc.getRadius(), is( 4.0 ) );
		assertThat( arc.getXRadius(), is( 4.0 ) );
		assertThat( arc.getYRadius(), is( 4.0 ) );
		assertThat( arc.getStart(), is( 180.0 ) );
		assertThat( arc.getExtent(), is( 17.0 ) );
		assertThat( arc.calcRotate(), is( 0.0 ) );
		assertNull( arc.getRotate() );
		assertNull( arc.getType() );
	}

	@Test
	void testUpdateFromEllipseArc() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignArc.SHAPE, DesignArc.ELLIPSE );
		map.put( DesignArc.ORIGIN, "0,0,0" );
		map.put( DesignArc.X_RADIUS, 4.0 );
		map.put( DesignArc.Y_RADIUS, 5.0 );
		map.put( DesignArc.START, 6.0 );
		map.put( DesignArc.EXTENT, 7.0 );

		DesignArc arc = new DesignArc();
		arc.updateFrom( map );

		assertThat( arc.getOrigin(), is( Point3D.ZERO ) );
		assertThat( arc.getRadius(), is( 4.0 ) );
		assertThat( arc.getXRadius(), is( 4.0 ) );
		assertThat( arc.getYRadius(), is( 5.0 ) );
		assertThat( arc.calcRotate(), is( 0.0 ) );
		assertNull( arc.getRotate() );
		assertThat( arc.getStart(), is( 6.0 ) );
		assertThat( arc.getExtent(), is( 7.0 ) );
		assertNull( arc.getType() );
	}

	@Test
	void testUpdateFromRotatedEllipseArc() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignArc.SHAPE, DesignArc.ARC );
		map.put( DesignArc.ORIGIN, "1,2,3" );
		map.put( DesignArc.X_RADIUS, 4.0 );
		map.put( DesignArc.Y_RADIUS, 5.0 );
		map.put( DesignArc.ROTATE, 6.0 );
		map.put( DesignArc.START, 7.0 );
		map.put( DesignArc.EXTENT, 8.0 );
		map.put( DesignArc.TYPE, DesignArc.Type.CHORD.name().toLowerCase() );

		DesignArc arc = new DesignArc();
		arc.updateFrom( map );

		assertThat( arc.getOrigin(), is( new Point3D( 1, 2, 3 ) ) );
		assertThat( arc.getRadius(), is( 4.0 ) );
		assertThat( arc.getXRadius(), is( 4.0 ) );
		assertThat( arc.getYRadius(), is( 5.0 ) );
		assertThat( arc.calcRotate(), is( 6.0 ) );
		assertThat( arc.getRotate(), is( 6.0 ) );
		assertThat( arc.getStart(), is( 7.0 ) );
		assertThat( arc.getExtent(), is( 8.0 ) );
		assertThat( arc.getType(), is( DesignArc.Type.CHORD ) );
	}

	@Test
	void testDistanceTo() {
		// Test circles
		DesignArc arc = new DesignArc( new Point3D( 5, 0, 0 ), 1.0, 0.0, 45.0, DesignArc.Type.OPEN );
		assertThat( arc.distanceTo( new Point3D( 0, 0, 0 ) ), is( 4.0 ) );
		assertThat( arc.distanceTo( new Point3D( 5, 0, 0 ) ), is( 1.0 ) );

		// TODO Test circle arcs

		// TODO Test ellipse arcs
	}

	@Test
	void testApplyWithCircleArcAndMirrorTransformCW() {
		DesignArc arc = new DesignArc( new Point3D( 0, 2, 0 ), 5.0, 225.0, -90.0, DesignArc.Type.OPEN );
		CadTransform transform = CadTransform.mirror( new Point3D( 2, 0, 0 ), new Point3D( 2, 2, 0 ) );

		arc.apply( transform );
		assertThat( arc.getOrigin(), near( new Point3D( 4, 2, 0 ) ) );
		assertThat( arc.getStart(), near( -45.0 ) );
		assertThat( arc.getExtent(), near( 90.0 ) );
		assertThat( arc.getRotate(), near( 0.0 ));

		// Mirror it back
		arc.apply( transform );
		assertThat( arc.getOrigin(), near( new Point3D( 0, 2, 0 ) ) );
		assertThat( arc.getStart(), near( 225.0 ) );
		assertThat( arc.getExtent(), near( -90.0 ) );
		assertThat( arc.getRotate(), near( 0.0 ));
	}

	@Test
	void testApplyWithCircleArcAndMirrorTransformCCW() {
		DesignArc arc = new DesignArc( new Point3D( 0, 2, 0 ), 5.0, 135.0, 90.0, DesignArc.Type.OPEN );
		CadTransform transform = CadTransform.mirror( new Point3D( 2, 0, 0 ), new Point3D( 2, 2, 0 ) );

		arc.apply( transform );
		assertThat( arc.getOrigin(), near( new Point3D( 4, 2, 0 ) ) );
		assertThat( arc.getStart(), near( 135.0 ) );
		assertThat( arc.getExtent(), near( 90.0 ) );
		assertThat( arc.getRotate(), near( 0.0 ));

		// Mirror it back
		arc.apply( transform );
		assertThat( arc.getOrigin(), near( new Point3D( 0, 2, 0 ) ) );
		assertThat( arc.getStart(), near( 135 ) );
		assertThat( arc.getExtent(), near( 90.0 ) );
		assertThat( arc.getRotate(), near( 0.0 ));
	}

	@Test
	void testApplyWithCircleArcAndAngledMirrorTransformCW() {
		DesignArc arc = new DesignArc( new Point3D( 0, 2, 0 ), 5.0, -135.0, -90.0, DesignArc.Type.OPEN );
		CadTransform transform = CadTransform.mirror( new Point3D( 0, 0, 0 ), new Point3D( 2, 2, 0 ) );

		arc.apply( transform );
		assertThat( arc.getOrigin(), near( new Point3D( 2, 0, 0 ) ) );
		assertThat( arc.getStart(), near( -135.0 ) );
		assertThat( arc.getExtent(), near( -90.0 ) );
		assertThat( arc.getRotate(), near( -90.0 ));

		// Mirror it back
		arc.apply( transform );
		assertThat( arc.getOrigin(), near( new Point3D( 0, 2, 0 ) ) );
		assertThat( arc.getExtent(), near( -90.0 ) );
		assertThat( arc.getStart(), near( -135.0 ) );
		assertThat( arc.getRotate(), near( 0.0 ));
	}

//	@Test
//	void testApplyWithEllipseArcAndMirrorTransform() {
//		DesignArc arc = new DesignArc( new Point3D( 0, 2, 0 ), 3.0, 5.0, 135.0, 90.0, DesignArc.Type.OPEN );
//		CadTransform transform = CadTransform.mirror( new Point3D( 2, 0, 0 ), new Point3D( 2, 2, 0 ) );
//
//		arc.apply( transform );
//		assertThat( arc.getOrigin(), near( new Point3D( 4, 2, 0 ) ) );
//		assertThat( arc.getExtent(), near( 90.0 ) );
//		assertThat( arc.getStart(), near( 135.0 ) );
//		assertThat( arc.getRotate(), near( 0.0 ));
//
//		// Mirror it back
//		arc.apply( transform );
//		assertThat( arc.getOrigin(), near( new Point3D( 0, 2, 0 ) ) );
//		assertThat( arc.getExtent(), near( 90.0 ) );
//		assertThat( arc.getStart(), near( 135.0 ) );
//		assertThat( arc.getRotate(), near( 0.0 ));
//	}
//
//	@Test
//	void testApplyWithEllipseArcAndAngledMirrorTransform() {
//		DesignArc arc = new DesignArc( new Point3D( 0, 2, 0 ), 3.0, 5.0, 135.0, 90.0, DesignArc.Type.OPEN );
//		CadTransform transform = CadTransform.mirror( new Point3D( 0, 0, 0 ), new Point3D( 2, 2, 0 ) );
//
//		arc.apply( transform );
//		assertThat( arc.getOrigin(), near( new Point3D( 2, 0, 0 ) ) );
//		assertThat( arc.getExtent(), near( 90.0 ) );
//		assertThat( arc.getStart(), near( 135.0 ) );
//		assertThat( arc.getRotate(), near( -90.0 ));
//
//		// Mirror it back
//		arc.apply( transform );
//		assertThat( arc.getOrigin(), near( new Point3D( 0, 2, 0 ) ) );
//		assertThat( arc.getExtent(), near( 90.0 ) );
//		assertThat( arc.getStart(), near( 135.0 ) );
//		assertThat( arc.getRotate(), near( 0.0 ));
//	}

}
