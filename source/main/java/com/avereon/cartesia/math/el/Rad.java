package com.avereon.cartesia.math.el;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import java.util.Stack;

public class Rad extends PostfixMathCommand {

	public Rad() {
		super.numberOfParameters = 1;
	}

	@SuppressWarnings( "unchecked" )
	public void run( Stack stack ) throws ParseException {
		this.checkStack( stack );
		Object value = stack.pop();
		stack.push( this.toRadians( value ) );
	}

	public Object toRadians( Object value ) throws ParseException {
		if( value instanceof Number ) {
			return Math.toRadians( ((Number)value).doubleValue() );
		} else {
			throw new ParseException( "Invalid parameter type" );
		}
	}

}
