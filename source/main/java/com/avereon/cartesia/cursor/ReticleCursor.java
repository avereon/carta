package com.avereon.cartesia.cursor;

import com.avereon.zarra.image.RenderedIcon;
import com.avereon.zarra.javafx.Fx;
import javafx.geometry.Dimension2D;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;

public class ReticleCursor extends ImageCursor {

	public ReticleCursor( RenderedIcon icon ) {
		super( toImage( icon ), 0.5 * (icon.getWidth()) - 1, 0.5 * (icon.getHeight() - 1) );
	}

	protected static Image toImage( RenderedIcon icon ) {
		Fx.affirmOnFxThread();
		Dimension2D size = ImageCursor.getBestSize( 64, 64 );
		icon.regrid( size.getWidth(), size.getHeight() );
		icon.resize( size.getWidth(), size.getHeight() );
		return icon.getImage();
	}

}
