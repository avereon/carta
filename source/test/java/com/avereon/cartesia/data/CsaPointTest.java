package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsaPointTest {

	@Test
	void testModify() {
		CsaPoint point = new CsaPoint( new Point3D( 0, 0, 0 ) );
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
		CsaPoint point = new CsaPoint( new Point3D( 0, 0, 0 ) );
		assertThat( point.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );

		point.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( point.getOrigin(), is( new Point3D( 1, 2, 3 ) ) );
	}

}
