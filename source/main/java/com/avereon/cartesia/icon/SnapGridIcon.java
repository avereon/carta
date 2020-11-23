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
		if( enabled ) {
			draw( "M" + offset + " " + MIN + " L" + offset + " " + MAX, 2 );
			draw( "M" + MIN + " " + offset + " L" + MAX + " " + offset, 2 );
			fill( circle( C, C, RADIUS ) );
		} else {
			draw( "M" + offset + " " + MIN + " L" + offset + " " + MAX, null, 2, StrokeLineCap.ROUND, StrokeLineJoin.MITER, 0.5, 1, 4 );
			draw( "M" + MIN + " " + offset + " L" + MAX + " " + offset, null, 2, StrokeLineCap.ROUND, StrokeLineJoin.MITER, 0.5, 1, 4 );
			draw( circle( C, C, RADIUS + 1 ), null, 2, StrokeLineCap.ROUND, StrokeLineJoin.MITER, 0.125 * Math.PI, 0.25 * Math.PI, 1 * Math.PI );
		}
	}

	public static void main( String[] parameters ) {
		proof( new SnapGridIcon( false ) );
	}

}
