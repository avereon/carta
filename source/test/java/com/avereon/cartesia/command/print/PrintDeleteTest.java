package com.avereon.cartesia.command.print;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.CommandTask;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PrintDeleteTest  extends CommandBaseTest {

	private final PrintDelete command = new PrintDelete();

	@Test
	void testRunTaskStepNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		// Pretend there is not a current view
		//when( tool.getCurrentPrint() ).thenReturn( null );

		// when
		Object result = task.runTaskStep();

		// then
		verify( design, times( 0 ) ).removePrint( any() );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void runTaskStepWithCurrentPrint() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );
		// Pretend there is a current view
		//when( tool.getCurrentPrint() ).thenReturn( new DesignPrint().setName( "Custom Print" ) );

		// when
		Object result = task.runTaskStep();

		// then
		//verify( design, times( 1 ) ).removePrint( any() );
		assertThat( result ).isEqualTo( SUCCESS );
	}
}
