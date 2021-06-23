package com.avereon.cartesia.command;

import com.avereon.cartesia.BaseCartesiaTest;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.xenon.asset.Asset;
import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SelectTest extends BaseCartesiaTest {

	private CommandContext context;

	private Asset asset;

	private DesignTool tool;

	private Select command;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		context = new CommandContext( getMod() );
		//tool = new DesignTool( getProduct(), asset ) {};
		command = new Select();
	}

	@Test
	void testExecuteNoArgs() throws Exception {
		command.execute( context );
	}

	@Test
	void testExecute() throws Exception {
		Object result;
		result = command.execute( context, createEvent( MouseEvent.MOUSE_PRESSED ) );
		assertThat( result, is( Command.INCOMPLETE ) );
		result = command.execute( context, createEvent( MouseEvent.MOUSE_RELEASED ) );
		assertThat( result, is( Command.COMPLETE ) );
	}

	@Test
	void testExecuteInPenMode() {
		//		context.submit( tool, command, createEvent( MouseEvent.MOUSE_PRESSED ) );
		//		Object result;
		//		result = command.execute( context, null, createEvent( MouseEvent.MOUSE_PRESSED ) );
		//		assertThat( result, is( Command.INCOMPLETE ) );
		//		result = command.execute( context, null, createEvent( MouseEvent.MOUSE_RELEASED ) );
		//		assertThat( result, is( Command.COMPLETE ) );
	}

	private MouseEvent createEvent( EventType<MouseEvent> type ) {
		return new MouseEvent( type, 0, 0, 0, 0, MouseButton.PRIMARY, 0, false, false, false, false, false, false, false, false, false, false, null );
	}

}
