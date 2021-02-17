package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadPoints;
import com.avereon.curve.math.Geometry;
import com.avereon.util.Log;
import javafx.geometry.Point3D;
import javafx.scene.shape.ArcType;

import java.util.Map;

public class DesignArc extends DesignEllipse {

	public enum Type {
		CHORD( ArcType.CHORD ),
		OPEN( ArcType.OPEN ),
		PIE( ArcType.ROUND );

		private final ArcType fxArcType;

		Type( ArcType fxArcType ) {
			this.fxArcType = fxArcType;
		}

		public ArcType arcType() {
			return fxArcType;
		}
	}

	public static final String ARC = "arc";

	public static final String START = "start";

	public static final String EXTENT = "extent";

	public static final String TYPE = "type";

	private static final System.Logger log = Log.get();

	public DesignArc() {
		this( null, null, null, null, null );
	}

	public DesignArc( Point3D origin, Double radius, Double start, Double extent, Type type ) {
		this( origin, radius, radius, start, extent, type );
	}

	public DesignArc( Point3D origin, Double xRadius, Double yRadius, Double start, Double extent, Type type ) {
		this( origin, xRadius, yRadius, start, extent, null, type );
	}

	public DesignArc( Point3D origin, Double xRadius, Double yRadius, Double start, Double extent, Double rotate, Type type ) {
		super( origin, xRadius, yRadius, rotate );
		addModifyingKeys( START, EXTENT, TYPE );
		setStart( start );
		setExtent( extent );
		setType( type );
	}

	public Double getStart() {
		return getValue( START );
	}

	public DesignArc setStart( Double value ) {
		setValue( START, value );
		return this;
	}

	public Double getExtent() {
		return getValue( EXTENT );
	}

	public DesignArc setExtent( Double value ) {
		setValue( EXTENT, value );
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
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, ARC );
		map.putAll( asMap( START, EXTENT, TYPE ) );
		return map;
	}

	public DesignArc updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( START ) ) setStart( (Double)map.get( START ) );
		if( map.containsKey( EXTENT ) ) setExtent( (Double)map.get( EXTENT ) );
		if( map.containsKey( TYPE ) ) setType( Type.valueOf( ((String)map.get( TYPE )).toUpperCase() ) );
		return this;
	}

	@Override
	public double distanceTo( Point3D point ) {
		// TODO Improve DesignArc.distanceTo()
		// This implementation is a simple estimate based on the origin and radius
		double[] o = CadPoints.asPoint( getOrigin() );
		double[] p = CadPoints.asPoint( point );
		return Math.abs( Geometry.distance( o, p ) - getRadius() );
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, RADIUS, X_RADIUS, Y_RADIUS, ROTATE, START, EXTENT, TYPE );
	}

}
