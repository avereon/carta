package com.avereon.cartesia.math;

import com.avereon.cartesia.math.el.Deg;
import com.avereon.cartesia.math.el.Rad;
import org.nfunk.jep.JEP;

import java.text.ParseException;

public class MathEx extends JEP {

	public MathEx() {
		setAllowUndeclared( false );
		addStandardConstants();
		addStandardFunctions();
		addFunction( "deg", new Deg() );
		addFunction( "rad", new Rad() );
	}

	public static double eval( String expression ) {
		MathEx parser = new MathEx();
		parser.parseExpression( expression );
		return parser.getValue();
	}

	public static double parse( String text ) throws ParseException {
		MathEx parser = new MathEx();
		parser.parseExpression( text );
		if( parser.hasError() ) throw new ParseException( parser.getErrorInfo(), -1 );
		return parser.getValue();
	}

}
