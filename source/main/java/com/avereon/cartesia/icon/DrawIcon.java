package com.avereon.cartesia.icon;

import com.avereon.zarra.image.SvgIcon;

public abstract class DrawIcon extends SvgIcon {

	private static final double LINE_WIDTH = 2;

	private static final double DOT_RADIUS = 3;

	protected static double getLineWidth() {
		return LINE_WIDTH;
	}

	protected double getDotRadius() {
		return DOT_RADIUS;
	}

}
