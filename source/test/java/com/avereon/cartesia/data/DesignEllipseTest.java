package com.avereon.cartesia.data;

import com.avereon.cartesia.PointAssert;
import com.avereon.curve.math.Constants;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class DesignEllipseTest {

	@Test
	void testModify() {
		DesignEllipse line = new DesignEllipse( new Point3D( 0, 0, 0 ), 1.0 );
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
		DesignEllipse arc = new DesignEllipse( new Point3D( 0, 0, 0 ), 2.0 );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		arc.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( arc.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testRadii() {
		Point3D radii = new Point3D( 7, 5, 0.0 );
		DesignEllipse arc = new DesignEllipse( new Point3D( 0, 0, 0 ), radii );
		assertThat( arc.getRadii() ).isEqualTo( radii );

		arc.setRadii( new Point3D( 13, 11, 0 ) );
		assertThat( arc.getRadii() ).isEqualTo( new Point3D( 13, 11, 0 ) );
	}

	@Test
	void testRadius() {
		DesignEllipse arc = new DesignEllipse( new Point3D( 0, 0, 0 ), 3.0 );
		assertThat( arc.getRadius() ).isEqualTo( 3.0 );

		arc.setRadius( 3.5 );
		assertThat( arc.getRadius() ).isEqualTo( 3.5 );
	}

	@Test
	void testToMapWithCircle() {
		DesignEllipse arc = new DesignEllipse( new Point3D( 1, 2, 3 ), 4.0 );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignEllipse.SHAPE ) ).isEqualTo( DesignEllipse.CIRCLE );
		assertThat( map.get( DesignEllipse.ORIGIN ) ).isEqualTo( new Point3D( 1, 2, 3 ) );
		assertThat( map.get( DesignEllipse.RADII ) ).isEqualTo( new Point3D( 4, 4, 0 ) );
		assertThat( map.get( DesignEllipse.ROTATE ) ).isNull();
	}

	@Test
	void testToMapWithEllipse() {
		DesignEllipse arc = new DesignEllipse( new Point3D( 1, 2, 3 ), 4.0, 5.0 );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignEllipse.SHAPE ) ).isEqualTo( DesignEllipse.ELLIPSE );
		assertThat( map.get( DesignEllipse.ORIGIN ) ).isEqualTo( new Point3D( 1, 2, 3 ) );
		assertThat( map.get( DesignEllipse.RADII ) ).isEqualTo( new Point3D( 4, 5, 0 ) );
		assertThat( map.get( DesignEllipse.ROTATE ) ).isNull();
	}

	@Test
	void testToMapWithRotatedEllipse() {
		DesignEllipse arc = new DesignEllipse( new Point3D( 1, 2, 3 ), 6.0, 7.0, 8.0 );
		Map<String, Object> map = arc.asMap();

		assertThat( map.get( DesignEllipse.SHAPE ) ).isEqualTo( DesignEllipse.ELLIPSE );
		assertThat( map.get( DesignEllipse.ORIGIN ) ).isEqualTo( new Point3D( 1, 2, 3 ) );
		assertThat( map.get( DesignEllipse.RADII ) ).isEqualTo( new Point3D( 6, 7, 0 ) );
		assertThat( map.get( DesignEllipse.ROTATE ) ).isEqualTo( "8.0" );
	}

	@Test
	void testUpdateFromCircle() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignEllipse.SHAPE, DesignEllipse.CIRCLE );
		map.put( DesignEllipse.ORIGIN, "0,0,0" );
		map.put( DesignEllipse.RADII, "4,4,0" );

		DesignEllipse arc = new DesignEllipse();
		arc.updateFrom( map );

		assertThat( arc.getOrigin() ).isEqualTo( Point3D.ZERO );
		assertThat( arc.getRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getXRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getYRadius() ).isEqualTo( 4.0 );
		assertThat( arc.calcRotate() ).isEqualTo( 0.0 );
		assertThat( arc.getRotate() ).isNull();
	}

	@Test
	void testUpdateFromCircleWithDeprecatedRadius() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignEllipse.SHAPE, DesignEllipse.CIRCLE );
		map.put( DesignEllipse.ORIGIN, "0,0,0" );
		map.put( DesignEllipse.RADII, "4,4,0" );

		DesignEllipse arc = new DesignEllipse();
		arc.updateFrom( map );

		assertThat( arc.getOrigin() ).isEqualTo( Point3D.ZERO );
		assertThat( arc.getRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getXRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getYRadius() ).isEqualTo( 4.0 );
		assertThat( arc.calcRotate() ).isEqualTo( 0.0 );
		assertThat( arc.getRotate() ).isNull();
	}

	@Test
	void testUpdateFromEllipse() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignEllipse.SHAPE, DesignEllipse.ELLIPSE );
		map.put( DesignEllipse.ORIGIN, "0,0,0" );
		map.put( DesignEllipse.RADII, "4,5,0" );

		DesignEllipse arc = new DesignEllipse();
		arc.updateFrom( map );

		assertThat( arc.getOrigin() ).isEqualTo( Point3D.ZERO );
		assertThat( arc.getRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getXRadius() ).isEqualTo( 4.0 );
		assertThat( arc.getYRadius() ).isEqualTo( 5.0 );
		assertThat( arc.calcRotate() ).isEqualTo( 0.0 );
		assertThat( arc.getRotate() ).isNull();
	}

	@Test
	void testUpdateFromEllipseWithDeprecatedRadius() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignEllipse.SHAPE, DesignEllipse.ELLIPSE );
		map.put( DesignEllipse.ORIGIN, "0,0,0" );
		map.put( DesignArc.RADII, "4,5,0" );

		DesignEllipse ellipse = new DesignEllipse();
		ellipse.updateFrom( map );

		assertThat( ellipse.getOrigin() ).isEqualTo( Point3D.ZERO );
		assertThat( ellipse.getRadius() ).isEqualTo( 4.0 );
		assertThat( ellipse.getXRadius() ).isEqualTo( 4.0 );
		assertThat( ellipse.getYRadius() ).isEqualTo( 5.0 );
		assertThat( ellipse.calcRotate() ).isEqualTo( 0.0 );
		assertThat( ellipse.getRotate() ).isNull();
	}

	@Test
	void testUpdateFromRotatedEllipse() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignEllipse.SHAPE, DesignEllipse.ELLIPSE );
		map.put( DesignEllipse.ORIGIN, "0,0,0" );
		map.put( DesignEllipse.RADII, "6,7,0" );
		map.put( DesignEllipse.ROTATE, "8.0" );

		DesignEllipse arc = new DesignEllipse();
		arc.updateFrom( map );

		assertThat( arc.getOrigin() ).isEqualTo( Point3D.ZERO );
		assertThat( arc.getRadius() ).isEqualTo( 6.0 );
		assertThat( arc.getXRadius() ).isEqualTo( 6.0 );
		assertThat( arc.getYRadius() ).isEqualTo( 7.0 );
		assertThat( arc.calcRotate() ).isEqualTo( 8.0 );
		assertThat( arc.getRotate() ).isEqualTo( "8.0" );
	}

	@Test
	void testUpdateFromRotatedEllipseWithDeprecatedRadius() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignEllipse.SHAPE, DesignEllipse.ELLIPSE );
		map.put( DesignEllipse.ORIGIN, "0,0,0" );
		map.put( DesignEllipse.RADII, "6,7,0" );
		map.put( DesignEllipse.ROTATE, "8.0" );

		DesignEllipse arc = new DesignEllipse();
		arc.updateFrom( map );

		assertThat( arc.getOrigin() ).isEqualTo( Point3D.ZERO );
		assertThat( arc.getRadius() ).isEqualTo( 6.0 );
		assertThat( arc.getXRadius() ).isEqualTo( 6.0 );
		assertThat( arc.getYRadius() ).isEqualTo( 7.0 );
		assertThat( arc.calcRotate() ).isEqualTo( 8.0 );
		assertThat( arc.getRotate() ).isEqualTo( "8.0" );
	}

	@Test
	void testDistanceTo() {
		// Test circles
		DesignEllipse arc = new DesignEllipse( new Point3D( 5, 0, 0 ), 1.0 );
		assertThat( arc.distanceTo( new Point3D( 0, 0, 0 ) ) ).isEqualTo( 4.0 );
		assertThat( arc.distanceTo( new Point3D( 5, 0, 0 ) ) ).isEqualTo( 1.0 );

		// TODO Test DesignEllipse.distanceTo()
	}

	@Test
	void testPathLength() {
		// Circle
		assertThat( new DesignEllipse( new Point3D( 5, 0, 0 ), 1.0 ).pathLength() ).isCloseTo( Constants.FULL_CIRCLE, TOLERANCE );

		// Degenerate ellipses
		assertThat( new DesignEllipse( new Point3D( 5, 0, 0 ), 2.0, 0.0 ).pathLength() ).isCloseTo( 8.0, Offset.offset( Constants.RESOLUTION_LENGTH ) );
		assertThat( new DesignEllipse( new Point3D( 5, 0, 0 ), 0.0, 1.0 ).pathLength() ).isCloseTo( 4.0, Offset.offset( Constants.RESOLUTION_LENGTH ) );

		// Normal ellipse
		assertThat( new DesignEllipse( new Point3D( 5, 0, 0 ), 10.0, 5.0 ).pathLength() ).isCloseTo( 48.44224110273837, Offset.offset( Constants.RESOLUTION_LENGTH ) );

		// Rotated ellipse
		assertThat( new DesignEllipse( new Point3D( 5, 0, 0 ), 10.0, 5.0, 45.0 ).pathLength() ).isCloseTo( 48.44224110273837, Offset.offset( Constants.RESOLUTION_LENGTH ) );
	}

	@Test
	void testBounds() {
		// Circle
		assertThat( new DesignEllipse( new Point3D( 5, 0, 0 ), 1.0 ).getBounds() ).isEqualTo( new BoundingBox( 4, -1, 2, 2 ) );

		// Degenerate ellipses
		assertThat( new DesignEllipse( new Point3D( 5, 0, 0 ), 2.0, 0.0 ).getBounds() ).isEqualTo( new BoundingBox( 3, 0, 4, 0 ) );
		assertThat( new DesignEllipse( new Point3D( 5, 0, 0 ), 0.0, 1.0 ).getBounds() ).isEqualTo( new BoundingBox( 5, -1, 0, 2 ) );

		// Normal ellipse
		assertThat( new DesignEllipse( new Point3D( 5, 0, 0 ), 10.0, 5.0 ).getBounds() ).isEqualTo( new BoundingBox( -5, -5, 20, 10 ) );

		// Rotated ellipse
		//assertThat( new DesignEllipse( new Point3D( 5, 0, 0 ), 10.0, 5.0, 45.0 ).getBounds() ).isEqualTo( new BoundingBox( -5, -5, 20, 20 ) );
	}

	@Test
	void testLocalTransform() {
		DesignEllipse ellipse = new DesignEllipse( new Point3D( 0, 0, 0 ), 2.0, 1.0 );
		PointAssert.assertThat( ellipse.getLocalTransform().apply( new Point3D( 1, 1, 0 ) ) ).isCloseTo( new Point3D( 1, 2, 0 ) );

		ellipse = new DesignEllipse( new Point3D( 0, 0, 0 ), 2.0, 4.0 );
		PointAssert.assertThat( ellipse.getLocalTransform().apply( new Point3D( 1, 1, 0 ) ) ).isCloseTo( new Point3D( 1, 0.5, 0 ) );
	}

	@Test
	void testLocalTransformWithRotationScaleAndTranslate() {
		double root2 = Math.sqrt( 2 );
		DesignEllipse ellipse = new DesignEllipse( new Point3D( -3.0, 3.0, 0 ), 2.0, 1.0, 45.0 );
		PointAssert.assertThat( ellipse.getLocalTransform().apply( ellipse.getOrigin() ) ).isCloseTo( new Point3D( 0, 0, 0 ) );
		PointAssert.assertThat( ellipse.getLocalTransform().apply( new Point3D( -1, -1, 0 ) ) ).isCloseTo( new Point3D( -1 * root2, -6 * root2, 0 ) );

		ellipse = new DesignEllipse( new Point3D( -3.0, -3.0, 0 ), 2.0, 4.0, 270.0 );
		PointAssert.assertThat( ellipse.getLocalTransform().apply( ellipse.getOrigin() ) ).isCloseTo( new Point3D( 0, 0, 0 ) );
		PointAssert.assertThat( ellipse.getLocalTransform().apply( new Point3D( -1, -1, 0 ) ) ).isCloseTo( new Point3D( -2, 1, 0 ) );
	}

}
