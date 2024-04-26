package com.avereon.cartesia.math;

import com.avereon.cartesia.math.el.Deg;
import com.avereon.cartesia.math.el.Rad;
import com.avereon.util.TextUtil;
import org.nfunk.jep.JEP;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CadMath {

	public static final double SQRT2 = Math.sqrt( 2 );
	public static final double SQRT2_OVER_2 = 0.5 * SQRT2;

	/**
	 * Evaluate the math expression.
	 *
	 * @param expression The math expression
	 * @return The value of the expression
	 * @throws CadMathExpressionException If the expression cannot be evaluated
	 */
	public static double eval( String expression ) throws CadMathExpressionException {
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

	public static List<Double> evalExpressions( String expressions ) {
		if( TextUtil.isEmpty( expressions ) ) return List.of();
		return Arrays.stream( expressions.split( "," ) ).map( CadMath::evalNoException ).collect( Collectors.toList() );
	}

	private static class Expressions extends JEP {

		public Expressions() {
			setAllowUndeclared( false );
			addStandardConstants();
			addStandardFunctions();
			addFunction( "deg", new Deg() );
			addFunction( "rad", new Rad() );
		}

		public static double eval( String expression ) throws CadMathExpressionException {
			Expressions parser = new Expressions();
			parser.parseExpression( expression );
			if( parser.hasError() ) {
				ParseException parseException = new ParseException( parser.getErrorInfo(), -1 );
				throw new CadMathExpressionException( parseException.getMessage(), parseException );
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
