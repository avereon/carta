package com.avereon.cartesia.cursor;

import com.avereon.venza.image.VectorImage;
import javafx.geometry.Dimension2D;
import javafx.scene.ImageCursor;

public enum StandardCursor {

	DUPLEX( new DuplexCursor( 0.8 ) ),
	WIDE_DUPLEX( new DuplexCursor( 0.7 ) ),
	DUPLEX_60( new DuplexCursor( 0.6 ) ),
	DUPLEX_40( new DuplexCursor( 0.4 ) ),
	DUPLEX_20( new DuplexCursor( 0.2 ) ),
	DUPLEX_DOT( new DuplexDotCursor( 0.8 ) ),
	DUPLEX_CIRCLE( new DuplexCircleCursor( 0.8 ) ),
	CROSSHAIR( new CrosshairCursor() );

	private final VectorImage cursor;

	StandardCursor( VectorImage cursor ) {
		this.cursor = cursor;
	}

	public ImageCursor get() {
		Dimension2D size = ImageCursor.getBestSize( 64, 64 );
		VectorImage cursor = this.cursor.copy();
		cursor.regrid( size.getWidth(), size.getHeight() );
		cursor.resize( size.getWidth(), size.getHeight() );
		return new ImageCursor( cursor.getImage(), 0.5 * (size.getWidth()) - 1, 0.5 * (size.getHeight() - 1) );
	}

}
