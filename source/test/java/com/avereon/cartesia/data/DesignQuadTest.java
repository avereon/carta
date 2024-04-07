package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadConstants;
import javafx.geometry.Point3D;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignQuadTest {

	@Test
	void testModify() {
		DesignQuad quad = new DesignQuad( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( quad.isModified() ).isTrue();
		quad.setModified( false );
		assertThat( quad.isModified() ).isFalse();

		quad.setOrigin( new Point3D( 0, 0, 0 ) );
		quad.setPoint( new Point3D( 0, 0, 0 ) );
		assertThat( quad.isModified() ).isFalse();

		quad.setOrigin( new Point3D( 1, 1, 0 ) );
		assertThat( quad.isModified() ).isTrue();
		quad.setModified( false );
		assertThat( quad.isModified() ).isFalse();

		quad.setPoint( new Point3D( 2, 2, 0 ) );
		assertThat( quad.isModified() ).isTrue();
		quad.setModified( false );
		assertThat( quad.isModified() ).isFalse();
	}

	@Test
	void testOrigin() {
		DesignQuad quad = new DesignQuad( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( quad.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		quad.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( quad.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testPoint() {
		DesignQuad quad = new DesignQuad( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( quad.getPoint() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		quad.setPoint( new Point3D( 1, 2, 3 ) );
		assertThat( quad.getPoint() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testPathLength() {
		DesignQuad quad = new DesignQuad( new Point3D( 0, 0, 0 ), new Point3D( 0.5, 0.5, 0 ), new Point3D( 1, 0, 0 ) );
		assertThat( quad.pathLength() ).isEqualTo( 1.274307417012654, Offset.offset( CadConstants.RESOLUTION_LENGTH ) );

		quad = new DesignQuad( new Point3D( 0, 0, 0 ), new Point3D( 0, 1, 0 ), new Point3D( 1, 1, 0 ) );
		assertThat( quad.pathLength() ).isEqualTo( 1.802142831771924, Offset.offset( CadConstants.RESOLUTION_LENGTH ) );
	}

	@Test
	void testToMap() {
		DesignQuad quad = new DesignQuad( new Point3D( 0, 0, 0 ), new Point3D( 0.5, 0.5, 0 ), new Point3D( 1, 0, 0 ) );
		Map<String, Object> map = quad.asMap();

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

		DesignQuad quad = new DesignQuad();
		quad.updateFrom( map );

		assertThat( quad.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( quad.getControl() ).isEqualTo( new Point3D( 0.5, 0.5, 0 ) );
		assertThat( quad.getPoint() ).isEqualTo( new Point3D( 1, 0, 0 ) );
	}

}
