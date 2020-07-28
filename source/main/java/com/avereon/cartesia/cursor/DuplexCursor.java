package com.avereon.cartesia.cursor;

import com.avereon.zerra.image.Proof;
import com.avereon.zerra.image.VectorImage;

public class DuplexCursor extends CursorIcon {

	private double percent;

	public DuplexCursor() {}

	public DuplexCursor( double percent ) {
		this.percent = percent;
	}

	public DuplexCursor( double percent, double width, double height ) {
		super( width, height );
		this.percent = percent;
	}

	@Override
	protected void render() {
		super.render();
		startPath();
		getGraphicsContext2D().appendSVGPath( getPath() );
		setStrokeWidth( 1 );
		move( 0.5, 0.5 );
		draw();
	}

	protected double getW() {
		return getWidth() - 1;
	}

	protected double getC() {
		return 0.5 * getW();
	}

	protected double getR() {
		return Math.round( 0.5 * percent * getW() );
	}

	protected String getPath() {
		double w = getW();
		double r = getR();
		double a = 0;
		double c = getC();

		double d = a + r;
		double e = w - r;

		String path = "";
		path += "M" + a + "," + c;
		path += "L" + d + "," + c;
		path += "M" + e + "," + c;
		path += "L" + w + "," + c;

		path += "M" + c + "," + a;
		path += "L" + c + "," + d;
		path += "M" + c + "," + e;
		path += "L" + c + "," + w;

		return path;
	}

	@Override
	public <T extends VectorImage> T copy() {
		T copy = super.copy();

		((DuplexCursor)copy).percent = this.percent;

		return copy;
	}

	public static void main( String[] commands ) {
		Proof.proof( new DuplexCursor( 0.8, 48, 48 ) );
	}

}
