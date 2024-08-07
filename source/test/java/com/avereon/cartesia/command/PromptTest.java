package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.tool.CommandTask;
import com.avereon.cartesia.tool.DesignCommandContext;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PromptTest extends CommandBaseTest {

	@Test
	void testExecuteWithNoParameter() throws Exception {
		// With no parameter it should set the prompt and return incomplete

		// given
		Prompt command = new Prompt( "Hello world:", DesignCommandContext.Input.TEXT );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandPrompt, times( 1 ) ).setPrompt( eq( "Hello world:" ) );
		assertThat( result ).isEqualTo( INCOMPLETE );
	}

	@Test
	void testExecute() throws Exception {
		// given
		Prompt command = new Prompt( "Hello world:", DesignCommandContext.Input.TEXT );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "Hi!" );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandPrompt, times( 1 ) ).clear();
		verify( tool ).setCursor( eq( null ) );
		assertThat( result ).isEqualTo(  "Hi!" );
	}

	@Test
	void testExecuteWithArrayParameter() throws Exception {
		// given
		Prompt command = new Prompt( "Hello world:", DesignCommandContext.Input.TEXT );
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, (Object)new String[]{ "one", "two", "three" } );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandPrompt, times( 1 ) ).clear();
		verify( tool ).setCursor( eq( null ) );
		assertThat( result ).isEqualTo( new String[]{ "one", "two", "three" } );
	}

}
