package com.avereon.cartesia.data;

import javafx.geometry.Point3D;

public class DesignCurve extends DesignShape {

	public static final String CURVE = "curve";

	public static final String B = "b";

	public static final String C = "c";

	public static final String D = "d";

	public DesignCurve() {
		addModifyingKeys( B, C, D );
	}

	public DesignCurve( Point3D origin, Point3D b, Point3D c, Point3D d ) {
		this();
		setOrigin( origin );
		setB( b );
		setC( c );
		setD( d );
	}

	public Point3D getB() {
		return getValue( B );
	}

	public DesignShape setB( Point3D value ) {
		setValue( B, value );
		return this;
	}

	public Point3D getC() {
		return getValue( C );
	}

	public DesignShape setC( Point3D value ) {
		setValue( C, value );
		return this;
	}

	public Point3D getD() {
		return getValue( D );
	}

	public DesignShape setD( Point3D value ) {
		setValue( D, value );
		return this;
	}

}
