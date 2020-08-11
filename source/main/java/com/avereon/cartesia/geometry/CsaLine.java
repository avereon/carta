package com.avereon.cartesia.geometry;

import javafx.geometry.Point3D;

import java.util.Map;

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

	public Map<String, String> asMap() {
		Map<String, String> map = super.asMap();
		map.put( "type", "line" );
		map.putAll( asMap( ORIGIN, POINT ) );
		return map;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, POINT );
	}

}
