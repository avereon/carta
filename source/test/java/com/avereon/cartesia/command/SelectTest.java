package com.avereon.cartesia.command;

import com.avereon.cartesia.BaseCartesiaUiTest;
import com.avereon.cartesia.tool.CommandContext;
import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectTest extends BaseCartesiaUiTest {

	private CommandContext context;

	private Select command;

	public SelectTest() {}

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		context = new CommandContext( getMod() );
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
		assertThat( result ).isEqualTo( Command.INCOMPLETE );
		result = command.execute( context, createEvent( MouseEvent.MOUSE_RELEASED ) );
		assertThat( result ).isEqualTo( Command.COMPLETE );
	}

	private MouseEvent createEvent( EventType<MouseEvent> type ) {
		return new MouseEvent( type, 0, 0, 0, 0, MouseButton.PRIMARY, 0, false, false, false, false, false, false, false, false, false, false, null );
	}

}
