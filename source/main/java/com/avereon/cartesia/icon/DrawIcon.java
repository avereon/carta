package com.avereon.cartesia.icon;

import com.avereon.zarra.image.SvgIcon;

public class DrawIcon extends SvgIcon {

	private static final double LINE_WIDTH = 2;

	private static final double DOT_RADIUS = 3;

	public DrawIcon() {
		super( 32, 32 );
	}

	protected static double getLineWidth() {
		return LINE_WIDTH;
	}

	protected double getDotRadius() {
		return DOT_RADIUS;
	}

}
