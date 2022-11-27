package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignPathTest {

	@Test
	void testEmptyPath() {
		DesignPath path = new DesignPath();
		assertThat( path.getPathElements().size() ).isEqualTo( 0 );
		assertThat( path.pathLength() ).isNaN();
	}

	@Test
	void testAddLine() {
		DesignLine l = new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 1, 0, 0 ) );
		DesignPath path = new DesignPath();
		path.add( l );

		assertThat( path.getPathElements().get( 0 ) ).isEqualTo( l );
		assertThat( path.getPathElements().size() ).isEqualTo( 1 );
	}

	@Test
	void testAddShapes() {
		DesignLine l = new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 1, 0, 0 ) );
		DesignArc a = new DesignArc( new Point3D( 2, 0, 0 ), 1.0, 1.0, Math.PI, -0.5 * Math.PI, DesignArc.Type.OPEN );
		DesignCurve c = new DesignCurve( new Point3D( 2, 1, 0 ), new Point3D( 3, 1, 0 ), new Point3D( 2, 0, 0 ), new Point3D( 3, 0, 0 ) );

		DesignPath path = new DesignPath();
		path.add( l );
		path.add( a );
		path.add( c );
		assertThat( path.getPathElements().get( 0 ) ).isEqualTo( l );
		assertThat( path.getPathElements().get( 1 ) ).isEqualTo( a );
		assertThat( path.getPathElements().get( 2 ) ).isEqualTo( c );
		assertThat( path.getPathElements().size() ).isEqualTo( 3 );
	}

}
