package com.avereon.cartesia.icon;

import com.avereon.zarra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class Curve3Icon extends DrawIcon {

	protected void define() {
		super.define();
		double factor = 1.032;
		draw( "M4,28L16,4,28,28", null, getLineWidth(), StrokeLineCap.ROUND, StrokeLineJoin.MITER, factor*getLineWidth(), 0, 2 * factor * getLineWidth() );
		draw( "M4,28Q16,4,28,28", null, getLineWidth(), StrokeLineCap.BUTT, StrokeLineJoin.MITER );
		fill( circle( 4, 28, getDotRadius() ) );
		fill( circle( 16, 4, getDotRadius() ) );
		fill( circle( 28, 28, getDotRadius() ) );
	}

	public static void main( String[] commands ) {
		Proof.proof( new Curve3Icon() );
	}

}
