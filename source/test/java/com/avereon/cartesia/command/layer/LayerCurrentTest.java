package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.command.CommandTask;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LayerCurrentTest extends BaseCommandTest {

	private final LayerCurrent command = new LayerCurrent();

	// Script Tests --------------------------------------------------------------

	/**
	 * Layer current with all parameters should make the selected layer the
	 * current. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setCurrentLayer( eq( selectedLayer ) );
		assertThat( result ).isEqualTo( selectedLayer );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	// Bad Parameter Tests -------------------------------------------------------

	@Test
	void testExecuteWithBadParameterOneIsIgnored() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, BAD_TEXT_PARAMETER );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setCurrentLayer( eq( selectedLayer ) );
		assertThat( result ).isEqualTo( selectedLayer );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

}
