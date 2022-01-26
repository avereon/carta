package com.avereon.cartesia.icon;

import com.avereon.zarra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class Arc2Icon extends DrawIcon {

	public Arc2Icon() {
		draw( "M4,28 A24,24,0,0,1,28,4", null, getLineWidth(), StrokeLineCap.BUTT, StrokeLineJoin.MITER );
		fill( circle( 4, 28, getDotRadius() ) );
		fill( circle( 28, 4, getDotRadius() ) );
		fill( circle( 28, 28, getDotRadius() ) );
	}

	public static void main( String[] commands ) {
		Proof.proof( new Arc2Icon() );
	}

}
