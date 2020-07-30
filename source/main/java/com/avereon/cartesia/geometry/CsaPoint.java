package com.avereon.cartesia.geometry;

import javafx.geometry.Point3D;

public class CsaPoint extends CsaShape {

	public CsaPoint( Point3D origin ) {
		addModifyingKeys( ORIGIN );
		setOrigin( origin );
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN );
	}

}
