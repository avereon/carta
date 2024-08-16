package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.CommandTask;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CameraViewPreviousTest extends CommandBaseTest {

	private final Command command = new CameraViewPrevious();

	@Test
	void execute() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		verify( tool, times( 1 ) ).setView( any() );
		assertThat( result ).isEqualTo( Command.Result.SUCCESS );
	}

}
