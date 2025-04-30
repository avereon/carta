package com.avereon.cartesia.icon;

import com.avereon.zerra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class Curve4Icon extends DrawIcon {

	protected void define() {
		super.define();
		draw( "M4,28L4,4,28,4,28,28", null, getLineWidth(), StrokeLineCap.ROUND, StrokeLineJoin.MITER, getLineWidth(), 0, 2 * getLineWidth() );
		draw( "M4,28C4,4,28,4,28,28", null, getLineWidth(), StrokeLineCap.BUTT, StrokeLineJoin.MITER );
		fill( circle( 4, 28, getDotRadius() ) );
		fill( circle( 4, 4, getDotRadius() ) );
		fill( circle( 28, 4, getDotRadius() ) );
		fill( circle( 28, 28, getDotRadius() ) );
	}

	public static void main( String[] commands ) {
		Proof.proof( new Curve4Icon() );
	}

}
