package com.avereon.cartesia.tool;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.MockCartesiaMod;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.command.Value;
import com.avereon.cartesia.error.UnknownCommand;
import com.avereon.xenon.ProgramProduct;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class CommandContextTest {

	private ProgramProduct product;

	private CommandContext context;

	@BeforeEach
	public void setup() {
		this.product = new MockCartesiaMod();
		this.context = new CommandContext( product );

		assertNotNull( product );
		assertNotNull( context );

		System.err.println( "Product and context initialized!" );
	}

	@Test
	void testFullCommand() {
		String command = "ll 0,0 1,1";
		// Executing this should give a line from 0,0 to 1,1
	}

	@Test
	void testCommand() {
		TestCommand command = new TestCommand();
		assertNotNull( context );
		new CommandContext( product ).submit( null, command );
		assertThat( command.getValues().length, is( 0 ) );
	}

	@Test
	void testCommandWithOneParameter() {
		TestCommand command = new TestCommand();
		context.submit( null, command, "0" );
		assertThat( command.getValues()[ 0 ], is( "0" ) );
	}

	@Test
	void testCommandWithTwoParameters() {
		TestCommand command = new TestCommand();
		context.submit( null, command, "0", "1" );
		assertThat( command.getValues()[ 0 ], is( "0" ) );
		assertThat( command.getValues()[ 1 ], is( "1" ) );
	}

	@Test
	void testCommandThatNeedsOneValue() {
		TestCommand command = new TestCommand( 1 );
		context.submit( null, command );
		context.submit( null, new Value(), "hello" );
		assertThat( command.getValues()[ 0 ], is( "hello" ) );
	}

	@Test
	void testCommandThatNeedsTwoValues() {
		TestCommand command = new TestCommand( 2 );
		context.submit( null, command );
		context.submit( null, new Value(), "0" );
		context.submit( null, new Value(), "1" );
		assertThat( command.getValues()[ 0 ], is( "0" ) );
		assertThat( command.getValues()[ 1 ], is( "1" ) );
	}

	@Test
	void testInputMode() {
		assertThat( context.getInputMode(), is( CommandContext.Input.NONE ) );

		TestCommand command = new TestCommand( 0 );
		context.submit( null, command );
		context.submit( null, new Prompt( "", CommandContext.Input.NONE ) );
		assertThat( context.getInputMode(), is( CommandContext.Input.NONE ) );
		context.submit( null, new Prompt( "", CommandContext.Input.NUMBER ) );
		assertThat( context.getInputMode(), is( CommandContext.Input.NUMBER ) );
		context.submit( null, new Prompt( "", CommandContext.Input.POINT ) );
		assertThat( context.getInputMode(), is( CommandContext.Input.POINT ) );
		context.submit( null, new Prompt( "", CommandContext.Input.TEXT ) );
		assertThat( context.getInputMode(), is( CommandContext.Input.TEXT ) );
		context.submit( null, new Prompt( "", CommandContext.Input.NONE ) );
		assertThat( context.getInputMode(), is( CommandContext.Input.NONE ) );
	}

	@Test
	void testNumberInput() {
		TestCommand command = new TestCommand( 1 );
		context.submit( null, command );
		context.setInputMode( CommandContext.Input.NUMBER );
		context.text( "4,3,2", true );
		assertThat( command.getValues()[ 0 ], is( 4.0 ) );
	}

	@Test
	void testPointInput() {
		TestCommand command = new TestCommand( 1 );
		context.submit( null, command );
		context.setInputMode( CommandContext.Input.POINT );
		context.text( "4,3,2", true );
		assertThat( command.getValues()[ 0 ], is( new Point3D( 4, 3, 2 ) ) );
	}

	@Test
	void testRelativePointInput() {
		TestCommand command = new TestCommand( 1 );
		context.submit( null, command );
		context.setAnchor( new Point3D( 1, 1, 1 ) );
		context.setInputMode( CommandContext.Input.POINT );
		context.text( "@4,3,2", true );
		assertThat( command.getValues()[ 0 ], is( new Point3D( 5, 4, 3 ) ) );
	}

	@Test
	void testTextInput() {
		TestCommand command = new TestCommand( 1 );
		context.submit( null, command );
		context.setInputMode( CommandContext.Input.TEXT );
		context.text( "test", true );
		assertThat( command.getValues()[ 0 ], is( "test" ) );
	}

	@Test
	void testUnknownInput() {
		TestCommand command = new TestCommand( 1 );
		context.submit( null, command );
		context.setInputMode( CommandContext.Input.NONE );
		try {
			context.text( "unknown", true );
			fail();
		} catch( UnknownCommand exception ) {
			assertThat( exception.getMessage(), is( "unknown" ) );
		}
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
		public Object execute( CommandContext context, Object... parameters ) {
			if( parameters.length < needed ) return INCOMPLETE;
			this.values = parameters;
			return COMPLETE;
		}

		public Object[] getValues() {
			return values;
		}

	}

}
