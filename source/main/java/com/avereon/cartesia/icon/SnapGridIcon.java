package com.avereon.cartesia.icon;

import com.avereon.zerra.image.SvgIcon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class SnapGridIcon extends SvgIcon implements GridIconConstants {

	private static final double RADIUS = 4;

	public SnapGridIcon() {
		this( true );
	}

	public SnapGridIcon( boolean enabled ) {
		super( GRID, GRID );
		double offset = C;
		//for( int index = 0; index < 3; index++ ) {
			//double offset = INDENT + SPACING * index;
			if( enabled ) {
				draw( "M" + offset + " " + MIN + " L" + offset + " " + MAX, 2 );
				draw( "M" + MIN + " " + offset + " L" + MAX + " " + offset, 2 );
			} else {
				draw( "M" + offset + " " + MIN + " L" + offset + " " + MAX, null, 2, StrokeLineCap.ROUND, StrokeLineJoin.MITER, 0.5, 1, 4 );
				draw( "M" + MIN + " " + offset + " L" + MAX + " " + offset, null, 2, StrokeLineCap.ROUND, StrokeLineJoin.MITER, 0.5, 1, 4 );
			}
		//}
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
