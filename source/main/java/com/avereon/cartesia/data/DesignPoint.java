package com.avereon.cartesia.data;

import com.avereon.cartesia.math.Maths;
import com.avereon.cartesia.math.Shapes;
import com.avereon.curve.math.Geometry;
import javafx.geometry.Point3D;
import javafx.scene.paint.Paint;

import java.util.Map;

public class DesignPoint extends DesignShape {

	public static final String SIZE = "size";

	public static final String TYPE = "type";

	public static final double DEFAULT_SIZE = 1.0;

	private static final DesignPoints.Type DEFAULT_TYPE = DesignPoints.Type.CROSS;

	private static final double ZERO_DRAW_WIDTH = 0.0;

	public DesignPoint() {
		addModifyingKeys( ORIGIN, SIZE, TYPE );
	}

	public DesignPoint( Point3D origin ) {
		this();
		setOrigin( origin );
	}

	public double calcSize() {
		String size = getSize();
		if( size != null ) return Maths.evalNoException( size );
		return DEFAULT_SIZE;
	}

	public String getSize() {
		return getValue( SIZE );
	}

	public DesignPoint setSize( String size ) {
		setValue( SIZE, size );
		return this;
	}

	public DesignPoints.Type calcType() {
		return DesignPoints.parseType( getType() );
	}

	public String getType() {
		return getValue( TYPE );
	}

	public DesignPoint setType( String type ) {
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

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, "point" );
		map.putAll( asMap( SIZE, TYPE ) );
		return map;
	}

	public DesignPoint updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setSize( (String)map.get( SIZE ) );
		setType( (String)map.get( TYPE ) );
		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN );
	}

	public double getRadius() {
		return 0.5 * calcSize();
	}

	@Override
	public double distanceTo( Point3D point ) {
		double[] o = Shapes.asPoint( getOrigin() );
		double[] p = Shapes.asPoint( point );
		return Geometry.distance( o, p );
	}

}
