package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadMath;
import com.avereon.curve.math.Constants;
import com.avereon.zarra.color.Paints;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class DesignLineTest {

	@Test
	void testModify() {
		DesignLine line = new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( line.isModified() ).isTrue();
		line.setModified( false );
		assertThat( line.isModified() ).isFalse();

		line.setOrigin( new Point3D( 0, 0, 0 ) );
		line.setPoint( new Point3D( 0, 0, 0 ) );
		assertThat( line.isModified() ).isFalse();

		line.setOrigin( new Point3D( 1, 1, 0 ) );
		assertThat( line.isModified() ).isTrue();
		line.setModified( false );
		assertThat( line.isModified() ).isFalse();

		line.setPoint( new Point3D( 2, 2, 0 ) );
		assertThat( line.isModified() ).isTrue();
		line.setModified( false );
		assertThat( line.isModified() ).isFalse();
	}

	@Test
	void testOrigin() {
		DesignLine line = new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( line.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		line.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( line.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testPoint() {
		DesignLine line = new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 0, 0, 0 ) );
		assertThat( line.getPoint() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		line.setPoint( new Point3D( 1, 2, 3 ) );
		assertThat( line.getPoint() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testToMap() {
		DesignLine line = new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 1, 0, 0 ) );
		Map<String, Object> map = line.asMap();

		assertThat( map.get( DesignLine.SHAPE ) ).isEqualTo( DesignLine.LINE );
		assertThat( map.get( DesignLine.ORIGIN ) ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( map.get( DesignLine.POINT ) ).isEqualTo( new Point3D( 1, 0, 0 ) );
	}

	@Test
	void testUpdateFrom() {
		Map<String, Object> map = new HashMap<>();
		map.put( DesignLine.SHAPE, DesignLine.LINE );
		map.put( DesignLine.ORIGIN, "0,0,0" );
		map.put( DesignLine.POINT, "1,0,0" );

		DesignLine line = new DesignLine();
		line.updateFrom( map );

		assertThat( line.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );
		assertThat( line.getPoint() ).isEqualTo( new Point3D( 1, 0, 0 ) );
	}

	@Test
	void testDistanceTo() {
		DesignLine line = new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 1, 0, 0 ) );
		assertThat( line.distanceTo( new Point3D( 0.5, 0.5, 0 ) ) ).isEqualTo( 0.5 );
	}

	@Test
	void testPathLength() {
		assertThat( new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 1, 0, 0 ) ).pathLength() ).isEqualTo( 1.0, TOLERANCE );
		assertThat( new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 0, -1, 0 ) ).pathLength() ).isEqualTo( 1.0, TOLERANCE );
		assertThat( new DesignLine( new Point3D( 0, 0, 0 ), new Point3D( 1, 1, 0 ) ).pathLength() ).isEqualTo( Constants.SQRT_TWO, TOLERANCE );
		assertThat( new DesignLine( new Point3D( -2, 1, 0 ), new Point3D( 2, -2, 0 ) ).pathLength() ).isEqualTo( 5.0, TOLERANCE );
	}

	@Test
	void getVisualBoundsWithZeroStrokeWidth() {
		// given
		DesignLine line = new DesignLine( new Point3D( -2, -2, 0 ), new Point3D( 2, 2, 0 ) );
		line.setDrawWidth( "0.0" );

		// when
		Bounds bounds = line.getVisualBounds();

		// then
		assertThat( bounds.getMinX() ).isEqualTo( -2, TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( -2, TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( 2, TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( 2, TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( 4, TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( 4, TOLERANCE );
	}

	@Test
	void getVisualBoundsWithStroke() {
		// given
		DesignLine line = new DesignLine( new Point3D( -2, -2, 0 ), new Point3D( 2, 2, 0 ) );
		line.setDrawPaint( Paints.toString( Color.WHITE ) );
		line.setDrawCap( "square" );
		line.setDrawWidth( "1.0" );

		// when
		Bounds bounds = line.getVisualBounds();

		// then
		assertThat( bounds.getMinX() ).isEqualTo( -2 - CadMath.SQRT2_OVER_2, TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( -2 - CadMath.SQRT2_OVER_2, TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( 2 + CadMath.SQRT2_OVER_2, TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( 2 + CadMath.SQRT2_OVER_2, TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( 4 + CadMath.SQRT2, TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( 4 + CadMath.SQRT2, TOLERANCE );
	}

	@Test
	void getVisualBoundsWithNoLengthAndZeroStrokeWidth() {
		// given
		DesignLine line = new DesignLine( new Point3D( 1, 2, 0 ), new Point3D( 1, 2, 0 ) );
		line.setDrawWidth( "0.0" );

		// when
		Bounds bounds = line.getVisualBounds();

		// then
		assertThat( bounds.getMinX() ).isEqualTo( 1 );
		assertThat( bounds.getMinY() ).isEqualTo( 2 );
		assertThat( bounds.getMaxX() ).isEqualTo( 1 );
		assertThat( bounds.getMaxY() ).isEqualTo( 2 );
		assertThat( bounds.getWidth() ).isEqualTo( 0 );
		assertThat( bounds.getHeight() ).isEqualTo( 0 );
	}

	@Test
	void getVisualBoundsWithNoLengthAndAndButtCaps() {
		// given
		DesignLine line = new DesignLine( new Point3D( 1, 2, 0 ), new Point3D( 1, 2, 0 ) );
		line.setDrawWidth( "1.0" );
		line.setDrawCap( "butt" );

		// when
		Bounds bounds = line.getVisualBounds();

		// then
		assertThat( bounds.getMinX() ).isEqualTo( 1 );
		assertThat( bounds.getMinY() ).isEqualTo( 1.5 );
		assertThat( bounds.getMaxX() ).isEqualTo( 1 );
		assertThat( bounds.getMaxY() ).isEqualTo( 2.5 );
		assertThat( bounds.getWidth() ).isEqualTo( 0 );
		assertThat( bounds.getHeight() ).isEqualTo( 1 );
	}

	@Test
	void getVisualBoundsWithNoLengthAndAndRoundCaps() {
		// given
		DesignLine line = new DesignLine( new Point3D( 1, 2, 0 ), new Point3D( 1, 2, 0 ) );
		line.setDrawWidth( "1.0" );
		line.setDrawCap( "round" );

		// when
		Bounds bounds = line.getVisualBounds();

		// then
		assertThat( bounds.getMinX() ).isEqualTo( 0.5 );
		assertThat( bounds.getMinY() ).isEqualTo( 1.5 );
		assertThat( bounds.getMaxX() ).isEqualTo( 1.5 );
		assertThat( bounds.getMaxY() ).isEqualTo( 2.5 );
		assertThat( bounds.getWidth() ).isEqualTo( 1 );
		assertThat( bounds.getHeight() ).isEqualTo( 1 );
	}

	@Test
	void getVisualBoundsWithNoLengthAndAndSquareCaps() {
		// given
		DesignLine line = new DesignLine( new Point3D( 1, 2, 0 ), new Point3D( 1, 2, 0 ) );
		line.setDrawWidth( "1.0" );
		line.setDrawCap( "square" );

		// when
		Bounds bounds = line.getVisualBounds();

		// then
		assertThat( bounds.getMinX() ).isEqualTo( 0.5 );
		assertThat( bounds.getMinY() ).isEqualTo( 1.5 );
		assertThat( bounds.getMaxX() ).isEqualTo( 1.5 );
		assertThat( bounds.getMaxY() ).isEqualTo( 2.5 );
		assertThat( bounds.getWidth() ).isEqualTo( 1 );
		assertThat( bounds.getHeight() ).isEqualTo( 1 );
	}

	@Test
	void getVisualBoundsWithNarrowWidth() {
		// given
		DesignLine line = new DesignLine( new Point3D( -2, -2, 0 ), new Point3D( 2, 2, 0 ) );
		line.setDrawWidth( "0.1" );
		line.setDrawCap( "square" );

		// when
		Bounds bounds = line.getVisualBounds();

		// then
		assertThat( bounds.getMinX() ).isEqualTo( -2.0707106781186546, TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( -2.0707106781186546, TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( 2.0707106781186546, TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( 2.0707106781186546, TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( 4.14142135623731, TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( 4.14142135623731, TOLERANCE );
	}

}
