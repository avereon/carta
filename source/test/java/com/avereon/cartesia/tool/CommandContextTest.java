package com.avereon.cartesia.tool;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.MockCartesiaMod;
import com.avereon.cartesia.NumericTest;
import com.avereon.cartesia.command.ValueCommand;
import com.avereon.xenon.ProgramProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CommandContextTest implements NumericTest {

	private ProgramProduct product;

	private CommandContext processor;

	@BeforeEach
	public void setup() throws Exception {
		product = new MockCartesiaMod();
		processor = new CommandContext( product );
	}

	@Test
	void testFullCommand() {
		String command = "ll 0,0 1,1";
		// Executing this should give a line from 0,0 to 1,1
	}

	@Test
	void testCommand() {
		TestCommand command = new TestCommand();
		processor.submit( null, command );
		assertThat( command.getValues().length, is( 0 ) );
	}

	@Test
	void testCommandWithOneParameter() {
		TestCommand command = new TestCommand();
		processor.submit( null, command, "0" );
		assertThat( command.getValues()[ 0 ], is( "0" ) );
	}

	@Test
	void testCommandWithTwoParameters() {
		TestCommand command = new TestCommand();
		processor.submit( null, command, "0", "1" );
		assertThat( command.getValues()[ 0 ], is( "0" ) );
		assertThat( command.getValues()[ 1 ], is( "1" ) );
	}

	@Test
	void testCommandThatNeedsOneValue() {
		TestCommand command = new TestCommand( 1 );
		processor.submit( null, command );
		processor.submit( null, new ValueCommand(), "hello" );
		assertThat( command.getValues()[ 0 ], is( "hello" ) );
	}

	@Test
	void testCommandThatNeedsTwoValues() {
		TestCommand command = new TestCommand( 2 );
		processor.submit( null, command );
		processor.submit( null, new ValueCommand(), "0" );
		processor.submit( null, new ValueCommand(), "1" );
		assertThat( command.getValues()[ 0 ], is( "0" ) );
		assertThat( command.getValues()[ 1 ], is( "1" ) );
	}

	private static class TestCommand extends Command {

		private final int needed;

		private Object[] values;

		public TestCommand() {
			this( 0 );
		}

		public TestCommand( int needed ) {
			this.needed = needed;
		}

		@Override
		public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
			if( parameters.length < needed ) return incomplete();
			this.values = parameters;
			return setComplete();
		}

		public Object[] getValues() {
			return values;
		}

	}

}
