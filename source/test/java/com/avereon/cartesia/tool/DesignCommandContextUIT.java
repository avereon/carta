package com.avereon.cartesia.tool;

import com.avereon.cartesia.BaseCartesiaUiTest;
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

import static com.avereon.xenon.test.ProgramTestConfig.LONG_TIMEOUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class DesignCommandContextUIT extends BaseCartesiaUiTest {

	private DesignCommandContext context;

	private BaseDesignTool tool;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		this.context = new DesignCommandContext( getMod() );

		AssetType assetType = getProgram().getAssetManager().getAssetType( Design2dAssetType.class.getName() );
		Asset asset = getProgram().getAssetManager().createAsset( assetType );
		asset.setModel( new Design2D() );
		//this.tool = new Design2dEditor( getMod(), asset );

		assertThat( getProgram().getTaskManager().isRunning() ).isTrue();
		Future<ProgramTool> future = getProgram().getAssetManager().openAsset( asset );
		this.tool = (BaseDesignTool)future.get();

		context.setLastActiveDesignTool( tool );
	}

	@Test
	void testInputMode() throws Exception {
		assertThat( context.getInputMode() ).isEqualTo( DesignCommandContext.Input.NONE );

		MockCommand command = new MockCommand( 0 );
		context.submit( tool, command ).waitFor( LONG_TIMEOUT );

		context.submit( tool, new Prompt( "", DesignCommandContext.Input.NONE ) ).waitFor( LONG_TIMEOUT );
		assertThat( context.getInputMode() ).isEqualTo( DesignCommandContext.Input.NONE );
		context.submit( tool, new Prompt( "", DesignCommandContext.Input.NUMBER ) ).waitFor( LONG_TIMEOUT );
		assertThat( context.getInputMode() ).isEqualTo( DesignCommandContext.Input.NUMBER );
		context.submit( tool, new Prompt( "", DesignCommandContext.Input.POINT ) ).waitFor( LONG_TIMEOUT );
		assertThat( context.getInputMode() ).isEqualTo( DesignCommandContext.Input.POINT );
		context.submit( tool, new Prompt( "", DesignCommandContext.Input.TEXT ) ).waitFor( LONG_TIMEOUT );
		assertThat( context.getInputMode() ).isEqualTo( DesignCommandContext.Input.TEXT );
		context.submit( tool, new Prompt( "", DesignCommandContext.Input.NONE ) ).waitFor( LONG_TIMEOUT );
		assertThat( context.getInputMode() ).isEqualTo( DesignCommandContext.Input.NONE );
	}

	@Test
	void testFullCommand() {
		String command = "ll 0,0 1,1";
		// Executing this should give a line from 0,0 to 1,1

		// TODO Verify there is a line from 0,0 to 1,1
	}

	@Test
	void testCommand() throws Exception {
		MockCommand command = new MockCommand();
		context.submit( tool, command );
		command.waitFor();
		assertThat( command.getValues().length ).isEqualTo( 0 );
	}

	@Test
	void testCommandWithOneParameter() throws Exception {
		MockCommand command = new MockCommand();
		context.submit( tool, command, "0" );
		command.waitFor();
		assertThat( command.getValues()[ 0 ] ).isEqualTo( "0" );
	}

	@Test
	void testCommandWithTwoParameters() throws Exception {
		MockCommand command = new MockCommand();
		context.submit( tool, command, "0", "1" );
		command.waitFor();
		assertThat( command.getValues()[ 0 ] ).isEqualTo( "0" );
		assertThat( command.getValues()[ 1 ] ).isEqualTo( "1" );
	}

	@Test
	void testCommandThatNeedsOneValue() throws Exception {
		MockCommand command = new MockCommand( 1 );
		context.submit( tool, command );
		command.waitFor();
		context.submit( tool, new Value(), "hello" );
		command.waitFor();
		assertThat( command.getValues()[ 0 ] ).isEqualTo( "hello" );
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
		assertThat( command.getValues()[ 0 ] ).isEqualTo( "0" );
		assertThat( command.getValues()[ 1 ] ).isEqualTo( "1" );
	}

	@Test
	void testNumberInput() throws Exception {
		MockCommand command = new MockCommand( 1 );
		context.submit( tool, command );
		command.waitFor();
		context.setInputMode( DesignCommandContext.Input.NUMBER );
		context.processText( "4,3,2", true );
		command.waitFor();
		assertThat( command.getValues()[ 0 ] ).isEqualTo( 4.0 );
	}

	@Test
	void testPointInput() throws Exception {
		MockCommand command = new MockCommand( 1 );
		context.submit( tool, command );
		command.waitFor();
		context.setInputMode( DesignCommandContext.Input.POINT );
		context.processText( "4,3,2", true );
		command.waitFor();
		assertThat( command.getValues()[ 0 ] ).isEqualTo( new Point3D( 4, 3, 2 ) );
	}

	@Test
	void testRelativePointInput() throws Exception {
		MockCommand command = new MockCommand( 1 );
		context.submit( tool, command );
		command.waitFor();
		context.setWorldAnchor( new Point3D( 1, 1, 1 ) );
		context.setInputMode( DesignCommandContext.Input.POINT );
		context.processText( "@4,3,2", true );
		command.waitFor();
		assertThat( command.getValues()[ 0 ] ).isEqualTo( new Point3D( 5, 4, 3 ) );
	}

	@Test
	void testTextInput() throws Exception {
		MockCommand command = new MockCommand( 1 );
		context.submit( tool, command );
		command.waitFor();
		context.setInputMode( DesignCommandContext.Input.TEXT );
		context.processText( "test", true );
		command.waitFor();
		assertThat( command.getValues()[ 0 ] ).isEqualTo( "test" );
	}

	@Test
	void testUnknownInput() throws Exception {
		MockCommand command = new MockCommand( 1 );
		context.submit( tool, command );
		command.waitFor();
		context.setInputMode( DesignCommandContext.Input.NONE );
		try {
			context.processText( "unknown", true );
			fail();
		} catch( UnknownCommand exception ) {
			assertThat( exception.getMessage() ).isEqualTo( "unknown" );
		}
	}

	@Test
	void testAutoCommand() {
		getMod().getCommandMap().add( "test", MockCommand.class, "Test Command", "test", null );
		Command command = context.processText( "test", false );
		assertThat( command ).isInstanceOf( MockCommand.class );
	}

	@Test
	void testNoAutoCommandWithTextInput() {
		context.setInputMode( DesignCommandContext.Input.TEXT );
		getMod().getCommandMap().add( "test", MockCommand.class, "Test Command", "test", null );
		Command command = context.processText( "test", false );
		assertThat( command ).isNull();
	}

}
