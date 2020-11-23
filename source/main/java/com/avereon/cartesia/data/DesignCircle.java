package com.avereon.cartesia.data;

import com.avereon.cartesia.BundleKey;
import com.avereon.data.NodeSettingsWrapper;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.tool.settings.SettingsPageParser;
import javafx.geometry.Point3D;

import java.io.IOException;
import java.util.Map;

public class DesignCircle extends DesignShape {

	public static final String CIRCLE = "circle";

	public static final String RADIUS = "radius";

	public DesignCircle() {
		addModifyingKeys( ORIGIN, RADIUS );
	}

	public DesignCircle( Point3D origin, Double radius ) {
		this();
		setOrigin( origin );
		setRadius( radius );
	}

	public Double getRadius() {
		return getValue( RADIUS );
	}

	public DesignShape setRadius( Double point ) {
		setValue( RADIUS, point );
		return this;
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, CIRCLE );
		map.putAll( asMap( RADIUS ) );
		return map;
	}

	public DesignCircle updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setRadius( (Double)map.get( RADIUS ) );
		return this;
	}

	@Override
	public SettingsPage getPropertiesPage( ProgramProduct product ) throws IOException {
		String pagePath = "/com/avereon/cartesia/design/props/circle.xml";
		if( page == null ) page = new SettingsPageParser( product, new NodeSettingsWrapper( this ) ).parse( pagePath, BundleKey.PROPS ).get( CIRCLE );
		return page;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, RADIUS );
	}

}
