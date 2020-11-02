package com.avereon.cartesia.math;

import com.avereon.cartesia.math.el.Deg;
import com.avereon.cartesia.math.el.Rad;
import org.nfunk.jep.JEP;

import java.text.ParseException;

public class Maths {

	/**
	 * Evaluate the math expression.
	 *
	 * @param expression The math expression
	 * @return The value of the expression
	 * @throws ParseException
	 */
	public static double eval( String expression ) throws ExpressionException {
		return Expressions.eval( expression );
	}

	/**
	 * Evaluate the math expression. It is preferred to use {@link #eval} for
	 * better exception handling.
	 *
	 * @param expression The math expression
	 * @return The value of the expression
	 */
	public static double evalNoException( String expression ) {
		return Expressions.evalNoException( expression );
	}

	private static class Expressions extends JEP {

		public Expressions() {
			setAllowUndeclared( false );
			addStandardConstants();
			addStandardFunctions();
			addFunction( "deg", new Deg() );
			addFunction( "rad", new Rad() );
		}

		public static double eval( String expression ) throws ExpressionException {
			Expressions parser = new Expressions();
			parser.parseExpression( expression );
			if( parser.hasError() ) {
				ParseException parseException = new ParseException( parser.getErrorInfo(), -1 );
				throw new ExpressionException( parseException.getMessage(), parseException );
			}
			return parser.getValue();
		}

		public static double evalNoException( String expression ) {
			Expressions parser = new Expressions();
			parser.parseExpression( expression );
			return parser.getValue();
		}

	}

}
