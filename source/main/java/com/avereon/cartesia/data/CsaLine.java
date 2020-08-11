package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import javafx.geometry.Point3D;

import java.util.Map;

public class CsaLine extends CsaShape {

	public static final String POINT = "point";

	public CsaLine() {
		addModifyingKeys( ORIGIN, POINT );
	}

	public CsaLine( Point3D origin, Point3D point ) {
		this();
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

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, "line" );
		map.putAll( asMap( ORIGIN, POINT ) );
		return map;
	}

	public CsaLine updateFrom( Map<String, String> map ) {
		super.updateFrom( map );
		setPoint( ParseUtil.parsePoint3D( map.get( POINT ) ) );
		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, POINT );
	}

}
