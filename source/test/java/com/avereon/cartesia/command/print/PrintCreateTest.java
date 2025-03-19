package com.avereon.cartesia.command.print;

import com.avereon.cartesia.BaseCommandTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.command.base.Prompt;
import com.avereon.cartesia.data.DesignPrint;
import org.junit.jupiter.api.Test;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PrintCreateTest extends BaseCommandTest {

	private final PrintCreate command = new PrintCreate();

	@Test
	void testRunTaskStepNoParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command );

		// when
		Object result = task.runTaskStep();

		// then
		verify( commandContext, times( 1 ) ).submit( eq( tool ), any( Prompt.class ) );
		verify( tool, times( 1 ) ).setCursor( any() );
		assertThat( result ).isEqualTo( INCOMPLETE );
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
		assertThat( result ).isInstanceOf( DesignPrint.class );
		assertThat( ((DesignPrint)result).getName() ).isEqualTo( printName );
		verify( design, times( 1 ) ).addPrint( any() );
		//verify( tool, times( 0 ) ).setCurrentPrint( any() );
	}

	// Bad Parameter Tests -------------------------------------------------------

	@Test
	void testRunTaskStepWithBadParameters() {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, new Object[] {null} );

		// when
		InvalidInputException exception = catchThrowableOfType( InvalidInputException.class, task::runTaskStep );

		// then
		assertThat( exception.getInputRbKey() ).isEqualTo( "print-name" );
		verify( design, times( 0 ) ).addPrint( any() );
		//verify( tool, times( 0 ) ).setCurrentPrint( any() );
	}

	@Test
	void testExecuteWithBadParameterTwoIsIgnored() throws Exception {
		// given
		String printName = "Custom Print";
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, printName, null );

		// when
		Object result = task.runTaskStep();

		// then
		assertThat( result ).isInstanceOf( DesignPrint.class );
		assertThat( ((DesignPrint)result).getName() ).isEqualTo( printName );
		verify( design, times( 1 ) ).addPrint( any() );
		//verify( tool, times( 0 ) ).setCurrentPrint( any() );
	}

}
