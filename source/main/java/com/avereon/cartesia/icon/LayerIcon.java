package com.avereon.cartesia.icon;

import com.avereon.zerra.image.Proof;
import com.avereon.zerra.image.SvgIcon;

public class LayerIcon extends SvgIcon {

	protected static final double C = 16;

	protected static final double R = 14;

	protected static final double Q = R - Math.sqrt( 8 );

	public LayerIcon() {
		super( 32, 32 );
	}

	protected String getPath() {
		String path = "M" + C + "," + (C - R);
		path += "L" + (C - R) + "," + C;
		path += "L" + C + "," + (C + R);
		path += "L" + (C + R) + "," + C;
		path += "Z";
		return path;
	}

	public static void main( String[] commands ) {
		Proof.proof( new LayerIcon() );
	}

}
