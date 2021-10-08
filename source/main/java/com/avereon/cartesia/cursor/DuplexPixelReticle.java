package com.avereon.cartesia.cursor;

import com.avereon.zarra.image.Proof;
import com.avereon.zarra.image.SvgIcon;

public class DuplexPixelReticle extends DuplexReticle {

	public DuplexPixelReticle() {}

	public DuplexPixelReticle( double percent ) {
		super( percent );
	}

	public DuplexPixelReticle( double percent, double width, double height ) {
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
		Proof.proof( new DuplexPixelReticle( 0.8, 64, 64 ) );
	}

}
