package com.avereon.cartesia.data;

import javafx.scene.paint.Color;

import java.util.Map;

public abstract class DesignDraw extends DesignNode {

	public static final String DRAW_WIDTH = "draw-width";

	public static final String DRAW_COLOR = "draw-color";

	public static final String FILL_COLOR = "fill-color";

	protected DesignDraw() {
		addModifyingKeys( DRAW_WIDTH, DRAW_COLOR, FILL_COLOR );
	}

	public double getDrawWidth() {
		return getValue( DRAW_WIDTH );
	}

	public DesignDraw setDrawWidth( double width ) {
		setValue( DRAW_WIDTH, width );
		return this;
	}

	public Color getDrawColor() {
		return getValue( DRAW_COLOR );
	}

	public DesignDraw setDrawColor( Color color ) {
		setValue( DRAW_COLOR, color );
		return this;
	}

	public Color getFillColor() {
		return getValue( FILL_COLOR );
	}

	public DesignDraw setFillColor( Color color ) {
		setValue( FILL_COLOR, color );
		return this;
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( DRAW_WIDTH, DRAW_COLOR, FILL_COLOR ) );
		return map;
	}

	public DesignDraw updateFrom( Map<String, String> map ) {
		super.updateFrom( map );
		if( map.containsKey( DRAW_WIDTH ) ) setDrawWidth( Integer.parseInt( map.get( DRAW_WIDTH ) ) );
		if( map.containsKey( DRAW_COLOR ) ) setDrawColor( Color.web( map.get( DRAW_COLOR ) ) );
		if( map.containsKey( FILL_COLOR ) ) setFillColor( Color.web( map.get( FILL_COLOR ) ) );
		return this;
	}

}
