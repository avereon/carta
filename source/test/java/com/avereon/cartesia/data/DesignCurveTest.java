package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadConstants;
import javafx.geometry.Point3D;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignCurveTest {

	@Test
	void testModify() {
		DesignCurve curve = new DesignCurve( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
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
		DesignCurve curve = new DesignCurve( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( curve.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		curve.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( curve.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testPoint() {
		DesignCurve curve = new DesignCurve( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( curve.getPoint() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		curve.setPoint( new Point3D( 1, 2, 3 ) );
		assertThat( curve.getPoint() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testPathLength() {
		DesignCurve curve = new DesignCurve( new Point3D( 0, 0, 0 ), new Point3D( 0, 1, 0 ), new Point3D( 1, 1, 0 ), new Point3D( 1, 0, 0 ) );
		assertThat( curve.pathLength() ).isEqualTo( 2.0, Offset.offset( CadConstants.RESOLUTION_LENGTH ) );

		curve = new DesignCurve( new Point3D( 0, 0, 0 ), new Point3D( 0, 1, 0 ), new Point3D( 1, 1, 0 ), new Point3D( 2, 1, 0 ) );
		assertThat( curve.pathLength() ).isEqualTo( 2.550645, Offset.offset( CadConstants.RESOLUTION_LENGTH ) );
	}

	@Test
	void testToMap() {
		DesignCurve curve = new DesignCurve( new Point3D( 0, 0, 0 ), new Point3D( 0.5, 0.5, 0 ), new Point3D( 0.5, -0.5, 0 ), new Point3D( 1, 0, 0 ) );
		Map<String, Object> map = curve.asMap();

		assertThat( map.get( DesignCurve.SHAPE ) ).isEqualTo( DesignCurve.CURVE );
		assertThat( map.get( DesignCurve.ORIGIN ) ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( map.get( DesignCurve.ORIGIN_CONTROL ) ).isEqualTo( new Point3D( 0.5, 0.5, 0 ) );
		assertThat( map.get( DesignCurve.POINT_CONTROL ) ).isEqualTo( new Point3D( 0.5, -0.5, 0 ) );
		assertThat( map.get( DesignCurve.POINT ) ).isEqualTo( new Point3D( 1, 0, 0 ) );
	}

	@Test
	void testUpdateFrom() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignCurve.SHAPE, DesignCurve.CURVE );
		map.put( DesignCurve.ORIGIN, "0,0,0" );
		map.put( DesignCurve.ORIGIN_CONTROL, "0.5,0.5,0" );
		map.put( DesignCurve.POINT_CONTROL, "0.5,-0.5,0" );
		map.put( DesignCurve.POINT, "1,0,0" );

		DesignCurve curve = new DesignCurve();
		curve.updateFrom( map );

		assertThat( curve.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( curve.getOriginControl() ).isEqualTo( new Point3D( 0.5, 0.5, 0 ) );
		assertThat( curve.getPointControl() ).isEqualTo( new Point3D( 0.5, -0.5, 0 ) );
		assertThat( curve.getPoint() ).isEqualTo( new Point3D( 1, 0, 0 ) );
	}

}
