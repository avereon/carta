package com.avereon.cartesia.tool;

import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GridIsometricTest {

	@Test
	void testGetGridDots() throws Exception {
		Workplane workplane = new Workplane( -10, -10, 10, 10, "1", "1", "1", "1", "1", "1" );
		List<Shape> dots = Grid.ISO.getGridDots( workplane );
		assertThat( dots.size()).isEqualTo( 0 );
	}

}
