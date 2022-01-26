package com.avereon.cartesia.icon;

import com.avereon.zarra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class LayerVisibleIcon extends LayerIcon {

	public LayerVisibleIcon() {
		fill( getPath() );
		draw( getPath(), null, 2, StrokeLineCap.BUTT, StrokeLineJoin.MITER );
	}

	public static void main( String[] commands ) {
		Proof.proof( new LayerVisibleIcon() );
	}

}
