package com.avereon.cartesia.el;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;
import org.nfunk.jep.type.Complex;

import java.util.Stack;

public class Deg extends PostfixMathCommand {

	public Deg() {
		super.numberOfParameters = 1;
	}

	@SuppressWarnings( "unchecked" )
	public void run( Stack stack ) throws ParseException {
		this.checkStack( stack );
		Object value = stack.pop();
		stack.push( this.sin( value ) );
	}

	public Object sin( Object value ) throws ParseException {
		if( value instanceof Number ) {
			return Math.toDegrees( ((Number)value).doubleValue() );
		} else if( value instanceof Complex ) {
			return ((Complex)value).sin();
		} else {
			throw new ParseException( "Invalid parameter type" );
		}
	}


}
