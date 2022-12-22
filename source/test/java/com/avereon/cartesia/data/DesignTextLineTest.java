package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignTextLineTest {

	@Test
	void testOrigin() {
		DesignTextLine textline = new DesignTextLine( new Point3D( 0, 0, 0 ) );
		assertThat( textline.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		textline.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( textline.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

}
