package com.avereon.cartesia.tool;

import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CoordinateSystemIsometricTest {

	@Test
	void testGetGridDots() throws Exception {
		Workplane workplane = new Workplane( -10, -10, 10, 10, "1", "1", "1", "1", "1", "1" );
		List<Shape> dots = CoordinateSystem.ISO.getGridDots( workplane );
		assertThat( dots.size(), is( 0 ) );
	}

}
