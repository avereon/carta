package com.avereon.cartesia.icon;

import com.avereon.zarra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class Ellipse3Icon extends DrawIcon {

	public Ellipse3Icon() {
		double r = 16;
		double p = 10;
		double g = Math.sqrt( 0.5 * (r * r) );
		double h = Math.sqrt( 0.5 * (p * p) );

		String a = rotate( 16 - r, 16, 16, 16, 45 );
		String b = rotate( 16 + r, 16, 16, 16, 45 );

		String path = "M" + a + " A" + p + "," + r + ",45,0,0," + b + " A" + p + "," + r + ",45,0,0," + a + "Z";

		draw( path, null, getLineWidth(), StrokeLineCap.ROUND, StrokeLineJoin.MITER );
		fill( circle( 16, 16, getDotRadius() ) );
		fill( circle( 16 + g, 16 - g, getDotRadius() ) );
		fill( circle( 16 - h, 16 - h, getDotRadius() ) );
	}

	public static void main( String[] commands ) {
		Proof.proof( new Ellipse3Icon() );
	}

}
