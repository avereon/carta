package com.avereon.cartesia.data;

import com.avereon.cartesia.tool.design.FxBoundsAssert;
import com.avereon.curve.math.Constants;
import com.avereon.zarra.color.Paints;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.avereon.cartesia.TestConstants.LOOSE_TOLERANCE;
import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static com.avereon.cartesia.math.CadMath.SQRT2;
import static com.avereon.cartesia.math.CadMath.SQRT2_OVER_2;
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
		// This is the geometrically correct bounds
		//FxBoundsAssert.assertThat( bounds ).isEqualTo( new BoundingBox( -2, -2, 4,4 ), TOLERANCE );

		// But this is what is computed by JavaFX
		FxBoundsAssert.assertThat( bounds ).isEqualTo( new BoundingBox( -2.5, -2.5, 5, 5 ), TOLERANCE );
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
		FxBoundsAssert.assertThat( bounds ).isEqualTo( new BoundingBox( -2 - SQRT2_OVER_2, -2 - SQRT2_OVER_2, 4 + SQRT2, 4 + SQRT2 ), LOOSE_TOLERANCE );
	}

	@Test
	void getVisualBoundsWithNoLengthAndZeroStrokeWidth() {
		// given
		DesignLine line = new DesignLine( new Point3D( 1, 2, 0 ), new Point3D( 1, 2, 0 ) );
		line.setDrawWidth( "0.0" );

		// when
		Bounds bounds = line.getVisualBounds();

		// then
		// This is the geometrically correct bounds
		//assertThat( bounds ).isEqualTo( new BoundingBox( 1, 2, 0, 0 ) );

		// But this is what is computed by JavaFX
		assertThat( bounds ).isEqualTo( new BoundingBox( 0.5, 1.5, 1, 1 ) );
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
		// This is the geometrically correct bounds
		//assertThat( bounds ).isEqualTo( new BoundingBox( 1, 1.5, 0, 1 ) );

		// But this is what is computed by JavaFX
		assertThat( bounds ).isEqualTo( new BoundingBox( 0.5, 1.5, 1, 1 ) );
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
		assertThat( bounds ).isEqualTo( new BoundingBox( 0.5, 1.5, 1, 1 ) );
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
		// This is the geometrically correct bounds
		//assertThat( bounds ).isEqualTo( new BoundingBox( 0.5, 1.5, 1, 1 ) );

		// But this is what is computed by JavaFX
		FxBoundsAssert.assertThat( bounds ).isEqualTo( new BoundingBox( 1 - SQRT2_OVER_2, 2 - SQRT2_OVER_2, SQRT2, SQRT2 ), LOOSE_TOLERANCE );
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
		// This is the geometrically correct bounds
		//double TEN_OVER_SQRT2 = 10 / SQRT2;
		//double TEN_OVER_SQRT2_OVER_2 = 10 / SQRT2_OVER_2;
		//assertThat( bounds ).isEqualTo( new BoundingBox( -2-TEN_OVER_SQRT2_OVER_2, -2-TEN_OVER_SQRT2_OVER_2, 4+TEN_OVER_SQRT2, 4+TEN_OVER_SQRT2 ) );

		// But this is what is computed by JavaFX
		FxBoundsAssert.assertThat( bounds ).isEqualTo( new BoundingBox( -2 - SQRT2_OVER_2, -2 - SQRT2_OVER_2, 4 + SQRT2, 4 + SQRT2 ), LOOSE_TOLERANCE );
	}

}
