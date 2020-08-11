package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import javafx.geometry.Point3D;

import java.util.Map;

public abstract class CsaShape extends DesignDraw {

	public static final String SHAPE = "shape";

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

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( "type", "point" );
		map.putAll( asMap( ORIGIN ) );
		return map;
	}

	public CsaShape updateFrom( Map<String, String> map ) {
		super.updateFrom( map );
		setOrigin( ParseUtil.parsePoint3D( map.get( ORIGIN ) ) );
		return this;
	}

}
