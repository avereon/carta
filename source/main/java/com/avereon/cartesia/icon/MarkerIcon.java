package com.avereon.cartesia.icon;

import com.avereon.zerra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class MarkerIcon extends DrawIcon {

	@Override
	protected void doRender() {
		draw( "M8,16 L24,16 M16,8 L16,24", null, getStrokeWidth(), StrokeLineCap.BUTT, StrokeLineJoin.MITER );
		super.doRender();
	}

	public static void main( String[] commands ) {
		Proof.proof( new MarkerIcon() );
	}


}
