package com.avereon.cartesia.icon;

import com.avereon.zerra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class Circle3Icon extends DrawIcon {

	protected void define() {
		super.define();
		double r = 12;
		double g = Math.sqrt( 0.5 * (r * r) );
		draw( "M4,16 A12,12,0,0,0,28,16 A12,12,0,0,0,4,16Z", null, getLineWidth(), StrokeLineCap.BUTT, StrokeLineJoin.MITER );
		fill( circle( 16 - g, 16 - g, getDotRadius() ) );
		fill( circle( 16 - g, 16 + g, getDotRadius() ) );
		fill( circle( 28, 16, getDotRadius() ) );
	}

	public static void main( String[] commands ) {
		Proof.proof( new Circle3Icon() );
	}
}
