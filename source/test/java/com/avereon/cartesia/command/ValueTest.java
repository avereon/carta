package com.avereon.cartesia.command;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.command.base.Prompt;
import com.avereon.cartesia.command.base.Value;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ValueTest extends BaseCommandTest {

	private final Value command = new Value();

	@Test
	void executeWithNothing() {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		InvalidInputException exception = catchThrowableOfType( InvalidInputException.class, task::runTaskStep );

		// then
		verify( commandContext, times( 0 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( currentLayer, times( 0 ) ).addShape( any() );
		assertThat( exception.getInputRbKey() ).isEqualTo( "value" );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	@Test
	void executeWithOneParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "one" );

		// when
		Object result = task.runTaskStep();
		// The result should be the parameter array

		// then
		assertThat( result ).isEqualTo( new Object[]{ "one" } );
	}

	@Test
	void executeWithMultipleParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "one", "two", "three" );

		// when
		Object result = task.runTaskStep();
		// The result should be the parameter array

		// then
		assertThat( result ).isEqualTo( new Object[]{ "one", "two", "three" } );
	}

	@Test
	void executeWithArrayParameter() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, (Object)new String[]{ "one", "two", "three" } );

		// when
		Object result = task.runTaskStep();
		// The result should be the parameter array

		// then
		assertThat( result ).isEqualTo( new Object[]{ new String[]{ "one", "two", "three" } } );
	}

}
