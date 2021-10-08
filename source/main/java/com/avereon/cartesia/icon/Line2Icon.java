package com.avereon.cartesia.icon;

import com.avereon.zarra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class Line2Icon extends DrawIcon {

	@Override
	protected void doRender() {
		draw( "M4,28L28,4", null, getLineWidth(), StrokeLineCap.BUTT, StrokeLineJoin.MITER );
		fill( circle( 4, 28, getDotRadius() ) );
		fill( circle( 28, 4, getDotRadius() ) );
		super.doRender();
	}

	public static void main( String[] commands ) {
		Proof.proof( new Line2Icon() );
	}

}
