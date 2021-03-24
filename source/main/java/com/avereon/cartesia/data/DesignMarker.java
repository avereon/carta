package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.curve.math.Geometry;
import javafx.geometry.Point3D;
import javafx.scene.paint.Paint;

import java.util.Map;

public class DesignMarker extends DesignShape {

	public static final String MARKER = "marker";

	@Deprecated
	public static final String POINT = "point";

	public static final String SIZE = "size";

	public static final String TYPE = "type";

	public static final double DEFAULT_SIZE = 1.0;

	private static final double ZERO_DRAW_WIDTH = 0.0;

	public DesignMarker() {
		this( null );
	}

	public DesignMarker( Point3D origin ) {
		super( origin );
		addModifyingKeys( ORIGIN, SIZE, TYPE );
	}

	public double calcSize() {
		String size = getSize();
		if( size != null ) return CadMath.evalNoException( size );
		return DEFAULT_SIZE;
	}

	public String getSize() {
		return getValue( SIZE );
	}

	public DesignMarker setSize( String size ) {
		setValue( SIZE, size );
		return this;
	}

	public DesignMarkers.Type calcType() {
		return DesignMarkers.parseType( getType() );
	}

	public String getType() {
		return getValue( TYPE );
	}

	public DesignMarker setType( String type ) {
		setValue( TYPE, type );
		return this;
	}

	@Override
	public double calcDrawWidth() {
		return calcType().isClosed() ? ZERO_DRAW_WIDTH : super.calcDrawWidth();
	}

	@Override
	public Paint calcFillPaint() {
		return calcDrawPaint();
	}

	public double getRadius() {
		return 0.5 * calcSize();
	}

	@Override
	public double distanceTo( Point3D point ) {
		double[] o = CadPoints.asPoint( getOrigin() );
		double[] p = CadPoints.asPoint( point );
		return Geometry.distance( o, p );
	}

	@Override
	public DesignMarker cloneShape() {
		return new DesignMarker().copyFrom( this );
	}

	@Override
	public void apply( CadTransform transform ) {
		setOrigin( transform.apply( getOrigin()) );
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, MARKER );
		map.putAll( asMap( SIZE, TYPE ) );
		return map;
	}

	public DesignMarker updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setSize( (String)map.get( SIZE ) );
		setType( (String)map.get( TYPE ) );
		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN );
	}

}
