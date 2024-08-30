package com.avereon.cartesia.command.print;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.command.CommandTask;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;

public class PrintUpdateTest  extends BaseCommandTest {

	private final PrintUpdate command = new PrintUpdate();

	@Test
	void testRunTaskStepNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		//verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		//verify( tool, times( 1 ) ).setCursor( any() );
		assertThat( result ).isEqualTo( SUCCESS );
	}

	@Test
	void testRunTaskStepWithOneParameter() throws Exception {
		// given
		String printName = "Custom Print";
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, printName );

		// when
		Object result = task.runTaskStep();

		// then
		// Check the created view
		//assertThat( result ).isInstanceOf( DesignPrint.class );
		//assertThat( ((DesignPrint)result).getName() ).isEqualTo( printName );
		//verify( design, times( 1 ) ).addPrint( any() );
		//verify( tool, times( 0 ) ).setCurrentPrint( any() );
		assertThat( result ).isEqualTo( SUCCESS );
	}

}
