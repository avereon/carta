package com.avereon.cartesia.command;

import com.avereon.cartesia.BaseCommandTest;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GridToggleTest extends BaseCommandTest {

	private final GridToggle command = new GridToggle();

	@Test
	void testExecuteTrueToFalse() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		// Pretend the grid is visible
		when( tool.isGridVisible() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setGridVisible( false );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void testExecuteFalseToTrue() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		// Pretend the grid is not visible
		when( tool.isGridVisible() ).thenReturn( false );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setGridVisible( true );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void executeIgnoresExtraParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "off" );
		// Pretend the grid is visible
		when( tool.isGridVisible() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setGridVisible( false );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void executeIgnoresEvent() throws Exception {
		// given
		InputEvent event = createMouseEvent( MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, false, false, false, false, false, 48, 17 );
		CommandTask task = new CommandTask( commandContext, tool, null, event, command );
		// Pretend the grid is visible
		when( tool.isGridVisible() ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setGridVisible( false );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
