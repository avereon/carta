package com.avereon.cartesia.tool;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.command.Command;
import javafx.scene.input.InputEvent;
import lombok.Getter;

@Getter
class MockCommand extends Command {

	private final int needed;

	private Object[] values;

	public MockCommand() {
		this( 0 );
	}

	public MockCommand( int needed ) {
		this.needed = needed;
	}

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) {
		if( parameters.length < needed ) return INCOMPLETE;
		this.values = parameters;
		return COMPLETE;
	}

}
