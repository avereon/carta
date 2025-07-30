package com.avereon.cartesia.tool;

import javafx.geometry.Point3D;

public interface RenderConstants {

	double DEFAULT_DPI = 96;

	double DEFAULT_OUTPUT_SCALE = 1.0;

	Point3D DEFAULT_CENTER = new Point3D( 0, 0, 0 );

	double DEFAULT_ROTATE = 0;

	double DEFAULT_ZOOM = 1;

	/**
	 * The default JavaFx refresh rate of 60 Hz.
	 */
	long DEFAULT_REFRESH_RATE = 60;

	/**
	 * The default refresh interval time in nanoseconds.
	 */
	long DEFAULT_REFRESH_TIME_NANOS = 1_000_000_000L / DEFAULT_REFRESH_RATE;

}
