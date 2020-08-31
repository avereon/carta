package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.math.Points;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.cartesia.tool.DesignPane;
import javafx.geometry.Point3D;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

import java.util.List;
import java.util.Map;

public class CsaPoint extends CsaShape {

	public static final String SIZE = "size";

	public static final String TYPE = "type";

	private static final double DEFAULT_SIZE = 1;

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

	public String getType() {
		return getValue( TYPE );
	}

	public CsaPoint setType( String type ) {
		setValue( TYPE, type );
		return this;
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
		setType( map.get( TYPE ) );
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
		Path path = Points.createPoint( getType(), ox, oy, getRadius() );

		configureShape( path );
		// If the path is closed then the listeners need to be modified a bit
		return List.of( path );
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
