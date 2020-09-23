package com.avereon.cartesia.icon;

import com.avereon.zerra.image.Proof;

public class LayerHiddenIcon extends LayerIcon {

	public LayerHiddenIcon() {}

	@Override
	protected String getPath() {
		String inner = super.getPath() + "M" + C + "," + (C - Q);
		inner += "L" + (C + Q) + "," + C;
		inner += "L" + C + "," + (C + Q);
		inner += "L" + (C - Q) + "," + C;
		inner += "Z";
		return inner;
	}

	public static void main( String[] commands ) {
		Proof.proof( new LayerHiddenIcon() );
	}

}
