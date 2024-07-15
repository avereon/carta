package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadMath;
import com.avereon.zarra.color.Paints;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.TestConstants.LOOSE_TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class DesignBoxTest {

	@Test
	void getBounds() {
		// given
		DesignBox box = new DesignBox( new Point3D( 2, 1, 0 ), new Point3D( 4, 2, 0 ) );
		// The default draw width is 0.05

		// when
		Bounds bounds = box.getBounds();

		assertThat( bounds.getMinX() ).isEqualTo( 1.975, LOOSE_TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( 0.975, LOOSE_TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( 4.05, LOOSE_TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( 2.05, LOOSE_TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( 6.025, LOOSE_TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( 3.025, LOOSE_TOLERANCE );
	}

	@Test
	void getBoundsWithStroke() {
		// given
		DesignBox box = new DesignBox( new Point3D( 2, 1, 0 ), new Point3D( 4, 2, 0 ) );
		box.setDrawWidth( "1" );

		// when
		Bounds bounds = box.getBounds();

		assertThat( bounds.getMinX() ).isEqualTo( 1.5 );
		assertThat( bounds.getMinY() ).isEqualTo( 0.5 );
		assertThat( bounds.getWidth() ).isEqualTo( 5 );
		assertThat( bounds.getHeight() ).isEqualTo( 3 );
		assertThat( bounds.getMaxX() ).isEqualTo( 6.5 );
		assertThat( bounds.getMaxY() ).isEqualTo( 3.5 );
	}

	@Test
	void getBoundsWithRotate() {
		// given
		DesignBox box = new DesignBox( 2, 1, 1, 1 );
		box.setDrawWidth( "0" );
		box.setRotate( 45 );

		// when
		Bounds bounds = box.getBounds();

		assertThat( bounds.getMinX() ).isEqualTo( 2 - CadMath.SQRT2_OVER_2, LOOSE_TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( 2 + CadMath.SQRT2_OVER_2, LOOSE_TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( 1, LOOSE_TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( 1 + CadMath.SQRT2, LOOSE_TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( CadMath.SQRT2, LOOSE_TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( CadMath.SQRT2, LOOSE_TOLERANCE );
	}

	@Test
	void getVisualBounds() {
		// given
		DesignBox box = new DesignBox( new Point3D( 2, 1, 0 ), new Point3D( 4, 2, 0 ) );

		// when
		Bounds bounds = box.getVisualBounds();

		assertThat( bounds.getMinX() ).isEqualTo( 1.975, LOOSE_TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( 0.975, LOOSE_TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( 6.025, LOOSE_TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( 3.025, LOOSE_TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( 4.05, LOOSE_TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( 2.05, LOOSE_TOLERANCE );
	}

	@Test
	void getVisualBoundsWithStroke() {
		// given
		DesignBox box = new DesignBox( new Point3D( 2, 1, 0 ), new Point3D( 4, 2, 0 ) );
		box.setDrawPaint( Paints.toString( Color.WHITE ) );
		box.setDrawWidth( "1" );

		// when
		Bounds bounds = box.getVisualBounds();

		assertThat( bounds.getMinX() ).isEqualTo( 1.5, LOOSE_TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( 0.5, LOOSE_TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( 6.5, LOOSE_TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( 3.5, LOOSE_TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( 5, LOOSE_TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( 3, LOOSE_TOLERANCE );
	}

	@Test
	void getVisualBoundsWithRotate() {
		// given
		DesignBox box = new DesignBox( new Point3D( 2, 1, 0 ), new Point3D( 4, 2, 0 ) );
		box.setRotate( 45 );

		double a = 0.025 * CadMath.SQRT2;
		double b = 0.05 * CadMath.SQRT2;

		// when
		Bounds bounds = box.getVisualBounds();

		assertThat( bounds.getMinX() ).isEqualTo( 2 - (2 * CadMath.SQRT2_OVER_2) - a, LOOSE_TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( 2 + (4 * CadMath.SQRT2_OVER_2) + a, LOOSE_TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( 1 - a, LOOSE_TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( 1 + (6 * CadMath.SQRT2_OVER_2) + a, LOOSE_TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( 2 * CadMath.SQRT2_OVER_2 + 4 * CadMath.SQRT2_OVER_2 + b, LOOSE_TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( 2 * CadMath.SQRT2_OVER_2 + 4 * CadMath.SQRT2_OVER_2 + b, LOOSE_TOLERANCE );
	}

}
