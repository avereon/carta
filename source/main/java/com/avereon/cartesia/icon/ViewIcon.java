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

	private static final double Fy = Ey;

	private static final double Px = 16;

	private static final double Py = 15;

	public ViewIcon() {
		super( 32, 32 );
	}

	@Override
	protected void doRender() {
		String tLid = "M 2,14 A 20 20 0 0 1 30,14 A 15 15 0 0 0 2,14 Z";
		//String eye = "M 2,16 A 16 16 0 0 0 30,16 L 28,16 A 16 16 0 0 0 4,16 Z";
		String eye = "M 4,16 A 16 16 0 0 0 28,16 A 16 16 0 0 0 4,16 Z";
		String bLid = "M 2,16 A 17 17 0 0 0 30,16 A 15 15 0 0 1 2,16 Z";

		clip( eye );
		fill( circle( Px, Py, 7 ) + " " + circle( Px, Py, 2 ) );

		//restore();
		clip(null);
		fill( tLid );
		fill( bLid );

		super.doRender();
	}

	protected void doRenderOld() {
		String eye = "M" + Ax + " " + Ay + "C" + Bx + " " + By + " " + Cx + " " + Cy + " " + Dx + " " + Dy + "C" + Ex + " " + Ey + " " + Fx + " " + Fy + " " + Ax + " " + Ay + "Z";

		clip( eye );
		draw( circle( Px, Py, 5 ), 1.5 * getStrokeWidth() );

		clip( null );
		draw( eye, 0.5 * getStrokeWidth() );

		super.doRender();
	}

	public static void main( String[] commands ) {
		Proof.proof( new ViewIcon() );
	}

}
