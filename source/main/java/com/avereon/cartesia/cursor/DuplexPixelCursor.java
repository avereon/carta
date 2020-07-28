package com.avereon.cartesia.cursor;

import com.avereon.zerra.image.Proof;
import com.avereon.zerra.image.SvgIcon;

public class DuplexPixelCursor extends DuplexCursor {

	public DuplexPixelCursor() {}

	public DuplexPixelCursor( double percent ) {
		super( percent );
	}

	public DuplexPixelCursor( double percent, double width, double height ) {
		super( percent, width, height );
	}

	@Override
	protected void render() {
		super.render();
		startPath();
		getGraphicsContext2D().appendSVGPath( SvgIcon.circle( getC() - 0.5, getC() - 0.5, 1 ) );
		fill();
	}

	public static void main( String[] commands ) {
		Proof.proof( new DuplexPixelCursor( 0.8, 64, 64 ) );
	}

}
