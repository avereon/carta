package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DesignLineTest {

	@Test
	void testModify() {
		DesignLine line = new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
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
		DesignLine line = new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( line.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );

		line.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( line.getOrigin(), is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void testPoint() {
		DesignLine line = new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( line.getPoint(), is( new Point3D( 0, 0, 0 ) ) );

		line.setPoint( new Point3D( 1, 2, 3 ) );
		assertThat( line.getPoint(), is( new Point3D( 1, 2, 3 ) ) );
	}

	@Test
	void testToMap() {
		DesignLine line = new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 1, 0, 0 ) );
		Map<String, Object> map = line.asMap();

		assertThat( map.get( DesignLine.SHAPE ), is( DesignLine.LINE ) );
		assertThat( map.get( DesignLine.ORIGIN ), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( map.get( DesignLine.POINT ), is( new Point3D( 1, 0, 0 ) ) );
	}

	@Test
	void testUpdateFrom() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignLine.SHAPE, DesignLine.LINE );
		map.put( DesignLine.ORIGIN, "0,0,0" );
		map.put( DesignLine.POINT, "1,0,0" );

		DesignLine line = new DesignLine();
		line.updateFrom( map );

		assertThat( line.getOrigin(), is( new Point3D( 0, 0, 0 ) ) );
		assertThat( line.getPoint(), is( new Point3D( 1, 0, 0 ) ) );
	}

	@Test
	void testDistanceTo() {
		DesignLine line = new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 1, 0, 0 ) );
		assertThat( line.distanceTo( new Point3D( 0.5, 0.5, 0 ) ), is( 0.5 ) );
	}

}
