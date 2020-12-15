package com.avereon.cartesia.data;

import javafx.geometry.Point3D;

import java.util.Comparator;

public class DesignShapeDistanceComparator implements Comparator<DesignShape> {

	private final Point3D point;

	public DesignShapeDistanceComparator( Point3D point ) {
		this.point = point;
	}

	@Override
	public int compare( DesignShape a, DesignShape b ) {
		return (int)Math.signum( a.distanceTo( point ) - b.distanceTo( point ) );
	}

}
