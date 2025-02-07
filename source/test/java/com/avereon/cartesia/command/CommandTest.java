package com.avereon.cartesia.command;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.test.Point3DAssert;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import static com.avereon.cartesia.TestConstants.TOLERANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test shared command functionality.
 */
public class CommandTest extends BaseCommandTest {

	@Spy
	private final Command command = new Command() {};

	@Test
	void testCancel() {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		command.cancel( task );

		// then
		verify( command, times( 1 ) ).clearReferenceAndPreview( eq( task ) );
		verify( tool, times( 1 ) ).setCursor( eq( Cursor.DEFAULT ) );
		verify( tool, times( 1 ) ).clearSelectedShapes();
	}

	@Test
	void testAsPointWithInput() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Point3D result = command.asPoint( task, "point", "1.01,2.01" );

		// then
		Point3DAssert.assertThat( result ).isEqualTo( new Point3D( 1.01, 2.01, 0 ) );
	}

	@Test
	void testAsPointWithPoint() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Point3D result = command.asPoint( task, "point", new Point3D( 1.01, 2.01, 0 ) );

		// then
		Point3DAssert.assertThat( result ).isEqualTo( new Point3D( 1.01, 2.01, 0 ) );
	}

	@Test
	void testAsPointWithInputEvent() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		when( tool.screenToWorld( new Point3D( 72, 72, 0 ) ) ).thenReturn( new Point3D( 1.01, 2.01, 0 ) );

		// when
		Point3D result = command.asPoint( task, "point", createMouseEvent( MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, false, false, false, false, false, 72, 72 ) );

		// then
		Point3DAssert.assertThat( result ).isEqualTo( new Point3D( 1.01, 2.01, 0 ) );
	}

	// TODO Test more shared Command methods

	@Test
	void testDeriveStart() {
		assertThat( Command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 1, 2, 0 ) ) ).isCloseTo( 0.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 2, 2, 0 ) ) ).isCloseTo( 0.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 2, 3, 0 ) ) ).isCloseTo( 45.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 1, 3, 0 ) ) ).isCloseTo( 90.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 0, 3, 0 ) ) ).isCloseTo( 135.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 0, 2, 0 ) ) ).isCloseTo( 180.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 0, 1, 0 ) ) ).isCloseTo( -135.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 1, 1, 0 ) ) ).isCloseTo( -90.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 1, 2, 0 ), 1.0, 1.0, 0.0, new Point3D( 2, 1, 0 ) ) ).isCloseTo( -45.0, TOLERANCE );
	}

	@Test
	void testDeriveStartWithRotate() {
		assertThat( Command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 3, 2, 0 ) ) ).isCloseTo( 0.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 2, 2, 0 ) ) ).isCloseTo( 45.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 1, 2, 0 ) ) ).isCloseTo( 90.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 1, 1, 0 ) ) ).isCloseTo( 135.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 1, 0, 0 ) ) ).isCloseTo( 180.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 2, 0, 0 ) ) ).isCloseTo( -135.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 3, 0, 0 ) ) ).isCloseTo( -90.0, TOLERANCE );
		assertThat( Command.deriveStart( new Point3D( 2, 1, 0 ), 1.0, 1.0, 45.0, new Point3D( 3, 1, 0 ) ) ).isCloseTo( -45.0, TOLERANCE );
	}

	@Test
	void testDeriveExtent() {
		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 2, 3, 0 ), 1.0 ) ).isCloseTo( 0.0, TOLERANCE );
		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 2, 3, 0 ), -1.0 ) ).isCloseTo( 0.0, TOLERANCE );

		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 1, 3, 0 ), 1.0 ) ).isCloseTo( 45.0, TOLERANCE );
		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 1, 3, 0 ), -1.0 ) ).isCloseTo( -315.0, TOLERANCE );

		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 0, 3, 0 ), 1.0 ) ).isCloseTo( 90.0, TOLERANCE );
		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 0, 3, 0 ), -1.0 ) ).isCloseTo( -270.0, TOLERANCE );

		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 0, 2, 0 ), 1.0 ) ).isCloseTo( 135.0, TOLERANCE );
		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 0, 2, 0 ), -1.0 ) ).isCloseTo( -225.0, TOLERANCE );

		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 0, 1, 0 ), 1.0 ) ).isCloseTo( 180.0, TOLERANCE );
		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 0, 1, 0 ), -1.0 ) ).isCloseTo( -180.0, TOLERANCE );

		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 1, 1, 0 ), 1.0 ) ).isCloseTo( 225.0, TOLERANCE );
		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 1, 1, 0 ), -1.0 ) ).isCloseTo( -135.0, TOLERANCE );

		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 2, 1, 0 ), 1.0 ) ).isCloseTo( 270.0, TOLERANCE );
		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 2, 1, 0 ), -1.0 ) ).isCloseTo( -90.0, TOLERANCE );

		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 2, 2, 0 ), 1.0 ) ).isCloseTo( 315.0, TOLERANCE );
		assertThat( Command.deriveExtent( new Point3D( 1, 2, 0 ), 1.0, 1.0, 45.0, 0.0, new Point3D( 2, 2, 0 ), -1.0 ) ).isCloseTo( -45.0, TOLERANCE );
	}

	@Test
	void testGetExtentSpin() {
		// Initial
		assertThat( Command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, 0, 0 ), new Point3D( 1, 1, 0 ), 0.0 ) ).isCloseTo( 1.0, TOLERANCE );
		assertThat( Command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, 0, 0 ), new Point3D( 1, -1, 0 ), 0.0 ) ).isCloseTo( -1.0, TOLERANCE );

		// Maintain CCW
		assertThat( Command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, 1, 0 ), new Point3D( 1, 1.5, 0 ), 1.0 ) ).isCloseTo( 1.0, TOLERANCE );
		assertThat( Command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, 1, 0 ), new Point3D( 1, 0.5, 0 ), 1.0 ) ).isCloseTo( 1.0, TOLERANCE );

		// Maintain CW
		assertThat( Command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, -1, 0 ), new Point3D( 1, -0.5, 0 ), -1.0 ) ).isCloseTo( -1.0, TOLERANCE );
		assertThat( Command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, -1, 0 ), new Point3D( 1, -1.5, 0 ), -1.0 ) ).isCloseTo( -1.0, TOLERANCE );

		// Switch
		assertThat( Command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, 0.5, 0 ), new Point3D( 1, -0.5, 0 ), 1.0 ) ).isCloseTo( -1.0, TOLERANCE );
		assertThat( Command.getExtentSpin( new Point3D( 0, 0, 0 ), 1.0, 1.0, 0.0, 0.0, new Point3D( 1, -0.5, 0 ), new Point3D( 1, 0.5, 0 ), -1.0 ) ).isCloseTo( 1.0, TOLERANCE );
	}

	@Test
	void testGetExtentSpinWithRotatedArc() {
		// These tests use the point where the crossover line goes through (4.0,5.0)

		// Initial
		assertThat( Command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4, 5, 0 ), new Point3D( 4, 3, 0 ), 0.0 ) ).isCloseTo( 1.0, TOLERANCE );
		assertThat( Command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4, 5, 0 ), new Point3D( 5, 5, 0 ), 0.0 ) ).isCloseTo( -1.0, TOLERANCE );

		// Maintain CCW
		assertThat( Command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4, 4.9, 0 ), new Point3D( 4, 4.5, 0 ), 1.0 ) ).isCloseTo( 1.0, TOLERANCE );
		assertThat( Command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4.5, 5, 0 ), new Point3D( 4.1, 5, 0 ), 1.0 ) ).isCloseTo( 1.0, TOLERANCE );

		// Maintain CW
		assertThat( Command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4, 4.5, 0 ), new Point3D( 4, 4.9, 0 ), -1.0 ) ).isCloseTo( -1.0, TOLERANCE );
		assertThat( Command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4.1, 5, 0 ), new Point3D( 4.5, 5, 0 ), -1.0 ) ).isCloseTo( -1.0, TOLERANCE );

		// Switch
		assertThat( Command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4, 4.9, 0 ), new Point3D( 4, 5.1, 0 ), 1.0 ) ).isCloseTo( -1.0, TOLERANCE );
		assertThat( Command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4, 5.1, 0 ), new Point3D( 4, 4.9, 0 ), -1.0 ) ).isCloseTo( 1.0, TOLERANCE );
		assertThat( Command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 3.9, 5, 0 ), new Point3D( 4.1, 5, 0 ), 1.0 ) ).isCloseTo( -1.0, TOLERANCE );
		assertThat( Command.getExtentSpin( new Point3D( 5, 3, 0 ), 1.0, 0.5, 90.0, 45.0, new Point3D( 4.1, 5, 0 ), new Point3D( 3.9, 5, 0 ), 1.0 ) ).isCloseTo( 1.0, TOLERANCE );
	}

}
