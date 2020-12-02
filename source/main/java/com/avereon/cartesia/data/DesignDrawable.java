package com.avereon.cartesia.data;

import com.avereon.cartesia.math.Maths;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.zerra.color.Paints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Map;

public abstract class DesignDrawable extends DesignNode {

	public static final String ORDER = "order";

	public static final String DRAW_WIDTH = "draw-width";

	public static final String DRAW_PAINT = "draw-paint";

	public static final String FILL_PAINT = "fill-paint";

	private static final double DEFAULT_DRAW_WIDTH = 0.05;

	private static final Color DEFAULT_DRAW_PAINT = Color.web( "0x000000ff" );

	private static final Color DEFAULT_FILL_PAINT = Color.web( "0x202030ff" );

	protected SettingsPage page;

	protected DesignDrawable() {
		addModifyingKeys( DRAW_WIDTH, DRAW_PAINT, FILL_PAINT );
	}

	public DesignLayer getParentLayer() {
		return getParent();
	}

	public int getOrder() {
		return getValue( ORDER, 0 );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setOrder( int order ) {
		setValue( ORDER, order );
		return (T)this;
	}

	public double calcDrawWidth() {
		String width = getDrawWidth();
		if( width != null ) return Maths.evalNoException( width );
		if( this instanceof DesignLayer ) return DEFAULT_DRAW_WIDTH;
		DesignNode parent = getParent();
		if( parent instanceof DesignLayer ) return ((DesignLayer)parent).calcDrawWidth();
		return Double.NaN;
	}

	public String getDrawWidth() {
		return getValue( DRAW_WIDTH );
	}

	public DesignDrawable setDrawWidth( String width ) {
		setValue( DRAW_WIDTH, width );
		return this;
	}

	public Paint calcDrawPaint() {
		String paint = getDrawPaint();
		if( paint != null ) return Paints.parse( paint );
		if( this instanceof DesignLayer ) return DEFAULT_DRAW_PAINT;
		DesignNode parent = getParent();
		if( parent instanceof DesignLayer ) return ((DesignLayer)parent).calcDrawPaint();
		return DEFAULT_DRAW_PAINT;
	}

	public String getDrawPaint() {
		return getValue( DRAW_PAINT );
	}

	public DesignDrawable setDrawPaint( String color ) {
		setValue( DRAW_PAINT, color );
		return this;
	}

	public Paint calcFillPaint() {
		String paint = getFillPaint();
		if( paint != null ) return Paints.parse( paint );
		if( this instanceof DesignLayer ) return DEFAULT_FILL_PAINT;
		DesignNode parent = getParent();
		if( parent instanceof DesignLayer ) return ((DesignLayer)parent).calcFillPaint();
		return DEFAULT_FILL_PAINT;
	}

	public String getFillPaint() {
		return getValue( FILL_PAINT );
	}

	public DesignDrawable setFillPaint( String color ) {
		setValue( FILL_PAINT, color );
		return this;
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public <T> T getValue( String key ) {
		// NOTE This is a bit of a workaround since "layer" is not a real value
		if( "layer".equals( key ) ) return (T)getParentLayer().getId();
		return super.getValue( key );
	}

	@Override
	public <T> T setValue( String key, T newValue ) {
		if( "layer".equals( key ) ) return changeLayer( newValue );
		return super.setValue( key, newValue );
	}

	private <T> T changeLayer( T newValue ) {
		String newLayerId = String.valueOf( newValue );
		if( getValue( "layer" ).equals( newLayerId ) ) return newValue;

		Design design = getDesign();
		DesignLayer newLayer = design.findLayerById( newLayerId );
		newLayer.addDrawable( getParentLayer().removeDrawable( this ) );

		return newValue;
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( ORDER, DRAW_WIDTH, DRAW_PAINT, FILL_PAINT ) );
		return map;
	}

	public DesignDrawable updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );

		// Old keys
		if( map.containsKey( "draw-color" ) ) setDrawPaint( (String)map.get( "draw-color" ) );
		if( map.containsKey( "fill-color" ) ) setFillPaint( (String)map.get( "fill-color" ) );

		if( map.containsKey( ORDER ) ) setOrder( (Integer)map.get( ORDER ) );
		if( map.containsKey( DRAW_WIDTH ) ) setDrawWidth( (String)map.get( DRAW_WIDTH ) );
		if( map.containsKey( DRAW_PAINT ) ) setDrawPaint( (String)map.get( DRAW_PAINT ) );
		if( map.containsKey( FILL_PAINT ) ) setFillPaint( (String)map.get( FILL_PAINT ) );
		return this;
	}

}
