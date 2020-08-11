package com.avereon.cartesia.data;

import com.avereon.data.IdNode;
import javafx.scene.paint.Color;

import java.util.Map;

public abstract class DesignDraw extends IdNode {

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

	public Map<String, String> asMap() {
		return asMap( ID, DRAW_WIDTH, DRAW_COLOR, FILL_COLOR );
	}

}
