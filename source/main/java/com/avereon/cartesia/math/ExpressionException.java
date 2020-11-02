package com.avereon.cartesia.math;

public class ExpressionException extends Exception {

	public ExpressionException() {
		super();
	}

	public ExpressionException( String message ) {
		super( message );
	}

	public ExpressionException( String message, Throwable cause ) {
		super( message, cause );
	}

	public ExpressionException( Throwable cause ) {
		super( cause );
	}

}
