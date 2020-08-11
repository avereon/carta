package com.avereon.cartesia.data;

import javafx.geometry.Point3D;

import java.util.Map;

public class CsaPoint extends CsaShape {

	public CsaPoint() {
		addModifyingKeys( ORIGIN );
	}

	public CsaPoint( Point3D origin ) {
		this();
		setOrigin( origin );
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, "point" );
		return map;
	}

	public CsaPoint updateFrom( Map<String, String> map ) {
		super.updateFrom( map );
		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN );
	}

}
