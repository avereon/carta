package com.avereon.cartesia.icon;

import com.avereon.zerra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class PathIcon extends DrawIcon {

	protected void define() {
		super.define();
		draw( "M6,6 L22,6 A4,4,0,0,1,22,16 L10,16 A4,4,0,0,0,10,26 L26,26", null, getLineWidth(), StrokeLineCap.BUTT, StrokeLineJoin.MITER );
		fill( circle( 6, 6, getDotRadius() ) );
		//		fill( circle( 16, 4, getDotRadius() ) );
		fill( circle( 26, 26, getDotRadius() ) );
	}

	public static void main( String[] commands ) {
		Proof.proof( new PathIcon() );
	}

}
