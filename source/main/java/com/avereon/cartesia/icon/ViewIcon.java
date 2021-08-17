package com.avereon.cartesia.icon;

import com.avereon.zerra.image.Proof;
import com.avereon.zerra.image.SvgIcon;

public class ViewIcon extends SvgIcon {

	private static final double Ax = 1;

	private static final double Ay = 16;

	private static final double Bx = 10;

	private static final double By = 4;

	private static final double Cx = 22;

	private static final double Cy = By;

	private static final double Dx = 31;

	private static final double Dy = 16;

	private static final double Ex = 22;

	private static final double Ey = 24;

	private static final double Fx = 12;

	private static final double Fy = Ey - 4;

	private static final double Px = 16;

	private static final double Py = 13;

	public ViewIcon() {
		super( 32, 32 );
	}

	@Override
	protected void doRender() {
		String eye = "M" + Ax + " " + Ay + "C" + Bx + " " + By + " " + Cx + " " + Cy + " " + Dx + " " + Dy + "C" + Ex + " " + Ey + " " + Fx + " " + Fy + " " + Ax + " " + Ay + "Z";

		save();
		clip( eye );
		draw( circle( Px, Py, 5 ), 1.5 * getStrokeWidth() );

		restore();
		draw( eye, 0.5 * getStrokeWidth() );

		super.doRender();
	}

	public static void main( String[] commands ) {
		Proof.proof( new ViewIcon() );
	}

}
