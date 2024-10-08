package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.xenon.undo.NodeChange;
import org.fxmisc.undo.UndoManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UndoTest extends BaseCommandTest {

	@Mock
	private UndoManager<List<NodeChange>> undoManager;

	private final Undo command = new Undo();

	// Script Tests --------------------------------------------------------------

	/**
	 * Undo should undo the last edit. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParametersWithVisibleTrue() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		when( asset.getUndoManager() ).thenReturn( undoManager );

		// when
		Object result = task.runTaskStep();

		// then
		verify( undoManager, times( 1 ) ).undo();
		assertThat( result ).isEqualTo( SUCCESS );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	// Bad Parameter Tests -------------------------------------------------------

	@Test
	void testExecuteWithBadParameterOneIsIgnored() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, BAD_TEXT_PARAMETER );
		when( asset.getUndoManager() ).thenReturn( undoManager );

		// when
		Object result = task.runTaskStep();

		// then
		verify( undoManager, times( 1 ) ).undo();
		assertThat( result ).isEqualTo( SUCCESS );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

}
