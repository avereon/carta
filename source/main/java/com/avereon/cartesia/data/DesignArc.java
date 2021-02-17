package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadPoints;
import com.avereon.curve.math.Geometry;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

import java.util.Map;

public class DesignArc extends DesignShape {

	public enum Type {
		CHORD,
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

	public static final String TYPE = "type"; // open, chord, pie

	private static final System.Logger log = Log.get();

	public DesignArc() {
		addModifyingKeys( ORIGIN, X_RADIUS, Y_RADIUS, START, EXTENT, ROTATE, TYPE );
	}

	public DesignArc( Point3D origin, double radius ) {
		this( origin, radius, radius, 0, 360, 0, Type.OPEN );
	}

	public DesignArc( Point3D origin, double xRadius, double yRadius, double start, double extent, double rotate, Type type ) {
		this();
		setOrigin( origin );
		setXRadius( xRadius );
		setYRadius( yRadius );
		setStart( start );
		setExtent( extent );
		setRotate( rotate );
		setType( type );
	}

	public Double getRadius() {
		return getXRadius();
	}

	public DesignArc setRadius( double value ) {
		try( Txn ignore = Txn.create() ) {
			setXRadius( value );
			setYRadius( value );
		} catch( TxnException exception ) {
			log.log( Log.WARN, "Unable to set radius: " + value );
		}
		return this;
	}

	public Double getXRadius() {
		return getValue( X_RADIUS );
	}

	public DesignShape setXRadius( double value ) {
		setValue( X_RADIUS, value );
		return this;
	}

	public Double getYRadius() {
		return getValue( Y_RADIUS );
	}

	public DesignShape setYRadius( double value ) {
		setValue( Y_RADIUS, value );
		return this;
	}

	public Double getStart() {
		return getValue( START );
	}

	public DesignShape setStart( double value ) {
		setValue( START, value );
		return this;
	}

	public Double getExtent() {
		return getValue( EXTENT );
	}

	public DesignShape setExtent( double value ) {
		setValue( EXTENT, value );
		return this;
	}

	public Double getRotate() {
		return getValue( ROTATE );
	}

	public DesignShape setRotate( double value ) {
		setValue( ROTATE, value );
		return this;
	}

	public Type getType() {
		return getValue( TYPE );
	}

	public DesignShape setType( Type value ) {
		setValue( TYPE, value );
		return this;
	}

	protected Map<String, Object> asMap() {
		String shape = ARC;
		if( getExtent() == 360 ) shape = getXRadius().equals( getYRadius() ) ? CIRCLE : ELLIPSE;

		Map<String, Object> map = super.asMap();
		map.put( SHAPE, shape );
		map.putAll( asMap( X_RADIUS, Y_RADIUS, START, EXTENT, ROTATE, TYPE ) );
		return map;
	}

	public DesignArc updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( RADIUS ) ) {
			setXRadius( (Double)map.get( RADIUS ) );
			setYRadius( (Double)map.get( RADIUS ) );
		} else {
			setXRadius( (Double)map.get( X_RADIUS ) );
			setYRadius( (Double)map.get( Y_RADIUS ) );
		}
		if( map.containsKey( START ) ) setStart( (Double)map.get( START ) );
		if( map.containsKey( EXTENT ) ) setExtent( (Double)map.get( EXTENT ) );
		if( map.containsKey( ROTATE ) ) setRotate( (Double)map.get( ROTATE ) );
		if( map.containsKey( TYPE ) ) setType( Type.valueOf( ((String)map.get( TYPE )).toUpperCase() ) );
		return this;
	}

	@Override
	public double distanceTo( Point3D point ) {
		double[] o = CadPoints.asPoint( getOrigin() );
		double[] p = CadPoints.asPoint( point );
		return Math.abs( Geometry.distance( o, p ) - getRadius() );
	}

}
