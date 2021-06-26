package com.avereon.cartesia.tool;

import com.avereon.cartesia.command.Command;

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
	public Object execute( CommandContext context, Object... parameters ) {
		if( parameters.length < needed ) return INCOMPLETE;
		this.values = parameters;
		return COMPLETE;
	}

	public Object[] getValues() {
		return values;
	}

}
