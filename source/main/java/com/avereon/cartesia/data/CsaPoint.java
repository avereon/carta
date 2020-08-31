package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.math.Points;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.cartesia.tool.DesignPane;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

import java.util.List;
import java.util.Map;

public class CsaPoint extends CsaShape {

	public static final String SIZE = "size";

	public static final String TYPE = "type";

	private static final double DEFAULT_SIZE = 1.0;

	private static final Points.Type DEFAULT_TYPE = Points.Type.CROSS;

	private static final double ZERO_DRAW_WIDTH = 0.0;

	public CsaPoint() {
		addModifyingKeys( ORIGIN, SIZE, TYPE );
	}

	public CsaPoint( Point3D origin ) {
		this();
		setOrigin( origin );
	}

	public Double getCalculatedSize() {
		Double size = getSize();
		return size == null ? DEFAULT_SIZE : size;
	}

	public Double getSize() {
		return getValue( SIZE );
	}

	public CsaPoint setSize( Double size ) {
		setValue( SIZE, size );
		return this;
	}

	public Points.Type calcType() {
		Points.Type type = getType();
		return type == null ? DEFAULT_TYPE : type;
	}

	public Points.Type getType() {
		return getValue( TYPE );
	}

	public CsaPoint setType( Points.Type type ) {
		setValue( TYPE, type );
		return this;
	}

	@Override
	public double calcDrawWidth() {
		return calcType().isClosed() ? ZERO_DRAW_WIDTH : super.calcDrawWidth();
	}

	@Override
	public Color calcFillColor() {
		return calcDrawColor();
	}

	@Override
	public Color calcSelectFillColor() {
		return calcSelectDrawColor();
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, "point" );
		map.putAll( asMap( ORIGIN, SIZE, TYPE ) );
		return map;
	}

	public CsaPoint updateFrom( Map<String, String> map ) {
		super.updateFrom( map );
		setSize( ParseUtil.parseDouble( map.get( SIZE ) ) );
		setType( Points.parsePointType( map.get( TYPE ) ) );
		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN );
	}

	@Override
	public List<Shape> generateGeometry() {
		double ox = getOrigin().getX();
		double oy = getOrigin().getY();
		Path path = Points.createPoint( calcType(), ox, oy, getRadius() );
		return List.of( configureShape( path ) );
	}

	@Override
	public List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Path path = (Path)shapes.get( 0 );
		MoveTo m = ((MoveTo)path.getElements().get( 0 ));
		ConstructionPoint o = cp( pane, m.xProperty(), m.yProperty() );

		List<ConstructionPoint> cps = List.of( o );
		path.getProperties().put( CONSTRUCTION_POINTS, cps );
		return cps;
	}

	private double getRadius() {
		return 0.5 * getCalculatedSize();
	}

}
