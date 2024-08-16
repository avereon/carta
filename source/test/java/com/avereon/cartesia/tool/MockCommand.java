package com.avereon.cartesia.tool;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.CommandTask;
import lombok.Getter;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static com.avereon.cartesia.command.Command.Result.SUCCESS;

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
	public Object execute( CommandTask task) {
		if( task.getParameterCount() < expected ) return INCOMPLETE;
		this.values = task.getParameters();
		return SUCCESS;
	}

}
