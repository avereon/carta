package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.curve.math.Geometry;
import javafx.geometry.Point3D;

import java.util.Map;

public class DesignCurve extends DesignShape {

	public static final String CURVE = "curve";

	public static final String ORIGIN_CONTROL = "origin-control";

	public static final String POINT_CONTROL = "point-control";

	public static final String POINT = "point";

	public DesignCurve() {
		addModifyingKeys( ORIGIN_CONTROL, POINT_CONTROL, POINT );
	}

	public DesignCurve( Point3D origin, Point3D originControl, Point3D pointControl, Point3D point ) {
		this();
		setOrigin( origin );
		setOriginControl( originControl );
		setPointControl( pointControl );
		setPoint( point );
	}

	public Point3D getOriginControl() {
		return getValue( ORIGIN_CONTROL );
	}

	public DesignShape setOriginControl( Point3D value ) {
		setValue( ORIGIN_CONTROL, value );
		return this;
	}

	public Point3D getPointControl() {
		return getValue( POINT_CONTROL );
	}

	public DesignShape setPointControl( Point3D value ) {
		setValue( POINT_CONTROL, value );
		return this;
	}

	public Point3D getPoint() {
		return getValue( POINT );
	}

	public DesignShape setPoint( Point3D value ) {
		setValue( POINT, value );
		return this;
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, CURVE );
		map.putAll( asMap( ORIGIN_CONTROL, POINT_CONTROL, POINT ) );
		return map;
	}

	public DesignCurve updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setOriginControl( ParseUtil.parsePoint3D( (String)map.get( ORIGIN_CONTROL ) ) );
		setPointControl( ParseUtil.parsePoint3D( (String)map.get( POINT_CONTROL ) ) );
		setPoint( ParseUtil.parsePoint3D( (String)map.get( POINT ) ) );
		return this;
	}

	@Override
	public double distanceTo( Point3D point ) {
		// TODO Improve DesignCurve.distanceTo()
		// This implementation is a simple estimate based on the origin and point
		double[] a = CadPoints.asPoint( getOrigin() );
		double[] b = CadPoints.asPoint( getPoint() );
		double[] p = CadPoints.asPoint( point );
		return Geometry.pointLineDistance( a, b, p );
	}

}
