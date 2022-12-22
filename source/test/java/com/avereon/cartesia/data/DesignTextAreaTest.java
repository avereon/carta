package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignTextAreaTest {

	@Test
	void testOrigin() {
		DesignTextArea textarea = new DesignTextArea( new Point3D( 0, 0, 0 ) );
		assertThat( textarea.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		textarea.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( textarea.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

}
