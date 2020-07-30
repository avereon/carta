package com.avereon.cartesia.geometry;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsaLineTest {

	@Test
	void testModify() {
		CsaLine line = new CsaLine( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertTrue( line.isModified() );
		line.setModified( false );
		assertFalse( line.isModified() );

		line.setOrigin( new Point3D( 0, 0, 0 ) );
		line.setPoint( new Point3D( 0, 0, 0 ) );
		assertFalse( line.isModified() );

		line.setOrigin( new Point3D( 1, 1, 0 ) );
		assertTrue( line.isModified() );
		line.setModified( false );
		assertFalse( line.isModified() );

		line.setPoint( new Point3D( 2, 2, 0 ) );
		assertTrue( line.isModified() );
		line.setModified( false );
		assertFalse( line.isModified() );
	}


	@Test
	void testOrigin() {
		CsaLine point = new CsaLine( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( point.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );

		point.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( point.getOrigin(), is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void testPoint() {
		CsaLine point = new CsaLine( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( point.getPoint(), is( new Point3D( 0, 0, 0 ) ) );

		point.setPoint( new Point3D( 1, 2, 3 ) );
		assertThat( point.getPoint(), is( new Point3D( 1, 2, 3 ) ) );
	}

}
