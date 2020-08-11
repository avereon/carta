package com.avereon.cartesia.geometry;

import javafx.geometry.Point3D;

import java.util.Map;

public class CsaPoint extends CsaShape {

	public CsaPoint( Point3D origin ) {
		addModifyingKeys( ORIGIN );
		setOrigin( origin );
	}

	public Map<String, String> asMap() {
		Map<String, String> map = super.asMap();
		map.put( "type", "point" );
		map.putAll( asMap( ORIGIN ) );
		return map;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN );
	}

}
