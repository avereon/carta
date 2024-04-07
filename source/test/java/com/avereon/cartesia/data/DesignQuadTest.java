package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignQuadTest {

	@Test
	void testModify() {
		DesignQuad curve = new DesignQuad( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( curve.isModified() ).isTrue();
		curve.setModified( false );
		assertThat( curve.isModified() ).isFalse();

		curve.setOrigin( new Point3D( 0, 0, 0 ) );
		curve.setPoint( new Point3D( 0, 0, 0 ) );
		assertThat( curve.isModified() ).isFalse();

		curve.setOrigin( new Point3D( 1, 1, 0 ) );
		assertThat( curve.isModified() ).isTrue();
		curve.setModified( false );
		assertThat( curve.isModified() ).isFalse();

		curve.setPoint( new Point3D( 2, 2, 0 ) );
		assertThat( curve.isModified() ).isTrue();
		curve.setModified( false );
		assertThat( curve.isModified() ).isFalse();
	}

	@Test
	void testOrigin() {
		DesignQuad curve = new DesignQuad( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( curve.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		curve.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( curve.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testPoint() {
		DesignQuad curve = new DesignQuad( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( curve.getPoint() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		curve.setPoint( new Point3D( 1, 2, 3 ) );
		assertThat( curve.getPoint() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testPathLength() {
//		DesignQuad curve = new DesignQuad( new Point3D( 0, 0, 0 ), new Point3D( 0, 1, 0 ), new Point3D( 1, 1, 0 ), new Point3D( 1, 0, 0 ) );
//		assertThat( curve.pathLength() ).isEqualTo( 2.0, Offset.offset( CadConstants.RESOLUTION_LENGTH ) );
//
//		curve = new DesignQuad( new Point3D( 0, 0, 0 ), new Point3D( 0, 1, 0 ), new Point3D( 1, 1, 0 ), new Point3D( 2, 1, 0 ) );
//		assertThat( curve.pathLength() ).isEqualTo( 2.550645, Offset.offset( CadConstants.RESOLUTION_LENGTH ) );
	}

	@Test
	void testToMap() {
		DesignQuad curve = new DesignQuad( new Point3D( 0, 0, 0 ), new Point3D( 0.5, 0.5, 0 ), new Point3D( 1, 0, 0 ) );
		Map<String, Object> map = curve.asMap();

		assertThat( map.get( DesignQuad.SHAPE ) ).isEqualTo( DesignQuad.QUAD );
		assertThat( map.get( DesignQuad.ORIGIN ) ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( map.get( DesignQuad.CONTROL ) ).isEqualTo( new Point3D( 0.5, 0.5, 0 ) );
		assertThat( map.get( DesignQuad.POINT ) ).isEqualTo( new Point3D( 1, 0, 0 ) );
	}

	@Test
	void testUpdateFrom() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignQuad.SHAPE, DesignQuad.QUAD );
		map.put( DesignQuad.ORIGIN, "0,0,0" );
		map.put( DesignQuad.CONTROL, "0.5,0.5,0" );
		map.put( DesignQuad.POINT, "1,0,0" );

		DesignQuad curve = new DesignQuad();
		curve.updateFrom( map );

		assertThat( curve.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( curve.getControl() ).isEqualTo( new Point3D( 0.5, 0.5, 0 ) );
		assertThat( curve.getPoint() ).isEqualTo( new Point3D( 1, 0, 0 ) );
	}

}
