package com.avereon.cartesia.geometry;

import com.avereon.data.Node;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

public abstract class CsaShape extends Node {

	public static final String DRAW_COLOR = "draw-color";

	public static final String DRAW_WIDTH = "draw-width";

	public static final String ORIGIN = "origin";

	public CsaShape() {
		addModifyingKeys( DRAW_COLOR, DRAW_WIDTH, ORIGIN );
	}

	public Color getDrawColor() {
		return getValue( DRAW_COLOR );
	}

	public CsaShape setDrawColor( Color color ) {
		setValue( DRAW_COLOR, color );
		return this;
	}

	public double getDrawWidth() {
		return getValue( DRAW_WIDTH );
	}

	public CsaShape setDrawWidth( double width ) {
		setValue( DRAW_WIDTH, width );
		return this;
	}

	public Point3D getOrigin() {
		return getValue( ORIGIN );
	}

	public CsaShape setOrigin( Point3D origin ) {
		setValue( ORIGIN, origin );
		return this;
	}

}
