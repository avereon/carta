package com.avereon.cartesia;

import com.avereon.curve.math.Arithmetic;
import org.assertj.core.data.Offset;
import org.assertj.core.data.Percentage;

public interface TestConstants {

	@Deprecated
	Offset<Double> TOLERANCE = Offset.offset( Arithmetic.DEFAULT_PRECISION );

	Percentage TOLERANCE_PERCENT_EXTRA_TIGHT = Percentage.withPercentage( 0.0000000000000001 );
	Percentage TOLERANCE_PERCENT_TIGHT = Percentage.withPercentage( 0.0000000000001 );
	Percentage TOLERANCE_PERCENT = Percentage.withPercentage( 0.0000000001 );
	Percentage TOLERANCE_PERCENT_LOOSE = Percentage.withPercentage( 0.0001 );
	Percentage TOLERANCE_PERCENT_EXTRA_LOOSE = Percentage.withPercentage( 0.0001 );

}
