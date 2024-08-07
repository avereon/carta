package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.tool.CommandTask;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CameraZoomWindowTest extends CommandBaseTest {

	private final Command command = new CameraZoomWindow();

	/**
	 * Camera zoom with no parameters or event, should prompt the
	 * user to select an anchor point. The result should be incomplete.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testRunTaskStepNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		when( tool.getAsset() ).thenReturn( asset );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( null );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	// NEXT Continue implementing tests

}
