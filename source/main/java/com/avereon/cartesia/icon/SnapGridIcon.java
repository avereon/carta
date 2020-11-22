package com.avereon.cartesia.icon;

import com.avereon.zerra.image.SvgIcon;

public class SnapGridIcon extends SvgIcon {

	private static final double C = 16;

	private static final double GRID = 2 * C;

	private static final double PADDING = 1;

	private static final double SPACING = 10;

	private static final double INDENT = C - SPACING;

	private static final double MIN = PADDING;

	private static final double MAX = GRID - PADDING;

	private static final double RADIUS = 4;

	public SnapGridIcon() {
		this( true );
	}

	public SnapGridIcon( boolean enabled ) {
		super( GRID, GRID );
		for( int index = 0; index < 3; index++ ) {
			double offset = INDENT + SPACING * index;
			draw( "M" + offset + " " + MIN + " L" + offset + " " + MAX, 2 );
			draw( "M" + MIN + " " + offset + " L" + MAX + " " + offset, 2 );
		}
		if( enabled ) {
			fill( circle( C, C, RADIUS ) );
		} else {
			draw( circle( C, C, RADIUS + 2 ), 1 );
		}
	}

	public static void main( String[] parameters ) {
		proof( new SnapGridIcon( false ) );
	}

}
