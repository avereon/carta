package com.avereon.cartesia.cursor;

import com.avereon.venza.image.Proof;
import javafx.geometry.Dimension2D;

public class CrosshairCursor extends CursorIcon {

	public CrosshairCursor() {}

	public CrosshairCursor( Dimension2D size ) {
		this( size.getWidth(), size.getHeight() );
	}

	public CrosshairCursor( double width, double height ) {
		super( width, height );
		resize( width, height );
	}

	@Override
	protected void render() {
		super.render();
		double w = getWidth()-1;
		double a = 0;
		double c = 0.5 * w;

		String path = "M" + a + "," + c;
		path += "L" + w + ", " + c;
		path += "M" + c + ", " + a;
		path += "L" + c + ", " + w;

		startPath();
		getGraphicsContext2D().appendSVGPath( path );
		setStrokeWidth( 1 );
		move( 0.5, 0.5 );
		draw();
	}

	public static void main( String[] commands ) {
		Proof.proof( new CrosshairCursor( 48, 48 ) );
	}

}
