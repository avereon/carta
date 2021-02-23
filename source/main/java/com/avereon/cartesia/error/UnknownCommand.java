package com.avereon.cartesia.error;

public class UnknownCommand extends RuntimeException {

	public UnknownCommand( String message ) {
		super( message );
	}

	public UnknownCommand( String message, Throwable cause ) {
		super( message, cause );
	}

}
