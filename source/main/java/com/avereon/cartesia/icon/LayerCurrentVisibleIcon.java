package com.avereon.cartesia.icon;

import com.avereon.zerra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class LayerCurrentVisibleIcon extends LayerVisibleIcon {

	protected void define() {
		super.define();
		draw( getCurrentPath(), null, 2, StrokeLineCap.BUTT, StrokeLineJoin.MITER );
	}

	public static void main( String[] commands ) {
		Proof.proof( new LayerCurrentVisibleIcon() );
	}

}
