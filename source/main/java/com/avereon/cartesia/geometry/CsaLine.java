package com.avereon.cartesia.geometry;

import javafx.geometry.Point3D;

public class CsaLine extends CsaShape {

	public static final String POINT = "point";

	public CsaLine( Point3D origin, Point3D point ) {
		addModifyingKeys( ORIGIN, POINT );
		setOrigin( origin );
		setPoint( point );
	}

	public Point3D getPoint() {
		return getValue( POINT );
	}

	public CsaShape setPoint( Point3D point ) {
		setValue( POINT, point );
		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, POINT );
	}

}
