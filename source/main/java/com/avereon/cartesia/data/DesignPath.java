package com.avereon.cartesia.data;

import javafx.geometry.Point3D;

public class DesignPath extends DesignShape {

	public static final String PATH = "path";

	public static final String CLOSED = "closed";

	@Override
	public double distanceTo( Point3D point ) {
		return Double.NaN;
	}

}
