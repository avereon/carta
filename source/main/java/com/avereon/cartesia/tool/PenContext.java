package com.avereon.cartesia.tool;

// FIXME Some of this exists in CommandContext already
public class PenContext {

	public static final Object DESIGN_DRAW_COLOR = new Object();

	public static final Object DESIGN_DRAW_WIDTH = new Object();

	public static final Object DESIGN_FILL_COLOR = new Object();

	public static final Object LAYER_DRAW_COLOR = new Object();

	public static final Object LAYER_DRAW_WIDTH = new Object();

	public static final Object LAYER_FILL_COLOR = new Object();

	private Object drawColor = DESIGN_DRAW_COLOR;

	private Object drawWidth = DESIGN_DRAW_WIDTH;

	private Object fillColor = DESIGN_FILL_COLOR;

	public String getDrawColor() {
		return "";
	}

	public PenContext setDrawColor( String color ) {
		return this;
	}

	public String getDrawWidth() {
		return "";
	}

	public PenContext setDrawWidth( String expression ) {
		return this;
	}

	public String getFillColor() {
		return "";
	}

	public PenContext setFillColor( String color ) {
		return this;
	}

}
