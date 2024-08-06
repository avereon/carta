package com.avereon.cartesia.command.snap;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.tool.CommandTask;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SnapGridToggleTest extends CommandBaseTest {

	private final SnapGridToggle command = new SnapGridToggle();

	@Test
	void testRunTaskStepTrueToFalse() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		// Pretend the grid snap is enabled
		when( tool.isGridSnapEnabled() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setGridSnapEnabled( false );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void testRunTaskStepFalseToTrue() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		// Pretend the grid snap is not enabled
		when( tool.isGridSnapEnabled() ).thenReturn( false );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setGridSnapEnabled( true );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void executeIgnoresExtraParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "off" );
		// Pretend the grid is visible
		when( tool.isGridSnapEnabled() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setGridSnapEnabled( false );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void runTaskStepIgnoresEvent() throws Exception {
		// given
		InputEvent event = createMouseEvent( MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, false, false, false, false, false );
		CommandTask task = new CommandTask( commandContext, tool, null, event, command );
		// Pretend the grid is visible
		when( tool.isGridSnapEnabled() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setGridSnapEnabled( false );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
