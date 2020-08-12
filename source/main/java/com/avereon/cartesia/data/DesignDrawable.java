package com.avereon.cartesia.data;

import javafx.scene.paint.Color;

import java.util.Map;

public abstract class DesignDrawable extends DesignNode {

	public static final String ORDER = "order";

	public static final String DRAW_WIDTH = "draw-width";

	public static final String DRAW_COLOR = "draw-color";

	public static final String FILL_COLOR = "fill-color";

	private static final double DEFAULT_DRAW_WIDTH = 1.0;

	private static final Color DEFAULT_DRAW_COLOR = Color.web( "0x000000ff");

	private static final Color DEFAULT_FILL_COLOR = Color.web( "0x202030ff");

	protected DesignDrawable() {
		addModifyingKeys( DRAW_WIDTH, DRAW_COLOR, FILL_COLOR );
	}

	public int getOrder() {
		return getValue( ORDER, 0 );
	}

	public DesignDrawable setOrder( int order ) {
		setValue( ORDER, order );
		return this;
	}

	public Double getDrawWidth() {
		Double width = getValue( DRAW_COLOR );
		if( width != null ) return width;
		if( this instanceof DesignLayer) return DEFAULT_DRAW_WIDTH;
		DesignNode parent = getParent();
		if( parent instanceof DesignLayer ) width = ((DesignLayer)parent).getDrawWidth();
		return width;
	}

	public DesignDrawable setDrawWidth( double width ) {
		setValue( DRAW_WIDTH, width );
		return this;
	}

	public Color getDrawColor() {
		Color color = getValue( DRAW_COLOR );
		if( color != null ) return color;
		if( this instanceof DesignLayer) return DEFAULT_DRAW_COLOR;
		DesignNode parent = getParent();
		if( parent instanceof DesignLayer ) color = ((DesignLayer)parent).getDrawColor();
		return color;
	}

	public DesignDrawable setDrawColor( Color color ) {
		setValue( DRAW_COLOR, color );
		return this;
	}

	public Color getFillColor() {
		Color color = getValue( FILL_COLOR );
		if( color != null ) return color;
		if( this instanceof DesignLayer) return DEFAULT_FILL_COLOR;
		DesignNode parent = getParent();
		if( parent instanceof DesignLayer ) color = ((DesignLayer)parent).getFillColor();
		return color;
	}

	public DesignDrawable setFillColor( Color color ) {
		setValue( FILL_COLOR, color );
		return this;
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( ORDER, DRAW_WIDTH, DRAW_COLOR, FILL_COLOR ) );
		return map;
	}

	public DesignDrawable updateFrom( Map<String, String> map ) {
		super.updateFrom( map );
		if( map.containsKey( ORDER ) ) setOrder( Integer.parseInt( map.get( ORDER ) ) );
		if( map.containsKey( DRAW_WIDTH ) ) setDrawWidth( Integer.parseInt( map.get( DRAW_WIDTH ) ) );
		if( map.containsKey( DRAW_COLOR ) ) setDrawColor( Color.web( map.get( DRAW_COLOR ) ) );
		if( map.containsKey( FILL_COLOR ) ) setFillColor( Color.web( map.get( FILL_COLOR ) ) );
		return this;
	}

}
