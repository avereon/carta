package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.tool.CommandTask;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.INVALID;
import static org.assertj.core.api.Assertions.assertThat;

public class ValueTest extends CommandBaseTest {

	private final Value command = new Value();

	@Test
	void executeWithNothing() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( INVALID );
	}

	@Test
	void executeWithOneParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "one parameter" );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isEqualTo( "one parameter" );
	}

}
