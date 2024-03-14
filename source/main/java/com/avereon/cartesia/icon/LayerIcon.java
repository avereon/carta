package com.avereon.cartesia.icon;

import com.avereon.zarra.image.SvgIcon;

public abstract class LayerIcon extends SvgIcon {

	protected static final double C = 16;

	protected static final double R = 14;

	protected static final double R2 = 6;

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

	protected String getCurrentPath() {
//		String path = "M" + C + "," + (C - R2);
//		path += "L" + (C - R2) + "," + C;
//		path += "L" + C + "," + (C + R2);
//		path += "L" + (C + R2) + "," + C;
//		path += "Z";
//		return path;
		return circle( C, C, R2 );
	}

}
