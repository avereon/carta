package com.avereon.cartesia.data;

import com.avereon.cartesia.PointAssert;
import com.avereon.cartesia.math.CadTransform;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class DesignArcTest {

	@Test
	void testModify() {
		DesignArc line = new DesignArc( new Point3D( 0, 0, 0 ), 1.0, 0.0, 45.0, DesignArc.Type.OPEN );
		assertThat( line.isModified() ).isTrue();
		line.setModified( false );
		assertThat( line.isModified() ).isFalse();

		line.setOrigin( new Point3D( 0, 0, 0 ) );
		line.setRadius( 1.0 );
		assertThat( line.isModified() ).isFalse();

		line.setOrigin( new Point3D( 1, 1, 0 ) );
		assertThat( line.isModified() ).isTrue();
		line.setModified( false );
		assertThat( line.isModified() ).isFalse();

		line.setRadius( 2.0 );
		assertThat( line.isModified() ).isTrue();
		line.setModified( false );
		assertThat( line.isModified() ).isFalse();
	}

	@Test
	void testOrigin() {
		DesignArc arc = new DesignArc( new Point3D( 0, 0, 0 ), 2.0, 0.0, 45.0, DesignArc.Type.OPEN );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		arc.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testRadius() {
		DesignArc arc = new DesignArc( new Point3D( 0, 0, 0 ), 3.0, 0.0, 45.0, DesignArc.Type.OPEN );
		assertThat( arc.getRadius() ).isEqualTo( 3.0 );

		arc.setRadius( 3.5 );
		assertThat( arc.getRadius() ).isEqualTo( 3.5 );
	}

	@Test
	void testToMapWithCircleArc() {
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 3 ), 4.0, 0.0, 90.0, DesignArc.Type.PIE );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignArc.SHAPE ) ).isEqualTo( DesignArc.ARC );
		assertThat( map.get( DesignArc.ORIGIN ) ).isEqualTo( new Point3D( 1, 2, 3 ) );
		assertThat( map.get( DesignArc.RADII ) ).isEqualTo( new Point3D( 4, 4, 0 ) );
		assertThat( map.get( DesignArc.START ) ).isEqualTo( 0.0 );
		assertThat( map.get( DesignArc.EXTENT ) ).isEqualTo( 90.0 );
		assertThat( map.get( DesignArc.ROTATE ) ).isNull();
		assertThat( map.get( DesignArc.TYPE ) ).isEqualTo( DesignArc.Type.PIE );
	}

	@Test
	void testToMapWithEllipseArc() {
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 3 ), 4.0, 5.0, 0.0, 360.0, DesignArc.Type.OPEN );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignArc.SHAPE ) ).isEqualTo( DesignArc.ARC );
		assertThat( map.get( DesignArc.ORIGIN ) ).isEqualTo( new Point3D( 1, 2, 3 ) );
		assertThat( map.get( DesignArc.RADII ) ).isEqualTo( new Point3D( 4, 5, 0 ) );
		assertThat( map.get( DesignArc.START ) ).isEqualTo( 0.0 );
		assertThat( map.get( DesignArc.EXTENT ) ).isEqualTo( 360.0 );
		assertThat( map.get( DesignArc.ROTATE ) ).isNull();
		assertThat( map.get( DesignArc.TYPE ) ).isEqualTo( DesignArc.Type.OPEN );
	}

	@Test
	void testToMapWithRotatedEllipseArc() {
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 3 ), 4.0, 5.0, 6.0, 7.0, 8.0, DesignArc.Type.CHORD );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignArc.SHAPE ) ).isEqualTo( DesignArc.ARC );
		assertThat( map.get( DesignArc.ORIGIN ) ).isEqualTo( new Point3D( 1, 2, 3 ) );
		assertThat( map.get( DesignArc.RADII ) ).isEqualTo( new Point3D( 4, 5, 0 ) );
		assertThat( map.get( DesignArc.ROTATE ) ).isEqualTo( 6.0 );
		assertThat( map.get( DesignArc.START ) ).isEqualTo( 7.0 );
		assertThat( map.get( DesignArc.EXTENT ) ).isEqualTo( 8.0 );
		assertThat( map.get( DesignArc.TYPE ) ).isEqualTo( DesignArc.Type.CHORD );
	}

	@Test
	void testUpdateFromCircleArc() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignArc.SHAPE, DesignArc.ARC );
		map.put( DesignArc.ORIGIN, "0,0,0" );
		map.put( DesignArc.RADII, "4,4,0" );
		map.put( DesignArc.START, 180.0 );
		map.put( DesignArc.EXTENT, 17.0 );

		DesignArc arc = new DesignArc();
		arc.updateFrom( map );

		assertThat( arc.getOrigin() ).isEqualTo( Point3D.ZERO );
		assertThat( arc.getRadii() ).isEqualTo( new Point3D( 4, 4, 0 ) );
		assertThat( arc.getRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getXRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getYRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getStart() ).isEqualTo( 180.0 );
		assertThat( arc.getExtent() ).isEqualTo( 17.0 );
		assertThat( arc.calcRotate() ).isEqualTo( 0.0 );
		assertThat( arc.getRotate() ).isNull();
		assertThat( arc.getType() ).isNull();
	}

	@Test
	void testUpdateFromEllipseArc() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignArc.SHAPE, DesignArc.ELLIPSE );
		map.put( DesignArc.ORIGIN, "0,0,0" );
		map.put( DesignArc.RADII, "4,5,0" );
		map.put( DesignArc.START, 6.0 );
		map.put( DesignArc.EXTENT, 7.0 );

		DesignArc arc = new DesignArc();
		arc.updateFrom( map );

		assertThat( arc.getOrigin() ).isEqualTo( Point3D.ZERO );
		assertThat( arc.getRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getXRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getYRadius() ).isEqualTo( 5.0 );
		assertThat( arc.calcRotate() ).isEqualTo( 0.0 );
		assertThat( arc.getRotate() ).isNull();
		assertThat( arc.getStart() ).isEqualTo( 6.0 );
		assertThat( arc.getExtent() ).isEqualTo( 7.0 );
		assertThat( arc.getType() ).isNull();
	}

	@Test
	void testUpdateFromEllipseArcWithDeprecatedRadius() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignArc.SHAPE, DesignArc.ELLIPSE );
		map.put( DesignArc.ORIGIN, "0,0,0" );
		map.put( DesignArc.X_RADIUS, 4.0 );
		map.put( DesignArc.Y_RADIUS, 5.0 );
		map.put( DesignArc.START, 6.0 );
		map.put( DesignArc.EXTENT, 7.0 );

		DesignArc arc = new DesignArc();
		arc.updateFrom( map );

		assertThat( arc.getOrigin() ).isEqualTo( Point3D.ZERO );
		assertThat( arc.getRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getXRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getYRadius() ).isEqualTo( 5.0 );
		assertThat( arc.calcRotate() ).isEqualTo( 0.0 );
		assertThat( arc.getRotate() ).isNull();
		assertThat( arc.getStart() ).isEqualTo( 6.0 );
		assertThat( arc.getExtent() ).isEqualTo( 7.0 );
		assertThat( arc.getType() ).isNull();
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

		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
		assertThat( arc.getRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getXRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getYRadius() ).isEqualTo( 5.0 );
		assertThat( arc.calcRotate() ).isEqualTo( 6.0 );
		assertThat( arc.getRotate() ).isEqualTo( 6.0 );
		assertThat( arc.getStart() ).isEqualTo( 7.0 );
		assertThat( arc.getExtent() ).isEqualTo( 8.0 );
		assertThat( arc.getType() ).isEqualTo( DesignArc.Type.CHORD );
	}

	@Test
	void testDistanceTo() {
		// Test circles
		DesignArc arc = new DesignArc( new Point3D( 5, 0, 0 ), 1.0, 0.0, 45.0, DesignArc.Type.OPEN );
		assertThat( arc.distanceTo( new Point3D( 0, 0, 0 ) ) ).isEqualTo( 4.0 );
		assertThat( arc.distanceTo( new Point3D( 5, 0, 0 ) ) ).isEqualTo( 1.0 );

		// TODO Test circle arcs

		// TODO Test ellipse arcs
	}

	@Test
	void testApplyWithCircleArcAndMirrorTransformCW() {
		DesignArc arc = new DesignArc( new Point3D( 0, 2, 0 ), 5.0, 225.0, -90.0, DesignArc.Type.OPEN );
		CadTransform transform = CadTransform.mirror( new Point3D( 2, 0, 0 ), new Point3D( 2, 2, 0 ) );

		arc.apply( transform );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( 4, 2, 0 ) );
		assertThat( arc.getStart() ).isCloseTo( -45.0, TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( 90.0, TOLERANCE );
		assertThat( arc.calcRotate() ).isCloseTo( 0.0, TOLERANCE );

		// Mirror it back
		arc.apply( transform );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( 0, 2, 0 ) );
		// This ends up as 255 normalized to -135
		assertThat( arc.getStart() ).isCloseTo( -135.0, TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( -90.0, TOLERANCE );
		assertThat( arc.calcRotate() ).isCloseTo( 0.0, TOLERANCE );
	}

	@Test
	void testApplyWithCircleArcAndMirrorTransformCCW() {
		DesignArc arc = new DesignArc( new Point3D( 0, 2, 0 ), 5.0, 135.0, 90.0, DesignArc.Type.OPEN );
		CadTransform transform = CadTransform.mirror( new Point3D( 2, 0, 0 ), new Point3D( 2, 2, 0 ) );

		arc.apply( transform );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( 4, 2, 0 ) );
		assertThat( arc.getStart() ).isCloseTo( 45.0, TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( -90.0, TOLERANCE );
		assertThat( arc.calcRotate() ).isCloseTo( 0.0, TOLERANCE );

		// Mirror it back
		arc.apply( transform );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( 0, 2, 0 ) );
		assertThat( arc.getStart() ).isCloseTo( 135, TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( 90.0, TOLERANCE );
		assertThat( arc.calcRotate() ).isCloseTo( 0.0, TOLERANCE );
	}

	@Test
	void testApplyWithCircleArcAndAngledMirrorTransformCW() {
		DesignArc arc = new DesignArc( new Point3D( 0, 2, 0 ), 5.0, -135.0, -90.0, DesignArc.Type.OPEN );
		CadTransform transform = CadTransform.mirror( new Point3D( 0, 0, 0 ), new Point3D( 2, 2, 0 ) );

		arc.apply( transform );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( 2, 0, 0 ) );
		// The sum of the start and the arc rotate should be -135
		assertThat( arc.getStart() ).isCloseTo( -45.0, TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( 90.0, TOLERANCE );
		assertThat( arc.calcRotate() ).isCloseTo( -90.0, TOLERANCE );

		// Mirror it back
		arc.apply( transform );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( 0, 2, 0 ) );
		assertThat( arc.getStart() ).isCloseTo( -135.0, TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( -90.0, TOLERANCE );
		assertThat( arc.calcRotate() ).isCloseTo( 0.0, TOLERANCE );
	}

	@Test
	void testApplyWithEllipseArcAndMirrorTransform() {
		DesignArc arc = new DesignArc( new Point3D( 0, 2, 0 ), 3.0, 5.0, 135.0, 90.0, DesignArc.Type.OPEN );
		CadTransform transform = CadTransform.mirror( new Point3D( 2, 0, 0 ), new Point3D( 2, 2, 0 ) );

		arc.apply( transform );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( 4, 2, 0 ) );
		assertThat( arc.getStart() ).isCloseTo( 45.0, TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( -90.0, TOLERANCE );
		assertThat( arc.calcRotate() ).isCloseTo( 0.0, TOLERANCE );

		// Mirror it back
		arc.apply( transform );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( 0, 2, 0 ) );
		assertThat( arc.getStart() ).isCloseTo( 135.0, TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( 90.0, TOLERANCE );
		assertThat( arc.calcRotate() ).isCloseTo( 0.0, TOLERANCE );
	}

	@Test
	void testApplyWithEllipseArcAndAngledMirrorTransform() {
		DesignArc arc = new DesignArc( new Point3D( 0, 2, 0 ), 3.0, 5.0, 135.0, 90.0, DesignArc.Type.OPEN );
		CadTransform transform = CadTransform.mirror( new Point3D( 0, 0, 0 ), new Point3D( 2, 2, 0 ) );

		arc.apply( transform );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( 2, 0, 0 ) );
		// The sum of the start and the arc rotate should be -135
		assertThat( arc.getStart() ).isCloseTo( 45.0, TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( -90.0, TOLERANCE );
		assertThat( arc.calcRotate() ).isCloseTo( -90.0, TOLERANCE );

		// Mirror it back
		arc.apply( transform );
		PointAssert.assertThat( arc.getOrigin() ).isCloseTo( new Point3D( 0, 2, 0 ) );
		assertThat( arc.getStart() ).isCloseTo( 135.0, TOLERANCE );
		assertThat( arc.getExtent() ).isCloseTo( 90.0, TOLERANCE );
		assertThat( arc.calcRotate() ).isCloseTo( 0.0, TOLERANCE );
	}

	@Test
	void testMoveEndpointCW() {
		final double alpha = Math.toDegrees( Math.atan2( 3, 4 ) );

		DesignArc arc = new DesignArc( new Point3D( 1, 2, 0 ), 5.0, 180 - alpha, -180 + alpha, DesignArc.Type.OPEN );
		arc.moveEndpoint( new Point3D( 6, 2, 0 ), new Point3D( 5, 5, 0 ) );

		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 1, 2, 0 ) );
		assertThat( arc.calcRotate() ).isEqualTo( 0.0 );
		assertThat( arc.getStart() ).isEqualTo( 180 - alpha );
		assertThat( arc.getExtent() ).isEqualTo( -180 + 2 * alpha );
	}

	@Test
	void testMoveEndpointCCW() {
		final double alpha = Math.toDegrees( Math.atan2( 3, 4 ) );
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 0 ), 5.0, alpha, 180 - alpha, DesignArc.Type.OPEN );
		arc.moveEndpoint( new Point3D( -4, 2, 0 ), new Point3D( -3, 5, 0 ) );

		PointAssert.assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 1, 2, 0 ) );
		assertThat( arc.calcRotate() ).isEqualTo( 0.0 );
		assertThat( arc.getStart() ).isEqualTo( alpha );
		assertThat( arc.getExtent() ).isEqualTo( 180 - 2 * alpha );
	}

	@Test
	void testMoveEndpointBacksideCW() {
		final double alpha = Math.toDegrees( Math.atan2( 3, 4 ) );
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 0 ), 5.0, -90 - alpha, -180 + alpha, DesignArc.Type.OPEN );
		arc.moveEndpoint( new Point3D( 1, -3, 0 ), new Point3D( -2, 6, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 1, 2, 0 ) );
		assertThat( arc.calcRotate() ).isEqualTo( 0.0 );
		assertThat( arc.getStart() ).isEqualTo( -90 - alpha );
		assertThat( arc.getExtent() ).isEqualTo( -180 + 2 * alpha );
	}

	@Test
	void testMoveEndpointBacksideCCW() {
		final double alpha = Math.toDegrees( Math.atan2( 3, 4 ) );
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 0 ), 5.0, 90 + alpha, 180 - alpha, DesignArc.Type.OPEN );
		arc.moveEndpoint( new Point3D( 1, -3, 0 ), new Point3D( -2, -2, 0 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 1, 2, 0 ) );
		assertThat( arc.calcRotate() ).isEqualTo( 0.0 );
		assertThat( arc.getStart() ).isEqualTo( 90 + alpha );
		assertThat( arc.getExtent() ).isEqualTo( 180 - 2 * alpha );
	}

}
