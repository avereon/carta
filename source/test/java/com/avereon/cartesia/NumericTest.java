package com.avereon.cartesia;

public interface NumericTest {

	double PI_OVER_4 = 0.25 * Math.PI;

	// This value is a bit roomier than required. Most tests have deltas smaller
	// than 1e-14. This is set just a bit larger to give some wiggle room.
	double TOLERANCE = 1e-12;

}
