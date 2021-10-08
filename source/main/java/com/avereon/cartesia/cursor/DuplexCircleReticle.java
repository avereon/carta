package com.avereon.cartesia.cursor;

import com.avereon.zarra.image.Proof;
import com.avereon.zarra.image.SvgIcon;

public class DuplexCircleReticle extends DuplexReticle {

	public DuplexCircleReticle() {}

	public DuplexCircleReticle( double percent ) {
		super( percent );
	}

	public DuplexCircleReticle( double percent, double width, double height ) {
		super( percent, width, height );
	}

	@Override
	protected void render() {
		super.render();
		double circleR = Math.round( getC() - getR() ) - 0.5;
		startPath();
		getGraphicsContext2D().appendSVGPath( SvgIcon.circle( getC() - 0.5, getC() - 0.5, circleR ) );
		setStrokeWidth( 1.5 );
		draw();
	}

	public static void main( String[] commands ) {
		Proof.proof( new DuplexCircleReticle( 0.8, 64, 64 ) );
	}

}
