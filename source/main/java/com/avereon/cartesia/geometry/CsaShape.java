package com.avereon.cartesia.geometry;

import com.avereon.cartesia.data.DesignDraw;
import javafx.geometry.Point3D;

import java.util.Map;

public abstract class CsaShape extends DesignDraw {

	public static final String ORIGIN = "origin";

	public CsaShape() {
		addModifyingKeys( ORIGIN );
	}

	public Point3D getOrigin() {
		return getValue( ORIGIN );
	}

	public CsaShape setOrigin( Point3D origin ) {
		setValue( ORIGIN, origin );
		return this;
	}

}
