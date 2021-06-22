package com.avereon.cartesia.math;

import com.avereon.cartesia.BaseCartesiaTest;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.nio.DoubleBuffer;

import static com.avereon.cartesia.match.Near.near;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CadTransformTest extends BaseCartesiaTest {

	@Test
	void testConstructorWithArrays() {
		new CadTransform( new double[ 4 ][ 4 ] );
	}

	@Test
	void testConstructorWithNumbers() {
		new CadTransform( 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 );
	}

	@Test
	void testGetMatrix() {
		CadTransform transform = new CadTransform( 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 );
		DoubleBuffer matrix = transform.getMatrix();
		matrix.rewind();
		assertThat( matrix.get(), is( 0.0 ) );
		assertThat( matrix.get(), is( 4.0 ) );
		assertThat( matrix.get(), is( 8.0 ) );
		assertThat( matrix.get(), is( 12.0 ) );
		assertThat( matrix.get(), is( 1.0 ) );
		assertThat( matrix.get(), is( 5.0 ) );
		assertThat( matrix.get(), is( 9.0 ) );
		assertThat( matrix.get(), is( 13.0 ) );
		assertThat( matrix.get(), is( 2.0 ) );
		assertThat( matrix.get(), is( 6.0 ) );
		assertThat( matrix.get(), is( 10.0 ) );
		assertThat( matrix.get(), is( 14.0 ) );
		assertThat( matrix.get(), is( 3.0 ) );
		assertThat( matrix.get(), is( 7.0 ) );
		assertThat( matrix.get(), is( 11.0 ) );
		assertThat( matrix.get(), is( 15.0 ) );
	}

	@Test
	void testTimes() {
		Point3D vector = CadTransform.scale( 2, 2, 2 ).apply( new Point3D( 1, 2, 3 ) );
		assertThat( vector, is( new Point3D( 2, 4, 6 ) ) );
	}

	@Test
	void testTimesDirection() {
		Point3D vector = CadTransform.identity().applyDirection( new Point3D( 1, 2, 3 ) );
		assertThat( vector, is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void testTimesXY() {
		Point3D vector = CadTransform.identity().applyXY( new Point3D( 1, 2, 3 ) );
		assertThat( vector, is( new Point3D( 1, 2, 0 ) ) );
	}

	@Test
	void testTimesZ() {
		double z = CadTransform.identity().applyZ( new Point3D( 1, 2, 3 ) );
		assertThat( z, is( 3.0 ) );
	}

	@Test
	void testCombine() {
		CadTransform identity = CadTransform.identity();
		CadTransform translate = CadTransform.translation( 4, 5, 6 );
		CadTransform frustrum = CadTransform.frustrum( -1, 1, -1, 1, -1, -3 );
		assertMatrixValues( CadTransform.identity().combine( identity ), 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 );
		assertMatrixValues( CadTransform.translation( 1, 2, 3 ).combine( translate ), 1, 0, 0, 5, 0, 1, 0, 7, 0, 0, 1, 9, 0, 0, 0, 1 );
		assertMatrixValues( CadTransform.translation( 1, 2, 3 ).combine( frustrum ), -1, 0, -1, 0, 0, -1, -2, 0, 0, 0, -5, 3, 0, 0, -1, 0 );
	}

	@Test
	void testIdentity() {
		CadTransform transform = CadTransform.identity();
		assertMatrixValues( transform, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 );
	}

	@Test
	void testScale() {
		CadTransform transform = CadTransform.scale( 1, 2, 3 );
		assertMatrixValues( transform, 1, 0, 0, 0, 0, 2, 0, 0, 0, 0, 3, 0, 0, 0, 0, 1 );
		assertThat( transform.apply( new Point3D( 1, 1, 1 ) ), is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void testScaleWithOrigin() {
		CadTransform transform = CadTransform.scale( new Point3D( 1, 1, 1 ), 1, 2, 3 );
		assertMatrixValues( transform, 1, 0, 0, 0, 0, 2, 0, -1, 0, 0, 3, -2, 0, 0, 0, 1 );
		assertThat( transform.apply( new Point3D( 0, 0, 0 ) ), is( new Point3D( 0, -1, -2 ) ) );
		assertThat( transform.apply( new Point3D( 1, 1, 1 ) ), is( new Point3D( 1, 1, 1 ) ) );
		assertThat( transform.apply( new Point3D( 2, 2, 2 ) ), is( new Point3D( 2, 3, 4 ) ) );
	}

	@Test
	void testTranslation() {
		CadTransform transform = CadTransform.translation( 1, 2, 3 );
		assertMatrixValues( transform, 1, 0, 0, 1, 0, 1, 0, 2, 0, 0, 1, 3, 0, 0, 0, 1 );
		assertThat( transform.apply( new Point3D( 1, 1, 1 ) ), is( new Point3D( 2, 3, 4 ) ) );
	}

	@Test
	void testRotation() {
		CadTransform transform = CadTransform.rotation( new Point3D( 1, 1, 0 ), 180 );
		assertMatrixValues( transform, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 1 );
		assertThat( CadGeometry.distance( new Point3D( 0, 1, 0 ), transform.apply( new Point3D( 1, 0, 0 ) ) ), closeTo( 0.0, 1e-15 ) );

		transform = CadTransform.rotation( CadPoints.UNIT_Y, 0 );
		assertThat( transform.apply( CadPoints.UNIT_X ), is( CadPoints.UNIT_X ) );
	}

	@Test
	void testRotationWithZeroAxis() {
		CadTransform transform = CadTransform.rotation( Point3D.ZERO, 180 );
		assertThat( transform.apply( CadPoints.UNIT_X ), is( CadPoints.UNIT_X ) );
	}

	@Test
	void testRotationWithZeroAngle() {
		CadTransform transform = CadTransform.rotation( CadPoints.UNIT_Y, 0 );
		assertThat( transform.apply( CadPoints.UNIT_X ), is( CadPoints.UNIT_X ) );
	}

	@Test
	void testRotationWithOrigin() {
		CadTransform transform = CadTransform.rotation( new Point3D( 1, 1, 0 ), CadPoints.UNIT_Z, 90 );
		assertThat( transform.apply( new Point3D( 2, 2, 0 ) ), near( new Point3D( 0, 2, 0 ), 1e-15 ) );
	}

	@Test
	void testXrotation() {
		CadTransform transform = CadTransform.xrotation( 90 );
		assertMatrixValues( transform, 1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1 );
		assertThat( CadGeometry.distance( new Point3D( 0, 0, 1 ), transform.apply( new Point3D( 0, 1, 0 ) ) ), closeTo( 0.0, 1e-16 ) );
	}

	@Test
	void testYrotation() {
		CadTransform transform = CadTransform.yrotation( 90 );
		assertMatrixValues( transform, 0, 0, 1, 0, 0, 1, 0, 0, -1, 0, 0, 0, 0, 0, 0, 1 );
		assertThat( CadGeometry.distance( new Point3D( 0, 0, -1 ), transform.apply( new Point3D( 1, 0, 0 ) ) ), closeTo( 0.0, 1e-16 ) );
	}

	@Test
	void testZrotation() {
		CadTransform transform = CadTransform.zrotation( 90 );
		assertMatrixValues( transform, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 );
		assertThat( CadGeometry.distance( new Point3D( -1, 0, 0 ), transform.apply( new Point3D( 0, 1, 0 ) ) ), closeTo( 0.0, 1e-16 ) );
	}

	//	@Test
	//	 void testMirror() throws Exception {
	//		Transform transform = null;
	//
	//		transform = CadTransform.mirror( new Point3D( 1, 0 ), new Point3D( 1, 1 ), Vector.getUnitZ() );
	//		assertThat( new Point3D(), CadTransform.times( new Point3D( 2, 0, 0 ) ), 1e-12 );
	//
	//		transform = CadTransform.mirror( new Point3D( 2, 0 ), new Point3D( 0, 2 ), Vector.getUnitZ() );
	//		assertThat( new Point3D(), CadTransform.times( new Point3D( 2, 2, 0 ) ), 1e-12 );
	//	}

	@Test
	void testLocalCadTransform() {
		CadTransform transform = CadTransform.localTransform( new Point3D( 1, 0, 0 ), new Point3D( 0, 0, 1 ), new Point3D( 0, 1, 0 ) );
		assertMatrixValues( transform, 1, 0, 0, -1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 );
		assertThat( transform.apply( new Point3D( 1, 0, 0 ) ), is( new Point3D( 0, 0, 0 ) ) );
	}

	@Test
	void testWorldCadTransform() {
		CadTransform transform = CadTransform.targetTransform( new Point3D( 1, 0, 0 ), new Point3D( 0, 0, 1 ), new Point3D( 0, 1, 0 ) );
		assertMatrixValues( transform, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 );
		assertThat( transform.apply( new Point3D( 0, 0, 0 ) ), is( new Point3D( 1, 0, 0 ) ) );
	}

	@Test
	void testOrtho() {
		CadTransform transform = CadTransform.ortho( -1, 1, -1, 1, -1, 1 );
		assertMatrixValues( transform, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 1 );
	}

	@Test
	void testFrustrum() {
		CadTransform transform = CadTransform.frustrum( -1, 1, -1, 1, 0.2, 1 );
		assertMatrixValues( transform, 0.2, 0, 0, 0, 0, 0.2, 0, 0, 0, 0, -1.5, -0.5, 0, 0, -1, 0 );
	}

	@Test
	void testPerspective() {
		CadTransform transform = CadTransform.perspective( 1 );
		assertMatrixValues( transform, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0.25, 0.75, 0, 0, 0.25, 0.75 );
		assertThat( transform.apply( new Point3D( 1, 1, 2 ) ), is( new Point3D( 0.8, 0.8, 1 ) ) );
	}

	@Test
	void testInverseIdentity() {
		CadTransform transform = CadTransform.identity();
		transform = transform.inverse();
		assertThat( transform.apply( new Point3D( 1, 1, 1 ) ), is( new Point3D( 1, 1, 1 ) ) );
	}

	@Test
	void testInverseScale() {
		CadTransform transform = CadTransform.scale( 1, 2, 3 );
		transform = transform.inverse();
		assertThat( transform.apply( new Point3D( 1, 1, 1 ) ), is( new Point3D( 1, 1 / 2.0, 1 / 3.0 ) ) );
	}

	@Test
	void testInverseTranslation() {
		CadTransform transform = CadTransform.translation( 1, 2, 3 );
		transform = transform.inverse();
		assertThat( transform.apply( Point3D.ZERO ), is( new Point3D( -1, -2, -3 ) ) );
	}

	@Test
	void testInverseRotation() {
		CadTransform transform = CadTransform.rotation( new Point3D( 0, 0, 1 ), 90 );
		transform = transform.inverse();
		assertThat( transform.apply( new Point3D( 1, 0, 0 ) ), near( new Point3D( 0, -1, 0 ), 1E-16 ) );
	}

	@Test
	public void testInverseOrtho() {
		CadTransform transform = CadTransform.ortho( -1, 1, -1, 1, -1, -3 );
		transform = transform.inverse();
		assertThat( transform.apply( new Point3D( 0, 0, -1 ) ), is( new Point3D( 0, 0, 1 ) ) );
		assertThat( transform.apply( new Point3D( 0, 0, -0.5 ) ), is( new Point3D( 0, 0, 1.5 ) ) );
		assertThat( transform.apply( new Point3D( 0, 0, 0 ) ), is( new Point3D( 0, 0, 2 ) ) );
		assertThat( transform.apply( new Point3D( 0, 0, 0.5 ) ), is( new Point3D( 0, 0, 2.5 ) ) );
		assertThat( transform.apply( new Point3D( 0, 0, 1 ) ), is( new Point3D( 0, 0, 3 ) ) );
	}

	@Test
	public void testInverseFrustrum() {
		CadTransform transform = CadTransform.frustrum( -1, 1, -1, 1, -1, -3 );
		transform = transform.inverse();
		assertThat( transform.apply( new Point3D( 0, 0, -1 ) ), is( new Point3D( 0, 0, 1 ) ) );
		assertThat( transform.apply( new Point3D( 0, 0, 0 ) ), is( new Point3D( 0, 0, 1.5 ) ) );
		assertThat( transform.apply( new Point3D( 0, 0, 0.5 ) ), is( new Point3D( 0, 0, 2 ) ) );
		assertThat( transform.apply( new Point3D( 0, 0, 0.8 ) ), is( new Point3D( 0, 0, 2.5 ) ) );
		assertThat( transform.apply( new Point3D( 0, 0, 1 ) ), is( new Point3D( 0, 0, 3 ) ) );
	}

	@Test
	void testEquals() {
		CadTransform transform1 = CadTransform.identity();
		CadTransform transform2 = CadTransform.identity();
		CadTransform transform3 = CadTransform.translation( 3, 2, 1 );
		assertThat( transform2, is( transform1 ) );
		assertThat( transform1, is( transform2 ) );
		assertThat( transform3, is( not( transform1 ) ) );
	}

	@Test
	void testHashCode() {
		CadTransform transform = CadTransform.identity();
		assertThat( transform.hashCode(), is( 1082130432 ) );
	}

	@Test
	public void testToString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "[\n" );
		builder.append( "  1.0, 0.0, 0.0, 0.0,\n" );
		builder.append( "  0.0, 1.0, 0.0, 0.0,\n" );
		builder.append( "  0.0, 0.0, 1.0, 0.0,\n" );
		builder.append( "  0.0, 0.0, 0.0, 1.0\n" );
		builder.append( "]\n" );
		assertThat( CadTransform.identity().toString(), is( builder.toString() ) );
	}

	private static void assertMatrixValues(
		CadTransform transform,
		double e00,
		double e01,
		double e02,
		double e03,
		double e10,
		double e11,
		double e12,
		double e13,
		double e20,
		double e21,
		double e22,
		double e23,
		double e30,
		double e31,
		double e32,
		double e33
	) {
		double[][] m = transform.getMatrixArray();
		assertThat( m[ 0 ][ 0 ], closeTo( e00, 1e-12 ) );
		assertThat( m[ 0 ][ 1 ], closeTo( e01, 1e-12 ) );
		assertThat( m[ 0 ][ 2 ], closeTo( e02, 1e-12 ) );
		assertThat( m[ 0 ][ 3 ], closeTo( e03, 1e-12 ) );
		assertThat( m[ 1 ][ 0 ], closeTo( e10, 1e-12 ) );
		assertThat( m[ 1 ][ 1 ], closeTo( e11, 1e-12 ) );
		assertThat( m[ 1 ][ 2 ], closeTo( e12, 1e-12 ) );
		assertThat( m[ 1 ][ 3 ], closeTo( e13, 1e-12 ) );
		assertThat( m[ 2 ][ 0 ], closeTo( e20, 1e-12 ) );
		assertThat( m[ 2 ][ 1 ], closeTo( e21, 1e-12 ) );
		assertThat( m[ 2 ][ 2 ], closeTo( e22, 1e-12 ) );
		assertThat( m[ 2 ][ 3 ], closeTo( e23, 1e-12 ) );
		assertThat( m[ 3 ][ 0 ], closeTo( e30, 1e-12 ) );
		assertThat( m[ 3 ][ 1 ], closeTo( e31, 1e-12 ) );
		assertThat( m[ 3 ][ 2 ], closeTo( e32, 1e-12 ) );
		assertThat( m[ 3 ][ 3 ], closeTo( e33, 1e-12 ) );
	}

}
