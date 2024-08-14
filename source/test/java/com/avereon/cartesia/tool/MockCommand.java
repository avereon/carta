package com.avereon.cartesia.tool;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.command.Command;
import javafx.scene.input.InputEvent;
import lombok.Getter;
import static com.avereon.cartesia.command.Command.Result.*;

@Getter
class MockCommand extends Command {

	private final int expected;

	private Object[] values;

	public MockCommand() {
		this( 0 );
	}

	public MockCommand( int expected ) {
		this.expected = expected;
	}

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) {
		if( parameters.length < expected ) return INCOMPLETE;
		this.values = parameters;
		return SUCCESS;
	}

}
