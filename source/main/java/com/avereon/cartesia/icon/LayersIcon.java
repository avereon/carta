package com.avereon.cartesia.icon;

import com.avereon.zarra.image.Proof;
import com.avereon.zarra.image.SvgIcon;

public class LayersIcon extends SvgIcon {

	private static final double H = 12;

	private static final double V = 6;

	private static final double C = 16;

	private static final double N = C - V;

	private static final double S = C + V;

	private static final double W = C - H;

	private static final double E = C + H;

	private static final double OFFSET = 3;

	private static final double N2 = N + 1.5 * OFFSET;

	private static final double S2 = S - OFFSET;

	private static final double W2 = W + OFFSET;

	private static final double E2 = E - OFFSET;

	public LayersIcon() {
		super( 32, 32 );
		fill( upperLayer( -6 ) );
		fill( lowerLayer( 0 ) );
		fill( lowerLayer( 6 ) );
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
		Proof.proof( new LayersIcon() );
	}

}
