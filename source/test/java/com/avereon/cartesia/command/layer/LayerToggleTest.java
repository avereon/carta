package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.command.CommandTask;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class LayerToggleTest  extends BaseCommandTest {

	private final LayerToggle command = new LayerToggle();

	// Script Tests --------------------------------------------------------------

	/**
	 * Layer toggle with all parameters should toggle the visibility of the
	 * selected layer. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParametersWithVisibleTrue() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		when( tool.isLayerVisible( selectedLayer ) ).thenReturn( true );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( SUCCESS );
		verify( tool, times( 1 ) ).setLayerVisible( eq( selectedLayer ), eq( false ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	/**
	 * Layer toggle with all parameters should toggle the visibility of the
	 * selected layer. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParametersWithVisibleFalse() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		when( tool.isLayerVisible( selectedLayer ) ).thenReturn( false );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( SUCCESS );
		verify( tool, times( 1 ) ).setLayerVisible( eq( selectedLayer ), eq( true ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	// Bad Parameter Tests -------------------------------------------------------

	@Test
	void testExecuteWithBadParameterOneIsIgnored() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, BAD_TEXT_PARAMETER );
		when( tool.isLayerVisible( selectedLayer ) ).thenReturn( false );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( SUCCESS );
		verify( tool, times( 1 ) ).setLayerVisible( eq( selectedLayer ), eq( true ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

}

