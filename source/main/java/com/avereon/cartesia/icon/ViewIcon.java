package com.avereon.cartesia.icon;

import com.avereon.zerra.image.Proof;
import com.avereon.zerra.image.SvgIcon;

public class ViewIcon extends SvgIcon {

	public ViewIcon() {
		super( 32, 32 );
	}

	@Override
	protected void doRender() {
		fill( circle( 16, 16, 3 ) );
		draw( circle( 16, 16, 6 ), 0.25 * getStrokeWidth() );
		draw( "M4,16A15,15,0,0,1,28,16", 0.25 * getStrokeWidth() );
		draw( "M4,16A15,15,0,0,0,28,16", 0.25 * getStrokeWidth() );
		super.doRender();
	}

	public static void main( String[] commands ) {
		Proof.proof( new ViewIcon() );
	}

}
