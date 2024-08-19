package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.CommandTask;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LayerHideTest extends CommandBaseTest {

	private final LayerHide command = new LayerHide();

	// Script Tests --------------------------------------------------------------

	/**
	 * Layer create with all parameters should create and add a new layer to the
	 * design. The result should be success.
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
		assertThat( result ).isEqualTo( SUCCESS );
		verify( tool, times( 1 ) ).setLayerVisible( eq( selectedLayer ), eq( false ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	// Bad Parameter Tests -------------------------------------------------------

	@Test
	void testExecuteWithBadParameterOneIsIgnored() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, BAD_PARAMETER );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( SUCCESS );
		verify( tool, times( 1 ) ).setLayerVisible( eq( selectedLayer ), eq( false ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

}
