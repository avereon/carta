package com.avereon.cartesia.icon;

import com.avereon.zerra.image.SvgIcon;

public class SnapGridIcon extends SvgIcon {

	private static final double GRID = 32;

	private static final double PADDING = 1;

	private static final double SPACING = 10;

	private static final double INDENT = 16 - SPACING;

	private static final double MIN = PADDING;

	private static final double MAX = GRID - PADDING;

	public SnapGridIcon() {
		super( GRID, GRID );
		for( int index = 0; index < 3; index++ ) {
			double offset = INDENT + SPACING * index;
			draw( "M" + offset + " " + MIN + " L" + offset + " " + MAX, 2 );
			draw( "M" + MIN + " " + offset + " L" + MAX + " " + offset, 2 );
			fill( circle( 16, 16, 6 ) );
		}
	}

	public static void main( String[] parameters ) {
		proof( new SnapGridIcon() );
	}

}
