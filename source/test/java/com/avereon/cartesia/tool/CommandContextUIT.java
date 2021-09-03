package com.avereon.cartesia.tool;

import com.avereon.cartesia.BaseCartesiaUIT;
import com.avereon.cartesia.CommandMap;
import com.avereon.cartesia.Design2dAssetType;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.command.Value;
import com.avereon.cartesia.data.Design2D;
import com.avereon.cartesia.error.UnknownCommand;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetType;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class CommandContextUIT extends BaseCartesiaUIT {

	private CommandContext context;

	private DesignTool tool;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		this.context = new CommandContext( getMod() );

		AssetType assetType = getProgram().getAssetManager().getAssetType( Design2dAssetType.class.getName() );
		Asset asset = getProgram().getAssetManager().createAsset( assetType );
		asset.setModel( new Design2D() );
		//this.tool = new Design2dEditor( getMod(), asset );

		assertTrue( getProgram().getTaskManager().isRunning() );
		Future<ProgramTool> future = getProgram().getAssetManager().openAsset( asset );
		this.tool = (DesignTool)future.get();

		context.setLastActiveDesignTool( tool );
	}

	@Test
	void testInputMode() throws Exception {
		assertThat( context.getInputMode(), is( CommandContext.Input.NONE ) );

		MockCommand command = new MockCommand( 0 );
		context.submit( tool, command ).waitFor( TIMEOUT );

		context.submit( tool, new Prompt( "", CommandContext.Input.NONE ) ).waitFor( TIMEOUT );
		assertThat( context.getInputMode(), is( CommandContext.Input.NONE ) );
		context.submit( tool, new Prompt( "", CommandContext.Input.NUMBER ) ).waitFor( TIMEOUT );
		assertThat( context.getInputMode(), is( CommandContext.Input.NUMBER ) );
		context.submit( tool, new Prompt( "", CommandContext.Input.POINT ) ).waitFor( TIMEOUT );
		assertThat( context.getInputMode(), is( CommandContext.Input.POINT ) );
		context.submit( tool, new Prompt( "", CommandContext.Input.TEXT ) ).waitFor( TIMEOUT );
		assertThat( context.getInputMode(), is( CommandContext.Input.TEXT ) );
		context.submit( tool, new Prompt( "", CommandContext.Input.NONE ) ).waitFor( TIMEOUT );
		assertThat( context.getInputMode(), is( CommandContext.Input.NONE ) );
	}

	@Test
	void testFullCommand() {
		String command = "ll 0,0 1,1";
		// Executing this should give a line from 0,0 to 1,1
	}

	@Test
	void testCommand() throws Exception {
		MockCommand command = new MockCommand();
		context.submit( tool, command );
		command.waitFor();
		assertThat( command.getValues().length, is( 0 ) );
	}

	@Test
	void testCommandWithOneParameter() throws Exception {
		MockCommand command = new MockCommand();
		context.submit( tool, command, "0" );
		command.waitFor();
		assertThat( command.getValues()[ 0 ], is( "0" ) );
	}

	@Test
	void testCommandWithTwoParameters() throws Exception {
		MockCommand command = new MockCommand();
		context.submit( tool, command, "0", "1" );
		command.waitFor();
		assertThat( command.getValues()[ 0 ], is( "0" ) );
		assertThat( command.getValues()[ 1 ], is( "1" ) );
	}

	@Test
	void testCommandThatNeedsOneValue() throws Exception {
		MockCommand command = new MockCommand( 1 );
		context.submit( tool, command );
		command.waitFor();
		context.submit( tool, new Value(), "hello" );
		command.waitFor();
		assertThat( command.getValues()[ 0 ], is( "hello" ) );
	}

	@Test
	void testCommandThatNeedsTwoValues() throws Exception {
		MockCommand command = new MockCommand( 2 );
		context.submit( tool, command );
		command.waitFor();
		context.submit( tool, new Value(), "0" );
		command.waitFor();
		context.submit( tool, new Value(), "1" );
		command.waitFor();
		assertThat( command.getValues()[ 0 ], is( "0" ) );
		assertThat( command.getValues()[ 1 ], is( "1" ) );
	}

	@Test
	void testNumberInput() throws Exception {
		MockCommand command = new MockCommand( 1 );
		context.submit( tool, command );
		command.waitFor();
		context.setInputMode( CommandContext.Input.NUMBER );
		context.processText( "4,3,2", true );
		command.waitFor();
		assertThat( command.getValues()[ 0 ], is( 4.0 ) );
	}

	@Test
	void testPointInput() throws Exception {
		MockCommand command = new MockCommand( 1 );
		context.submit( tool, command );
		command.waitFor();
		context.setInputMode( CommandContext.Input.POINT );
		context.processText( "4,3,2", true );
		command.waitFor();
		assertThat( command.getValues()[ 0 ], is( new Point3D( 4, 3, 2 ) ) );
	}

	@Test
	void testRelativePointInput() throws Exception {
		MockCommand command = new MockCommand( 1 );
		context.submit( tool, command );
		command.waitFor();
		context.setAnchor( new Point3D( 1, 1, 1 ) );
		context.setInputMode( CommandContext.Input.POINT );
		context.processText( "@4,3,2", true );
		command.waitFor();
		assertThat( command.getValues()[ 0 ], is( new Point3D( 5, 4, 3 ) ) );
	}

	@Test
	void testTextInput() throws Exception {
		MockCommand command = new MockCommand( 1 );
		context.submit( tool, command );
		command.waitFor();
		context.setInputMode( CommandContext.Input.TEXT );
		context.processText( "test", true );
		command.waitFor();
		assertThat( command.getValues()[ 0 ], is( "test" ) );
	}

	@Test
	void testUnknownInput() throws Exception {
		MockCommand command = new MockCommand( 1 );
		context.submit( tool, command );
		command.waitFor();
		context.setInputMode( CommandContext.Input.NONE );
		try {
			context.processText( "unknown", true );
			fail();
		} catch( UnknownCommand exception ) {
			assertThat( exception.getMessage(), is( "unknown" ) );
		}
	}

	@Test
	void testAutoCommand() {
		CommandMap.add( "test", MockCommand.class, "Test Command", "test", null );
		Command command = context.processText( "test", false );
		assertThat( command, instanceOf( MockCommand.class ) );
	}

	@Test
	void testNoAutoCommandWithTextInput() {
		context.setInputMode( CommandContext.Input.TEXT );
		CommandMap.add( "test", MockCommand.class, "Test Command", "test", null );
		Command command = context.processText( "test", false );
		assertNull( command );
	}

}
