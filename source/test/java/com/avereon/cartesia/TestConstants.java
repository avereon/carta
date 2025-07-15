package com.avereon.cartesia;

import com.avereon.curve.math.Arithmetic;
import org.assertj.core.data.Offset;

public interface TestConstants {

	Offset<Double> TIGHT_TOLERANCE = Offset.offset( Arithmetic.HIGH_PRECISION );
	Offset<Double> TOLERANCE = Offset.offset( Arithmetic.DEFAULT_PRECISION );
	Offset<Double> LOOSE_TOLERANCE = Offset.offset( Arithmetic.LOW_PRECISION);
	Offset<Double> EXTRA_LOOSE_TOLERANCE = Offset.offset( Arithmetic.ULTRA_LOW_PRECISION);

}
