package com.avereon.cartesia.icon;

import com.avereon.zarra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class MarkerIcon extends DrawIcon {

	protected void define() {
		super.define();
		draw( "M8,16 L24,16 M16,8 L16,24", null, getStrokeWidth(), StrokeLineCap.BUTT, StrokeLineJoin.MITER );
	}

	public static void main( String[] commands ) {
		Proof.proof( new MarkerIcon() );
	}


}
