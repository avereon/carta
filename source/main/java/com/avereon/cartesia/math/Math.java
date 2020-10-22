package com.avereon.cartesia.math;

import com.avereon.cartesia.math.el.Deg;
import com.avereon.cartesia.math.el.Rad;
import org.nfunk.jep.JEP;

import java.text.ParseException;

public class Math extends JEP {

	public Math() {
		setAllowUndeclared( false );
		addStandardConstants();
		addStandardFunctions();
		addFunction( "deg", new Deg() );
		addFunction( "rad", new Rad() );
	}

	/**
	 * Evaluate the math expression.
	 *
	 * @param expression The math expression
	 * @return The value of the expression
	 * @throws ParseException
	 */
	public static double eval( String expression ) throws ParseException {
		Math parser = new Math();
		parser.parseExpression( expression );
		if( parser.hasError() ) throw new ParseException( parser.getErrorInfo(), -1 );
		return parser.getValue();
	}

	/**
	 * Evaluate the math expression. It is preferred to use {@link #eval} for
	 * better exception handling.
	 *
	 * @param expression The math expression
	 * @return The value of the expression
	 */
	public static double evalNoException( String expression ) {
		Math parser = new Math();
		parser.parseExpression( expression );
		return parser.getValue();
	}

}
