package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.test.Point3DAssert;
import com.avereon.zerra.color.Paints;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.avereon.cartesia.TestConstants.TOLERANCE_PERCENT_LOOSE;
import static org.assertj.core.api.Assertions.assertThat;

public class DesignBoxTest {

	@Test
	void getBounds() {
		// given
		DesignBox box = new DesignBox( new Point3D( 2, 1, 0 ), new Point3D( 4, 2, 0 ) );
		box.setDrawPaint( null );
		box.setFillPaint( "#ffffffff" );

		// The default draw width is 0.05

		// when
		Bounds bounds = box.getBounds();

		assertThat( bounds.getMinX() ).isEqualTo( 2 );
		assertThat( bounds.getMinY() ).isEqualTo( 1 );
		assertThat( bounds.getMaxX() ).isEqualTo( 6 );
		assertThat( bounds.getMaxY() ).isEqualTo( 3 );
		assertThat( bounds.getWidth() ).isEqualTo( 4 );
		assertThat( bounds.getHeight() ).isEqualTo( 2 );
	}

	@Test
	void getBoundsWithStroke() {
		// given
		DesignBox box = new DesignBox( new Point3D( 2, 1, 0 ), new Point3D( 4, 2, 0 ) );
		box.setDrawPaint( null );
		box.setDrawWidth( "1" );
		box.setFillPaint( "#ffffffff" );

		// when
		Bounds bounds = box.getBounds();

		assertThat( bounds.getMinX() ).isEqualTo( 2 );
		assertThat( bounds.getMinY() ).isEqualTo( 1 );
		assertThat( bounds.getMaxX() ).isEqualTo( 6 );
		assertThat( bounds.getMaxY() ).isEqualTo( 3 );
		assertThat( bounds.getWidth() ).isEqualTo( 4 );
		assertThat( bounds.getHeight() ).isEqualTo( 2 );
	}

	@Test
	void getBoundsWithRotate() {
		// given
		DesignBox box = new DesignBox( 2, 1, 1, 1 );
		box.setDrawPaint( null );
		box.setFillPaint( "#ffffffff" );
		box.setRotate( 45 );

		// when
		Bounds bounds = box.getBounds();

		assertThat( bounds.getMinX() ).isCloseTo( 2 - CadMath.SQRT2_OVER_2, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMaxX() ).isCloseTo( 2 + CadMath.SQRT2_OVER_2, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMinY() ).isCloseTo( 1, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMaxY() ).isCloseTo( 1 + CadMath.SQRT2, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getWidth() ).isCloseTo( CadMath.SQRT2, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getHeight() ).isCloseTo( CadMath.SQRT2, TOLERANCE_PERCENT_LOOSE );
	}

	@Test
	void getVisualBounds() {
		// given
		DesignBox box = new DesignBox( new Point3D( 2, 1, 0 ), new Point3D( 4, 2, 0 ) );

		// when
		Bounds bounds = box.getSelectBounds();

		assertThat( bounds.getMinX() ).isCloseTo( 1.975, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMinY() ).isCloseTo( 0.975, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMaxX() ).isCloseTo( 6.025, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMaxY() ).isCloseTo( 3.025, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getWidth() ).isCloseTo( 4.05, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getHeight() ).isCloseTo( 2.05, TOLERANCE_PERCENT_LOOSE );
	}

	@Test
	void getVisualBoundsWithStroke() {
		// given
		DesignBox box = new DesignBox( new Point3D( 2, 1, 0 ), new Point3D( 4, 2, 0 ) );
		box.setDrawPaint( Paints.toString( Color.WHITE ) );
		box.setDrawWidth( "1" );

		// when
		Bounds bounds = box.getSelectBounds();

		assertThat( bounds.getMinX() ).isCloseTo( 1.5, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMinY() ).isCloseTo( 0.5, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMaxX() ).isCloseTo( 6.5, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMaxY() ).isCloseTo( 3.5, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getWidth() ).isCloseTo( 5, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getHeight() ).isCloseTo( 3, TOLERANCE_PERCENT_LOOSE );
	}

	@Test
	void getVisualBoundsWithRotate() {
		// given
		DesignBox box = new DesignBox( new Point3D( 2, 1, 0 ), new Point3D( 4, 2, 0 ) );
		box.setRotate( 45 );

		double a = 0.025 * CadMath.SQRT2;
		double b = 0.05 * CadMath.SQRT2;

		// when
		Bounds bounds = box.getSelectBounds();

		assertThat( bounds.getMinX() ).isCloseTo( 2 - (2 * CadMath.SQRT2_OVER_2) - a, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMaxX() ).isCloseTo( 2 + (4 * CadMath.SQRT2_OVER_2) + a, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMinY() ).isCloseTo( 1 - a, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getMaxY() ).isCloseTo( 1 + (6 * CadMath.SQRT2_OVER_2) + a, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getWidth() ).isCloseTo( 2 * CadMath.SQRT2_OVER_2 + 4 * CadMath.SQRT2_OVER_2 + b, TOLERANCE_PERCENT_LOOSE );
		assertThat( bounds.getHeight() ).isCloseTo( 2 * CadMath.SQRT2_OVER_2 + 4 * CadMath.SQRT2_OVER_2 + b, TOLERANCE_PERCENT_LOOSE );
	}

	@Test
	void getReferencePoints() {
		// given
		DesignBox box = new DesignBox( new Point3D( 1, 1, 0 ), new Point3D( 2, 1, 0 ) );
		box.setRotate( 45 );
		assertThat( box.getSize() ).isEqualTo( new Point3D( 2, 1, 0 ) );

		double a = 2 * CadMath.SQRT2_OVER_2;
		double b = 1 * CadMath.SQRT2_OVER_2;

		// when
		List<Point3D> points = box.getReferencePoints();

		// then
		Point3DAssert.assertThat( points.getFirst() ).isCloseTo( new Point3D( 1, 1, 0 ) );
		Point3DAssert.assertThat( points.get( 1 ) ).isCloseTo( new Point3D( 1 + a, 1 + a, 0 ) );
		Point3DAssert.assertThat( points.get( 2 ) ).isCloseTo( new Point3D( 1 + a - b, 1 + a + b, 0 ) );
		Point3DAssert.assertThat( points.get( 3 ) ).isCloseTo( new Point3D( 1 - b, 1 + b, 0 ) );
		assertThat( points ).hasSize( 4 );
	}

}
