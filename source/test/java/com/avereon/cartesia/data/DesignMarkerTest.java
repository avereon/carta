package com.avereon.cartesia.data;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;

public class DesignMarkerTest {

	@Test
	void testModify() {
		DesignMarker point = new DesignMarker( new Point3D( 0, 0, 0 ) );
		assertThat( point.isModified() ).isTrue();
		point.setModified( false );
		assertThat( point.isModified() ).isFalse();

		point.setOrigin( new Point3D( 1, 1, 0 ) );
		assertThat( point.isModified() ).isTrue();
		point.setOrigin( new Point3D( 0, 0, 0 ) );
		assertThat( point.isModified() ).isFalse();

		point.setOrigin( new Point3D( 1, 1, 0 ) );
		assertThat( point.isModified() ).isTrue();
		point.setModified( false );
		assertThat( point.isModified() ).isFalse();
	}

	@Test
	void testOrigin() {
		DesignMarker point = new DesignMarker( new Point3D( 0, 0, 0 ) );
		assertThat( point.getOrigin() ).isEqualTo( new Point3D( 0, 0, 0 ) );

		point.setOrigin( new Point3D( 1, 2, 3 ) );
		assertThat( point.getOrigin() ).isEqualTo( new Point3D( 1, 2, 3 ) );
	}

	@Test
	void testDistanceTo() {
		assertThat( new DesignMarker( new Point3D( -2, 1, 0 ) ).distanceTo( new Point3D( 2, -2, 0 ) ) ).isCloseTo( 5.0, TOLERANCE );
	}

	@Test
	void testPathLength() {
		assertThat( new DesignMarker( new Point3D( -2, 1, 0 ) ).pathLength() ).isCloseTo( 0.0, TOLERANCE );
	}

	@Test
	void testGetVisualBounds() {
		// This is the geometrically correct bounds
		//assertThat( new DesignMarker( new Point3D( -2, 1, 0 ) ).getVisualBounds() ).isEqualTo( new BoundingBox( -2.5, 0.5, 1, 1 ) );

		// But this is what is computed by JavaFX
		assertThat( new DesignMarker( new Point3D( -2, 1, 0 ) ).getVisualBounds() ).isEqualTo( new BoundingBox( -3.0, 0.0, 2, 2 ) );
	}

	@Test
	void testCirclePath() {
		DesignMarker marker = new DesignMarker( new Point3D( 0, 0, 0 ) );
		marker.setType( DesignMarker.Type.CIRCLE.name() );

		assertThat( marker.getMarkerType() ).isEqualTo( DesignMarker.Type.CIRCLE.name().toLowerCase() );

		List<DesignPath.Step> steps = marker.getElements();
		DesignPath.Step e0 = steps.getFirst();
		assertThat( e0.command() ).isEqualTo( DesignPath.Command.M );
		assertThat( e0.data() ).isEqualTo( new double[]{ 0.0, -0.5 } );
		DesignPath.Step e1 = steps.get( 1 );
		assertThat( e1.command() ).isEqualTo( DesignPath.Command.A );
		assertThat( e1.data() ).isEqualTo( new double[]{ 0.0, 0.0, 0.5, 0.5, -90, 180 } );
		DesignPath.Step e2 = steps.get( 2 );
		assertThat( e2.command() ).isEqualTo( DesignPath.Command.A );
		assertThat( e2.data() ).isEqualTo( new double[]{ 0.0, 0.0, 0.5, 0.5, 90, 180 } );
		DesignPath.Step e3 = steps.get( 3 );
		assertThat( e3.command() ).isEqualTo( DesignPath.Command.Z );
		assertThat( e3.data() ).isEqualTo( new double[]{} );
		assertThat( steps.size() ).isEqualTo( 4 );
	}

}
