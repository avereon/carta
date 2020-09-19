package com.avereon.cartesia.data;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.cartesia.tool.DesignPane;
import com.avereon.data.NodeSettingsWrapper;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.tool.settings.SettingsPageParser;
import javafx.geometry.Point3D;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DesignLine extends DesignShape {

	public static final String POINT = "point";

	public DesignLine() {
		addModifyingKeys( ORIGIN, POINT );
	}

	public DesignLine( Point3D origin, Point3D point ) {
		this();
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
		map.put( SHAPE, "line" );
		map.putAll( asMap( POINT ) );
		return map;
	}

	public DesignLine updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setPoint( ParseUtil.parsePoint3D( (String)map.get( POINT ) ) );
		return this;
	}

	@Override
	public List<Shape> generateGeometry() {
		Line line = new Line( getOrigin().getX(), getOrigin().getY(), getPoint().getX(), getPoint().getY() );
		return List.of( line );
	}

	@Override
	public List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Line line = (Line)shapes.get( 0 );
		ConstructionPoint o = cp( pane, line.startXProperty(), line.startYProperty() );
		ConstructionPoint p = cp( pane, line.endXProperty(), line.endYProperty() );

		List<ConstructionPoint> cps = List.of( o, p );
		line.getProperties().put( CONSTRUCTION_POINTS, cps );
		return cps;
	}

	@Override
	public SettingsPage getPropertiesPage( ProgramProduct product ) throws IOException {
		String pagePath = "/com/avereon/cartesia/design/props/line.xml";
		if( page == null ) page = new SettingsPageParser( product, new NodeSettingsWrapper( this ) ).parse( pagePath, BundleKey.PROPS ).get( "line" );
		return page;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, POINT );
	}

}
