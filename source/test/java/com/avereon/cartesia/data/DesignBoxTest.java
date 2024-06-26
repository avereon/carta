package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadMath;
import com.avereon.zarra.color.Paints;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.TestConstants.LOOSE_TOLERANCE;
import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class DesignBoxTest {

	@Test
	void getBounds() {
		// given
		DesignBox box = new DesignBox( new Point3D( 2, 1, 0 ), new Point3D( 4, 2, 0 ) );
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
		box.setDrawWidth( "1" );

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
		box.setRotate( 45 );

		// when
		Bounds bounds = box.getBounds();

		assertThat( bounds.getMinX() ).isEqualTo( 2 - CadMath.SQRT2_OVER_2, TOLERANCE );
		assertThat( bounds.getMaxX() ).isEqualTo( 2 + CadMath.SQRT2_OVER_2, TOLERANCE );
		assertThat( bounds.getMinY() ).isEqualTo( 1, TOLERANCE );
		assertThat( bounds.getMaxY() ).isEqualTo( 1 + CadMath.SQRT2, TOLERANCE );
		assertThat( bounds.getWidth() ).isEqualTo( CadMath.SQRT2, TOLERANCE );
		assertThat( bounds.getHeight() ).isEqualTo( CadMath.SQRT2, TOLERANCE );
	}

	@Test
	void getVisualBounds() {
		// given
		DesignBox box = new DesignBox( new Point3D( 2, 1, 0 ), new Point3D( 4, 2, 0 ) );
		// The default draw width is 0.05

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

}
