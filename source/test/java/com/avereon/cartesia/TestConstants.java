package com.avereon.cartesia;

import com.avereon.curve.math.Arithmetic;
import org.assertj.core.data.Offset;

import static com.avereon.curve.math.Arithmetic.DEFAULT_DIGITS;

public interface TestConstants {

	Offset<Double> TOLERANCE = Offset.offset( Arithmetic.DEFAULT_PRECISION );
	Offset<Double> LOOSE_TOLERANCE = Offset.offset( 1.0 / Math.pow( 10, 0.5 * DEFAULT_DIGITS ));

}
