package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.curve.math.Geometry;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

import java.util.Map;

public class DesignLine extends DesignShape {

	public static final String LINE = "line";

	public static final String POINT = "point";

	private static final System.Logger log = Log.get();

	public DesignLine() {
	}

	public DesignLine( Point3D origin, Point3D point ) {
		super( origin );
		addModifyingKeys( ORIGIN, POINT );
		setOrigin( origin );
		setPoint( point );
	}

	public Point3D getPoint() {
		return getValue( POINT );
	}

	public DesignShape setPoint( Point3D point ) {
		setValue( POINT, point );
		return this;
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, LINE );
		map.putAll( asMap( POINT ) );
		return map;
	}

	public DesignLine updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setPoint( ParseUtil.parsePoint3D( (String)map.get( POINT ) ) );
		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, POINT );
	}

	@Override
	public double distanceTo( Point3D point ) {
		double[] a = CadPoints.asPoint( getOrigin() );
		double[] b = CadPoints.asPoint( getPoint() );
		double[] p = CadPoints.asPoint( point );
		return Geometry.pointLineDistance( a, b, p );
	}

}
