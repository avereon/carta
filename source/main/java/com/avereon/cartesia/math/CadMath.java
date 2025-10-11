package com.avereon.cartesia.math;

import com.avereon.util.TextUtil;
import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.Function;
import com.fathzer.soft.javaluator.Parameters;

import java.util.Arrays;
import java.util.Iterator;
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
	 * @throws IllegalArgumentException If the expression cannot be evaluated
	 */
	public static double evalNoException( String expression ) {
		if( TextUtil.isEmpty( expression ) ) return 0;
		return new CustomDoubleEvaluator().evaluate( expression );
	}

	/**
	 * Evaluate a comma-separated list of math expressions.
	 *
	 * @param expressions The comma-separated list of math expressions
	 * @return The list of values of the expressions
	 */
	public static List<Double> evalExpressions( String expressions ) {
		if( TextUtil.isEmpty( expressions ) ) return List.of();
		return Arrays.stream( expressions.split( "," ) ).map( CadMath::evalNoException ).collect( Collectors.toList() );
	}

	private static class CustomDoubleEvaluator extends DoubleEvaluator {

		private static final Function DEG = new Function( "deg", 1 );

		private static final Function RAD = new Function( "rad", 1 );

		private static final Function SQRT = new Function( "sqrt", 1 );

		private static final Parameters extraFunctions = DoubleEvaluator.getDefaultParameters();

		static {
			extraFunctions.add( DEG );
			extraFunctions.add( RAD );
			extraFunctions.add( SQRT );
		}

		public CustomDoubleEvaluator() {
			super( extraFunctions, true );
		}

		@Override
		@SuppressWarnings( "unchecked" )
		protected Double evaluate( Function function, Iterator arguments, Object evaluationContext ) {
			if( function == DEG ) {
				return Math.toDegrees( ((Number)arguments.next()).doubleValue() );
			} else if( function == RAD ) {
				return Math.toRadians( ((Number)arguments.next()).doubleValue() );
			} else if( function == SQRT ) {
				return Math.sqrt( ((Number)arguments.next()).doubleValue() );
			} else {
				return super.evaluate( function, arguments, evaluationContext );
			}
		}

	}

}
