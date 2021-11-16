package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class DesignMarkerTest {

	@Test
	void testModify() {
		DesignMarker point = new DesignMarker( new Point3D( 0, 0, 0 ) );
		assertThat( point.isModified() ).isTrue();
		point.setModified( false );
		assertThat( point.isModified() ).isFalse();

		point.setOrigin( new Point3D( 1, 1, 0 ) );
		assertThat( point.isModified() ).isTrue();
		point.setOrigin( new Point3D( 0, 0, 0 ) );
		assertThat( point.isModified() ).isFalse();

		point.setOrigin( new Point3D( 1, 1, 0 ) );
		assertThat( point.isModified() ).isTrue();
		point.setModified( false );
		assertThat( point.isModified() ).isFalse();
	}

	@Test
	void testOrigin() {
		DesignMarker point = new DesignMarker( new Point3D( 0, 0, 0 ) );
		assertThat( point.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		point.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( point.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testDistanceTo() {
		assertThat( new DesignMarker( new Point3D( -2, 1, 0 ) ).distanceTo( new Point3D( 2, -2, 0 ) ) ).isCloseTo( 5.0, TOLERANCE );
	}

	@Test
	void testPathLength() {
		assertThat( new DesignMarker( new Point3D( -2, 1, 0 ) ).pathLength() ).isCloseTo( 0.0, TOLERANCE );
	}

}
