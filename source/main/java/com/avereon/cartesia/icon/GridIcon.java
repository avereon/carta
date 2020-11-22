package com.avereon.cartesia.icon;

import com.avereon.zerra.image.SvgIcon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class GridIcon extends SvgIcon {

	private static final double C = 16;

	private static final double GRID = 2 * C;

	private static final double PADDING = 1;

	private static final double SPACING = 10;

	private static final double INDENT = C - SPACING;

	private static final double MIN = PADDING;

	private static final double MAX = GRID - PADDING;

	private static final double RADIUS = 4;

	public GridIcon() {
		this( true );
	}

	public GridIcon( boolean enabled ) {
		super( GRID, GRID );
		for( int index = 0; index < 3; index++ ) {
			double offset = INDENT + SPACING * index;
			if( enabled ) {
				draw( "M" + offset + " " + MIN + " L" + offset + " " + MAX, 2 );
				draw( "M" + MIN + " " + offset + " L" + MAX + " " + offset, 2 );
			} else {
				draw( "M" + offset + " " + MIN + " L" + offset + " " + MAX, null, 2, StrokeLineCap.ROUND, StrokeLineJoin.MITER, 0.5, 1, 4 );
				draw( "M" + MIN + " " + offset + " L" + MAX + " " + offset, null, 2, StrokeLineCap.ROUND, StrokeLineJoin.MITER, 0.5, 1, 4 );
			}
		}
	}

	public static void main( String[] parameters ) {
		proof( new GridIcon( false ) );
	}

}
