package com.avereon.cartesia;

import com.avereon.curve.math.Arithmetic;
import org.assertj.core.data.Offset;

public interface TestConstants {

	Offset<Double> TOLERANCE = Offset.offset( Arithmetic.DEFAULT_PRECISION );

}
