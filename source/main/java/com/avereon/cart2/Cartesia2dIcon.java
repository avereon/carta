package com.avereon.cart2;

import com.avereon.venza.image.Proof;
import com.avereon.venza.image.SvgIcon;

public class Cartesia2dIcon extends SvgIcon {

	private static final double CX = 16;

	private static final double CY = 9.5;

	private static final double KNOB = 3;

	private static final double RR = 1.5 * KNOB;

	private static final double SS = RR + 2;

	private static final double ARM_WIDTH = 2.5;

	public Cartesia2dIcon() {
		super( 32, 32 );
		//super( 24, 24, "M16.24,11.51l1.57-1.57l-3.75-3.75l-1.57,1.57L8.35,3.63 c-0.78-0.78-2.05-0.78-2.83,0l-1.9,1.9 c-0.78,0.78-0.78,2.05,0,2.83l4.13,4.13L3,17.25V21h3.75l4.76-4.76l4.13,4.13c0.95,0.95,2.23,0.6,2.83,0l1.9-1.9 c0.78-0.78,0.78-2.05,0-2.83L16.24,11.51z M9.18,11.07L5.04,6.94l1.89-1.9c0,0,0,0,0,0l1.27,1.27L7.02,7.5l1.41,1.41l1.19-1.19 l1.45,1.45L9.18,11.07z M17.06,18.96l-4.13-4.13l1.9-1.9l1.45,1.45l-1.19,1.19l1.41,1.41l1.19-1.19l1.27,1.27L17.06,18.96z M20.71,7.04c0.39-0.39,0.39-1.02,0-1.41l-2.34-2.34c-0.47-0.47-1.12-0.29-1.41,0l-1.83,1.83l3.75,3.75L20.71,7.04z" );

		double halfSize = 0.5 * KNOB;
		double a = Math.sqrt( RR * RR - halfSize * halfSize );

		double halfKnob = 0.5 * KNOB;

		String top = "";
		top += "M" + (CX - halfKnob) + "," + (CY - RR - KNOB);
		top += "L" + (CX - halfKnob) + "," + (CY - a);
		top += "A" + RR + "," + RR + " 0 1 0 " + (CX + halfKnob) + "," + (CY - a);
		top += "L" + (CX + halfKnob) + "," + (CY - RR - KNOB);
		top += circle( CX, CY, RR - ARM_WIDTH );
		top += "Z";
		add( top );

		// Slope of 0.5
		double alpha = Math.atan2( 1, 2 );
		// 270 deg - alpha
		double beta = 1.5 * Math.PI - alpha;
		double aX = SS * -Math.cos( beta );
		double aY = SS * -Math.sin( beta );
		double offsetX = 10;
		double offsetY = 2 * offsetX;

		// Perpendicular to beta (180 deg - alpha)
		double gamma = beta - 0.5 * Math.PI;
		double d = Math.sqrt( SS * SS - ARM_WIDTH * ARM_WIDTH );
		double dX = CX + d * Math.cos( beta );
		double dY = CY - d * Math.sin( beta );
		double eX = dX + ARM_WIDTH * Math.cos( gamma );
		double eY = dY - ARM_WIDTH * Math.sin( gamma );

		double hX = CX - eX;
		//double gX = CX - aX;

		String left = "M" + (CX - hX) + "," + eY;
		left += "A" + SS + "," + SS + " 0 0 0 " + (CX - aX) + "," + (CY + aY);
		left += "L" + (CX - offsetX) + "," + (CY + offsetY);
		left += "l0," + -ARM_WIDTH / Math.sin( alpha );
		left += "Z";
		add( left );

		String right = "M" + (CX + hX) + "," + eY;
		right += "A" + SS + "," + SS + " 0 0 1 " + (CX + aX) + "," + (CY + aY);
		right += "L" + (CX + offsetX) + "," + (CY + offsetY);
		right += "l0," + -ARM_WIDTH / Math.sin( alpha );
		right += "Z";
		add( right );
	}

	public static void main( String[] commands ) {
		Proof.proof( new Cartesia2dIcon() );
	}

}
