package com.avereon.cartesia.icon;

import com.avereon.zerra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class LinePerpendicularIcon extends DrawIcon {

	private static final double R = 12;
	private static final double G = Math.sqrt( 0.5 * (R * R) );

	protected void define() {
		super.define();
		draw( "M4,28L28,4", null, getLineWidth(), StrokeLineCap.ROUND, StrokeLineJoin.MITER, G / 1.5, 0, G / 1.5 );
		draw( "M16,16L28,28", null, getLineWidth(), StrokeLineCap.BUTT, StrokeLineJoin.MITER );
		fill( circle( 4, 28, getDotRadius() ) );
		fill( circle( 28, 4, getDotRadius() ) );
		fill( circle( 16, 16, getDotRadius() ) );
		fill( circle( 28, 28, getDotRadius() ) );
	}

	public static void main( String[] commands ) {
		Proof.proof( new LinePerpendicularIcon() );
	}

}
