package com.avereon.cartesia.icon;

import com.avereon.zarra.image.Proof;
import com.avereon.zarra.image.SvgIcon;

public class CartesiaIcon extends SvgIcon {

	private static final double CX = 16;

	private static final double CY = 9.5;

	private static final double KNOB = 3;

	private static final double RR = 1.5 * KNOB;

	private static final double SS = RR + 2;

	private static final double ARM_WIDTH = 2.5;

	public CartesiaIcon() {
		super( 32, 32 );

		// Slope of 0.5
		double alpha = Math.atan2( 1, 2 );
		// 270 deg - alpha
		double beta = 1.5 * Math.PI - alpha;
		// Perpendicular to beta (180 deg - alpha)
		double gamma = beta - 0.5 * Math.PI;

		double d = Math.sqrt( SS * SS - ARM_WIDTH * ARM_WIDTH );

		// The top-left point of the arm
		double aX = -d * Math.cos( beta ) - ARM_WIDTH * Math.cos( gamma );
		double aY = -d * Math.sin( beta ) - ARM_WIDTH * Math.sin( gamma );

		// The top-right point of the arm
		double bX = SS * -Math.cos( beta );
		double bY = SS * -Math.sin( beta );

		// The bottom-right point of the arm (the offset from center to the arm tip)
		double cX = 10;
		double cY = 2 * cX;

		// The bottom-left (even thought it is directly above point c) point of the arm
		double dX = cX + 0;
		double dY = cY - (ARM_WIDTH / Math.sin( alpha ));

		fill( top() + left( aX, aY, bX, bY, cX, cY, dX, dY ) + right( aX, aY, bX, bY, cX, cY, dX, dY ) );
	}

	private String top() {
		String top = "";

		double halfKnob = 0.5 * KNOB;
		double a = Math.sqrt( RR * RR - halfKnob * halfKnob );
		top += "M" + (CX - halfKnob) + "," + (CY - RR - KNOB);
		top += "L" + (CX - halfKnob) + "," + (CY - a);
		top += "A" + RR + "," + RR + " 0 1 0 " + (CX + halfKnob) + "," + (CY - a);
		top += "L" + (CX + halfKnob) + "," + (CY - RR - KNOB);
		top += circle( CX, CY, RR - ARM_WIDTH );
		top += "Z";
		return top;
	}

	private String left( double aX, double aY, double bX, double bY, double cX, double cY, double dX, double dY ) {
		String left = "M" + (CX - aX) + "," + (CY + aY);
		left += "A" + SS + "," + SS + " 0 0 0 " + (CX - bX) + "," + (CY + bY);
		left += "L" + (CX - cX) + "," + (CY + cY);
		left += "L" + (CX - dX) + "," + (CY + dY);
		left += "Z";
		return left;
	}

	private String right( double aX, double aY, double bX, double bY, double cX, double cY, double dX, double dY ) {
		String right = "M" + (CX + aX) + "," + (CY + aY);
		right += "A" + SS + "," + SS + " 0 0 1 " + (CX + bX) + "," + (CY + bY);
		right += "L" + (CX + cX) + "," + (CY + cY);
		right += "L" + (CX + dX) + "," + (CY + dY);
		right += "Z";
		return right;
	}

	public static void main( String[] commands ) {
		Proof.proof( new CartesiaIcon() );
	}

}
