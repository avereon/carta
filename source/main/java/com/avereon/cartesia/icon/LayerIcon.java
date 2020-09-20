package com.avereon.cartesia.icon;

import com.avereon.zerra.image.Proof;
import com.avereon.zerra.image.SvgIcon;

public class LayerIcon extends SvgIcon {

	private static final double C = 16;

	private static final double N = 10;

	private static final double S = 22;

	private static final double W = 4;

	private static final double E = 28;

	private static final double N2 = 14.5;

	private static final double S2 = 19;

	private static final double W2 = 7;

	private static final double E2 = 25;

	public LayerIcon() {
		this( 0 );
	}

	protected LayerIcon( double y ) {
		super( 32, 32 );
		add( upperLayer( y ) );
	}

	protected String upperLayer( double y ) {
		String layer = "M" + W + "," + (C + y);
		layer += "L" + C + "," + (S + y);
		layer += "L" + E + "," + (C + y);
		layer += "L" + C + "," + (N + y);
		layer += "Z";
		return layer;
	}

	protected String lowerLayer( double y ) {
		String layer = "M" + W + "," + (C + y);
		layer += "L" + C + "," + (S + y);
		layer += "L" + E + "," + (C + y);

		layer += "L" + E2 + "," + (N2 + y);
		layer += "L" + C + "," + (S2 + y);
		layer += "L" + W2 + "," + (N2 + y);

		layer += "Z";
		return layer;
	}

	public static void main( String[] commands ) {
		Proof.proof( new LayerIcon() );
	}

}
