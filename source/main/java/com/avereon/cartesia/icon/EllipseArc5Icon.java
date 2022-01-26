package com.avereon.cartesia.icon;

import com.avereon.zarra.image.Proof;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class EllipseArc5Icon extends DrawIcon {

	public EllipseArc5Icon() {
		double r = 16;
		double p = 10;
		double g = Math.sqrt( 0.5 * (r * r) );
		double h = Math.sqrt( 0.5 * (p * p) );

		String a = rotate( 16 - r, 16, 16, 16, 45 );
		String b = rotate( 16 + r, 16, 16, 16, 45 );
		String c = (16 - g) + "," + 16;
		String d = 16 + "," + (16 - g);

		String pathA = "M" + c + " A" + p + "," + r + ",45,1,0," + d;
		String pathB = "M" + d + " A" + p + "," + r + ",45,0,0," + c;

		double factor = 1.0075;

		draw( pathA, null, getLineWidth(), StrokeLineCap.ROUND, StrokeLineJoin.MITER, factor, 0, 2 * factor * getLineWidth() );
		draw( pathB, null, getLineWidth(), StrokeLineCap.ROUND, StrokeLineJoin.MITER );
		//draw( pathC, null, getLineWidth(), StrokeLineCap.ROUND, StrokeLineJoin.MITER, factor * getLineWidth(), 0, 2 * factor * getLineWidth() );
//		fill( circle( 16, 16, 0.75*getDotRadius() ) );
//		fill( circle( 16 + g, 16 - g, 0.75*getDotRadius() ) );
//		fill( circle( 16 + h, 16 + h, 0.75*getDotRadius() ) );

		fill( circle( 16, 16 - g, getDotRadius() ) );
		fill( circle( 16 - g, 16, getDotRadius() ) );
	}

	public static void main( String[] commands ) {
		Proof.proof( new EllipseArc5Icon() );
	}

}
