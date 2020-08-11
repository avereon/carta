package com.avereon.cartesia.data;

import javafx.scene.paint.Color;

import java.util.Map;

public abstract class DesignDrawable extends DesignNode {

	public static final String ORDER = "order";

	public static final String DRAW_WIDTH = "draw-width";

	public static final String DRAW_COLOR = "draw-color";

	public static final String FILL_COLOR = "fill-color";

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

	public double getDrawWidth() {
		return getValue( DRAW_WIDTH );
	}

	public DesignDrawable setDrawWidth( double width ) {
		setValue( DRAW_WIDTH, width );
		return this;
	}

	public Color getDrawColor() {
		return getValue( DRAW_COLOR );
	}

	public DesignDrawable setDrawColor( Color color ) {
		setValue( DRAW_COLOR, color );
		return this;
	}

	public Color getFillColor() {
		return getValue( FILL_COLOR );
	}

	public DesignDrawable setFillColor( Color color ) {
		setValue( FILL_COLOR, color );
		return this;
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap(ORDER, DRAW_WIDTH, DRAW_COLOR, FILL_COLOR ) );
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
