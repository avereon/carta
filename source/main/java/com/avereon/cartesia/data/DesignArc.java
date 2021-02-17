package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadPoints;
import com.avereon.curve.math.Geometry;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

import java.util.Map;

public class DesignArc extends DesignShape {

	public enum Type {
		CHORD,
		FULL,
		OPEN,
		PIE
	}

	public static final String ARC = "arc";

	public static final String CIRCLE = "circle";

	public static final String ELLIPSE = "ellipse";

	public static final String RADIUS = "radius";

	public static final String X_RADIUS = "x-radius";

	public static final String Y_RADIUS = "y-radius";

	public static final String START = "start";

	public static final String EXTENT = "extent";

	public static final String ROTATE = "rotate";

	public static final String TYPE = "type";

	private static final System.Logger log = Log.get();

	public DesignArc() {
		addModifyingKeys( ORIGIN, RADIUS, X_RADIUS, Y_RADIUS, START, EXTENT, ROTATE, TYPE );
	}

	public DesignArc( Point3D origin, double radius ) {
		this( origin, radius, radius, 0, 360, 0, Type.FULL );
	}

	public DesignArc( Point3D origin, double radius, double start, double extent, Type type ) {
		this( origin, radius, radius, start, extent, 0, type );
	}

	public DesignArc( Point3D origin, double xRadius, double yRadius ) {
		this( origin, xRadius, yRadius, 0, 360, 0, Type.FULL );
	}

	public DesignArc( Point3D origin, double xRadius, double yRadius, double start, double extent ) {
		this( origin, xRadius, yRadius, start, extent, 0, Type.FULL );
	}

	public DesignArc( Point3D origin, double xRadius, double yRadius, double start, double extent, double rotate, Type type ) {
		this();
		setOrigin( origin );
		if( xRadius == yRadius ) setRadius( xRadius );
		setXRadius( xRadius );
		setYRadius( yRadius );
		setStart( start );
		setExtent( extent );
		setRotate( rotate );
		setType( type );
	}

	public Double getRadius() {
		return getValue( RADIUS );
	}

	public DesignArc setRadius( double value ) {
		setValue( RADIUS, value );
		return this;
	}

	public Double getXRadius() {
		return getValue( X_RADIUS, (Double)getValue( RADIUS ) );
	}

	public DesignArc setXRadius( double value ) {
		setValue( X_RADIUS, value );
		return this;
	}

	public Double getYRadius() {
		return getValue( Y_RADIUS, (Double)getValue( RADIUS ) );
	}

	public DesignArc setYRadius( double value ) {
		setValue( Y_RADIUS, value );
		return this;
	}

	public Double getStart() {
		return getValue( START );
	}

	public DesignArc setStart( double value ) {
		setValue( START, value );
		return this;
	}

	public Double getExtent() {
		return getValue( EXTENT );
	}

	public DesignArc setExtent( double value ) {
		setValue( EXTENT, value );
		return this;
	}

	public Double getRotate() {
		return getValue( ROTATE );
	}

	public DesignArc setRotate( double value ) {
		setValue( ROTATE, value );
		return this;
	}

	public DesignArc.Type getType() {
		return getValue( TYPE );
	}

	public DesignArc setType( DesignArc.Type value ) {
		setValue( TYPE, value );
		return this;
	}

	protected Map<String, Object> asMap() {
		String shape = ARC;
		if( getExtent() == 360 ) shape = getXRadius().equals( getYRadius() ) ? CIRCLE : ELLIPSE;

		Map<String, Object> map = super.asMap();
		map.put( SHAPE, shape );
		map.putAll( asMap( RADIUS, X_RADIUS, Y_RADIUS, START, EXTENT, ROTATE, TYPE ) );
		return map;
	}

	public DesignArc updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( RADIUS ) ) setRadius( (Double)map.get( RADIUS ) );
		if( map.containsKey( X_RADIUS ) ) setXRadius( (Double)map.get( X_RADIUS ) );
		if( map.containsKey( Y_RADIUS ) ) setYRadius( (Double)map.get( Y_RADIUS ) );
		if( map.containsKey( START ) ) setStart( (Double)map.get( START ) );
		if( map.containsKey( EXTENT ) ) setExtent( (Double)map.get( EXTENT ) );
		if( map.containsKey( ROTATE ) ) setRotate( (Double)map.get( ROTATE ) );
		if( map.containsKey( TYPE ) ) setType( Type.valueOf( ((String)map.get( TYPE )).toUpperCase() ) );
		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, RADIUS );
	}

	@Override
	public double distanceTo( Point3D point ) {
		// TODO Improve DesignCircle.distanceTo()
		// This implementation is a simple estimate based on the origin and radius
		double[] o = CadPoints.asPoint( getOrigin() );
		double[] p = CadPoints.asPoint( point );
		return Math.abs( Geometry.distance( o, p ) - getRadius() );
	}

}
