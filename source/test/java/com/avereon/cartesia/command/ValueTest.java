package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.tool.CommandTask;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.INVALID;
import static org.assertj.core.api.Assertions.assertThat;

public class ValueTest extends CommandBaseTest {

	@Test
	void testExecuteWithNothing() throws Exception {
		// given
		Value command = new Value();
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = command.execute( task );

		// then
		assertThat( result ).isEqualTo( INVALID );
	}

	@Test
	void testExecuteWithOneParameter() throws Exception {
		// given
		Value command = new Value();
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "one parameter" );

		// when
		Object result = command.execute( task );

		// then
		assertThat( result ).isEqualTo( "one parameter" );
	}

}
