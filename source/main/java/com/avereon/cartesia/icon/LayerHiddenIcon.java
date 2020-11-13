package com.avereon.cartesia.icon;

import com.avereon.zerra.color.Colors;
import com.avereon.zerra.image.Proof;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class LayerHiddenIcon extends LayerIcon {

	private final double r = 0.0625 * Math.sqrt( R * R + R * R );

	@Override
	protected void doRender() {
		fill( getPath(), Colors.mix( (Color)getStrokePaint(), Color.TRANSPARENT, 0.5 ) );
		draw( getPath(), null, 2, StrokeLineCap.BUTT, StrokeLineJoin.MITER, 2 * r, 4 * r, 4 * r );
		//draw( getPath(), null, 2, StrokeLineCap.BUTT, StrokeLineJoin.MITER, 4 * r, 8 * r, 2 * r, 4 * r, 2 * r );
		//draw( getPath(), null, 2, StrokeLineCap.BUTT, StrokeLineJoin.MITER, 3 * r, 6 * r, 3 * r, 4 * r, 3 * r );
		super.doRender();
	}

	public static void main( String[] commands ) {
		Proof.proof( new LayerHiddenIcon() );
	}

}
