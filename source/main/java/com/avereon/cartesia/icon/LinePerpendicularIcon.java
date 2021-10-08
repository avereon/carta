package com.avereon.cartesia.icon;

import com.avereon.zarra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class LinePerpendicularIcon extends DrawIcon {

	@Override
	protected void doRender() {
		double r = 12;
		double g = Math.sqrt( 0.5 * (r * r) );

		draw( "M4,28L28,4", null, getLineWidth(), StrokeLineCap.ROUND, StrokeLineJoin.MITER, g / 1.5, 0, g / 1.5 );
		draw( "M16,16L28,28", null, getLineWidth(), StrokeLineCap.BUTT, StrokeLineJoin.MITER );
				fill( circle( 4, 28, getDotRadius() ) );
				fill( circle( 28, 4, getDotRadius() ) );
				fill( circle( 16, 16, getDotRadius() ) );
				fill( circle( 28, 28, getDotRadius() ) );
		super.doRender();
	}

	public static void main( String[] commands ) {
		Proof.proof( new LinePerpendicularIcon() );
	}

}
