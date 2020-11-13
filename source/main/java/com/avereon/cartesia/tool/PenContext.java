package com.avereon.cartesia.tool;

import javafx.scene.paint.Color;

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

	public Color calcDrawColor() {
		// FIXME Not sure I can get the layer or design color from here without some context
		return Color.MAGENTA;
	}

	public String getDrawColor() {
		return "";
	}

	public PenContext setDrawColor( String color ) {
		return this;
	}

	public double calcDrawWidth() {
		// FIXME Not sure I can get the layer or design width from here without some context
		return 1;
	}

	public String getDrawWidth() {
		return "";
	}

	public PenContext setDrawWidth( String expression ) {
		return this;
	}

	public Color calcFillColor() {
		// FIXME Not sure I can get the layer or design color from here without some context
		return Color.MAGENTA;
	}

	public String getFillColor() {
		return "";
	}

	public PenContext setFillColor( String color ) {
		return this;
	}

}
