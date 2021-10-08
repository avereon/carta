package com.avereon.cartesia.icon;

import com.avereon.zarra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class LayerVisibleIcon extends LayerIcon {

	@Override
	protected void doRender() {
		fill( getPath() );
		draw( getPath(), null, 2, StrokeLineCap.BUTT, StrokeLineJoin.MITER );
		super.doRender();
	}

	public static void main( String[] commands ) {
		Proof.proof( new LayerVisibleIcon() );
	}

}
