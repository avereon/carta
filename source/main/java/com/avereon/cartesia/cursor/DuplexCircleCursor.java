package com.avereon.cartesia.cursor;

import com.avereon.zerra.image.Proof;
import com.avereon.zerra.image.SvgIcon;

public class DuplexCircleCursor extends DuplexCursor {

	public DuplexCircleCursor() {}

	public DuplexCircleCursor( double percent ) {
		super( percent );
	}

	public DuplexCircleCursor( double percent, double width, double height ) {
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
		Proof.proof( new DuplexCircleCursor( 0.8, 64, 64 ) );
	}

}
