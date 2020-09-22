package com.avereon.cartesia.icon;

import com.avereon.zerra.image.Proof;
import com.avereon.zerra.image.SvgIcon;

public class LayerIcon extends SvgIcon {

	private static final double C = 16;

	private static final double Z = 12;

	public LayerIcon() {
		super( 32, 32 );
		String path = "M" + C + "," + (C - Z);
		path += "L" + (C - Z) + "," + C;
		path += "L" + C + "," + (C + Z);
		path += "L" + (C + Z) + "," + C;
		path += "Z";
		add( path );
	}

	public static void main( String[] commands ) {
		Proof.proof( new LayerIcon() );
	}

}
