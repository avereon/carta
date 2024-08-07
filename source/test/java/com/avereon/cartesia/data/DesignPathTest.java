package com.avereon.cartesia.data;

import com.avereon.cartesia.BaseCartesiaUnitTest;
import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.test.Point3DAssert;
import com.avereon.curve.math.Geometry;
import com.avereon.curve.math.Point;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class DesignPathTest extends BaseCartesiaUnitTest {

	@Test
	void constructor() {
		DesignPath path = new DesignPath();
		assertThat( path.isModified() ).isFalse();
		assertThat( path.getOrigin() ).isNull();
	}

	@Test
	void constructorWithOrigin() {
		DesignPath path = new DesignPath( Point3D.ZERO );
		assertThat( path.isModified() ).isTrue();
		assertThat( path.getOrigin() ).isEqualTo( Point3D.ZERO );
	}

	@Test
	void modifyFlag() {
		DesignPath path = new DesignPath( Point3D.ZERO );
		assertThat( path.isModified() ).isTrue();
		path.setModified( false );
		assertThat( path.isModified() ).isFalse();
	}

	@Test
	void moveOrigin() {
		// given
		DesignPath path = new DesignPath( Point3D.ZERO );
		path.setModified( false );
		assertThat( path.getOrigin() ).isEqualTo( Point3D.ZERO );
		assertThat( path.getSteps() ).hasSize( 1 );
		assertThat( path.isModified() ).isFalse();

		// when
		path.setOrigin( new Point3D( 2, 2, 0 ) );

		// then
		assertThat( path.getOrigin() ).isEqualTo( new Point3D( 2, 2, 0 ) );
		assertThat( path.getSteps() ).hasSize( 1 );
		assertThat( path.isModified() ).isTrue();
	}

	@Test
	void setSteps() {
		DesignPath path = new DesignPath( Point3D.ZERO );
		path.setModified( false );
		assertThat( path.getOrigin() ).isEqualTo( Point3D.ZERO );
		assertThat( path.getSteps() ).hasSize( 1 );
		assertThat( path.isModified() ).isFalse();

		path.setSteps( List.of( new DesignPath.Step( DesignPath.Command.M, 1, 1 ), new DesignPath.Step( DesignPath.Command.L, 2, 2 ) ) );
		assertThat( path.getSteps() ).hasSize( 2 );
		assertThat( path.isModified() ).isTrue();
	}

	@Test
	void distanceTo() {
		// given
		DesignPath path = new DesignPath( new Point3D( 1, 1, 0 ) );
		path.line( 2, 2 );
		path.arc( 3, 3, 0.5, 0.5, 0, 0, 1 );
		path.quad( 6, 6, 7, 5 );
		path.cubic( 8, 8, 9, 8, 10, 5 );

		// when
		// Leave the path open

		// then
		assertThat( path.distanceTo( new Point3D( 3, 0, 0 ) ) ).isEqualTo( 2.1213203435596424, TOLERANCE );

		// given
		// Check that this point is closer to the start point before closing the path
		assertThat( path.distanceTo( new Point3D( 2, 0, 0 ) ) ).isEqualTo( CadMath.SQRT2, TOLERANCE );

		// Close the path
		path.close();

		// then
		double expected = Geometry.pointLineDistance( Point.of( 2, 0 ), Point.of( 10, 5 ), Point.of( 1, 1 ) );
		assertThat( path.distanceTo( new Point3D( 2, 0, 0 ) ) ).isEqualTo( expected, TOLERANCE );
	}

	@Test
	void pathLength() {
		// given
		DesignPath path = new DesignPath( new Point3D( 1, 1, 0 ) );
		path.line( 2, 2 );
		path.arc( 3, 3, 0.5, 0.5, 0, 0, 0 );
		path.quad( 6, 6, 7, 5 );
		path.cubic( 8, 8, 9, 8, 10, 5 );

		// then
		assertThat( path.pathLength() ).isEqualTo( 11.899803353029451, TOLERANCE );

		path.close();
		assertThat( path.pathLength() ).isEqualTo( 21.748661154825555, TOLERANCE );
	}

	@Test
	void asMap() {
		// given
		DesignPath path = new DesignPath( new Point3D( 1, 1, 0 ) );
		path.line( 2, 2 );
		path.arc( 3, 3, 0.5, 0.5, 0, 0, 0 );
		path.quad( 6, 6, 7, 7 );
		path.cubic( 8, 8, 9, 9, 10, 11 );
		path.close();

		// when
		var map = path.asMap();

		// then
		assertThat( map.get( "id" ) ).isEqualTo( path.getId() );
		assertThat( map.get( "origin" ) ).isEqualTo( new Point3D( 1, 1, 0 ) );
		assertThat( map.get( "shape" ) ).isEqualTo( "path" );
		assertThat( map.get( "steps" ) ).isEqualTo( List.of(
			DesignPath.Command.M + " 1.0 1.0",
			DesignPath.Command.L + " 2.0 2.0",
			DesignPath.Command.A + " 3.0 3.0 0.5 0.5 0.0 0.0 0.0",
			DesignPath.Command.Q + " 6.0 6.0 7.0 7.0",
			DesignPath.Command.B + " 8.0 8.0 9.0 9.0 10.0 11.0",
			DesignPath.Command.Z.toString()
		) );
	}

	@Test
	void getReferencePoints() {
		// given
		DesignPath path = new DesignPath( new Point3D( 1, 1, 0 ) );
		path.line( 2, 2 );
		path.arc( 3, 3, 0.5, 0.5, 0, 0, 0 );
		path.quad( 6, 6, 7, 7 );
		path.cubic( 8, 8, 9, 9, 10, 11 );
		path.close();

		// when
		List<Point3D> points = path.getReferencePoints();

		// then
		Point3DAssert.assertThat( points.getFirst() ).isCloseTo( new Point3D( 1, 1, 0 ) );
		Point3DAssert.assertThat( points.get( 1 ) ).isCloseTo( new Point3D( 2, 2, 0 ) );
		Point3DAssert.assertThat( points.get( 2 ) ).isCloseTo( new Point3D( 3, 3, 0 ) );
		Point3DAssert.assertThat( points.get( 3 ) ).isCloseTo( new Point3D( 7, 7, 0 ) );
		Point3DAssert.assertThat( points.get( 4 ) ).isCloseTo( new Point3D( 10, 11, 0 ) );
		assertThat( points ).hasSize( 5 );
	}

}
