package com.avereon.cartesia.icon;

import com.avereon.zarra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class Arc3Icon extends DrawIcon {

	public Arc3Icon() {
		double r = 24;
		double g = Math.sqrt( 0.5 * (r * r) );

		draw( "M4,28 A24,24,0,0,1,28,4", null, getLineWidth(), StrokeLineCap.BUTT, StrokeLineJoin.MITER );
		fill( circle( 4, 28, getDotRadius() ) );
		fill( circle( 28, 4, getDotRadius() ) );
		fill( circle( 28 - g, 28 - g, getDotRadius() ) );
	}

	public static void main( String[] commands ) {
		Proof.proof( new Arc3Icon() );
	}

}
