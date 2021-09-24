package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.match.Near.near;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DesignMarkerTest {

	@Test
	void testModify() {
		DesignMarker point = new DesignMarker( new Point3D( 0, 0, 0 ) );
		Assertions.assertTrue( point.isModified() );
		point.setModified( false );
		Assertions.assertFalse( point.isModified() );

		point.setOrigin( new Point3D( 1, 1, 0 ) );
		assertTrue( point.isModified() );
		point.setOrigin( new Point3D( 0, 0, 0 ) );
		Assertions.assertFalse( point.isModified() );

		point.setOrigin( new Point3D( 1, 1, 0 ) );
		assertTrue( point.isModified() );
		point.setModified( false );
		assertFalse( point.isModified() );
	}

	@Test
	void testOrigin() {
		DesignMarker point = new DesignMarker( new Point3D( 0, 0, 0 ) );
		assertThat( point.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );

		point.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( point.getOrigin(), is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void testDistanceTo() {
		assertThat( new DesignMarker( new Point3D( -2, 1, 0 ) ).distanceTo( new Point3D( 2, -2, 0 ) ), near( 5.0 ) );
	}

	@Test
	void testPathLength() {
		assertThat( new DesignMarker( new Point3D( -2, 1, 0 ) ).pathLength(), near( 0.0 ) );
	}

}
