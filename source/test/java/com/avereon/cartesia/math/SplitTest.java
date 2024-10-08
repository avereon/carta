package com.avereon.cartesia.math;

import com.avereon.cartesia.data.*;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SplitTest {

	@Test
	void testSplitLine() {
		DesignLine a = new DesignLine( new Point3D( 1, 1, 0 ), new Point3D( 3, 1, 0 ) );
		DesignLine b = new DesignLine( new Point3D( 1, 1, 0 ), new Point3D( 2, 1, 0 ) );
		DesignLine c = new DesignLine( new Point3D( 2, 1, 0 ), new Point3D( 3, 1, 0 ) );

		Collection<DesignShape> shapes = Split.splitLine( a, new Point3D( 2, 0, 0 ) );

		assertThat( containsLine( shapes, b ) ).isTrue();
		assertThat( containsLine( shapes, c ) ).isTrue();
		assertThat( shapes.isEmpty() ).isFalse();
	}

	@Test
	void testSplitEllipse() {
		double start = 36.86989764584403;
		DesignEllipse a = new DesignEllipse( new Point3D( 1, 2, 0 ), 5.0 );
		DesignArc arc = new DesignArc( new Point3D( 1, 2, 0 ), 5.0, start, 360.0, DesignArc.Type.OPEN );

		Set<DesignShape> shapes = Split.splitEllipse( a, new Point3D( 9, 8, 0 ) );

		assertThat( containsArc( shapes, arc ) ).isTrue();
		assertThat( shapes.isEmpty() ).isFalse();
	}

	@Test
	void testSplitArc() {
		DesignArc a = new DesignArc( new Point3D( 1, 2, 0 ), 5.0, 45.0, 180.0, DesignArc.Type.OPEN );
		DesignArc b = new DesignArc( new Point3D( 1, 2, 0 ), 5.0, 45.0, 90.0, DesignArc.Type.OPEN );
		DesignArc c = new DesignArc( new Point3D( 1, 2, 0 ), 5.0, 135.0, 90.0, DesignArc.Type.OPEN );

		Set<DesignShape> shapes = Split.splitArc( a, new Point3D( -9, 12, 0 ) );

		assertThat( containsArc( shapes, b ) ).isTrue();
		assertThat( containsArc( shapes, c ) ).isTrue();
		assertThat( shapes.isEmpty() ).isFalse();
	}

	@Test
	void testSplitCurve() {
		Point3D mouse = new Point3D( 0.5, 2, 0 );
		DesignCubic a = new DesignCubic( new Point3D( 0, 1, 0 ), new Point3D( 1, 2, 0 ), new Point3D( 1, 0, 0 ), new Point3D( 2, 1, 0 ) );
		DesignCubic b = CadGeometry.cubicSubdivide( a, CadGeometry.getCubicParametricValueNear( a, mouse ) ).get( 0 );
		DesignCubic c = CadGeometry.cubicSubdivide( a, CadGeometry.getCubicParametricValueNear( a, mouse ) ).get( 1 );

		Set<DesignShape> shapes = Split.splitCurve( a, mouse );

		assertThat( containsCurve( shapes, b ) ).isTrue();
		assertThat( containsCurve( shapes, c ) ).isTrue();
		assertThat( shapes.isEmpty() ).isFalse();
	}

	private static boolean containsLine( Collection<DesignShape> shapes, DesignShape shape ) {
		for( DesignShape test : shapes ) {
			if( test.equals( shape, DesignLine.ORIGIN, DesignLine.POINT ) ) return true;
		}
		return false;
	}

	private static boolean containsArc( Collection<DesignShape> shapes, DesignShape shape ) {
		for( DesignShape test : shapes ) {
			if( test.equals( shape, DesignArc.ORIGIN, DesignArc.RADII, DesignArc.ROTATE, DesignArc.START, DesignArc.EXTENT ) ) return true;
		}
		return false;
	}

	private static boolean containsCurve( Collection<DesignShape> shapes, DesignShape shape ) {
		for( DesignShape test : shapes ) {
			if( test.equals( shape, DesignCubic.ORIGIN, DesignCubic.ORIGIN_CONTROL, DesignCubic.POINT_CONTROL, DesignLine.POINT ) ) return true;
		}
		return false;
	}

}
