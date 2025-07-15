package com.avereon.cartesia;

import com.avereon.curve.math.Arithmetic;
import org.assertj.core.data.Offset;
import org.assertj.core.data.Percentage;

public interface TestConstants {

	Offset<Double> TOLERANCE = Offset.offset( Arithmetic.DEFAULT_PRECISION );
	Offset<Double> EXTRA_LOOSE_TOLERANCE = Offset.offset( Arithmetic.EXTRA_LOW_PRECISION );

	Percentage TOLERANCE_PERCENT_EXTRA_TIGHT = Percentage.withPercentage( 0.0000000000000001 );
	Percentage TOLERANCE_PERCENT_TIGHT = Percentage.withPercentage( 0.0000000000001 );
	Percentage TOLERANCE_PERCENT = Percentage.withPercentage( 0.0000000001 );
	Percentage TOLERANCE_PERCENT_LOOSE = Percentage.withPercentage( 0.0000001 );
	Percentage TOLERANCE_PERCENT_EXTRA_LOOSE = Percentage.withPercentage( 0.0001 );

}
