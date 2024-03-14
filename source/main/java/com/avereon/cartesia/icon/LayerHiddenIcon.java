package com.avereon.cartesia.icon;

import com.avereon.zarra.color.Colors;
import com.avereon.zarra.image.Proof;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class LayerHiddenIcon extends LayerIcon {

	private final double r = 0.0625 * Math.sqrt( R * R + R * R );

	protected void define() {
		fill( getPath(), Colors.mix( (Color)getStrokePaint(), Color.TRANSPARENT, 0.2 ) );
		draw( getPath(), null, 2, StrokeLineCap.BUTT, StrokeLineJoin.MITER, 2 * r, 4 * r, 4 * r );
	}

	public static void main( String[] commands ) {
		Proof.proof( new LayerHiddenIcon() );
	}

}
