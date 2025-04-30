package com.avereon.cartesia.cursor;

import com.avereon.zerra.image.RenderedIcon;
import javafx.geometry.Dimension2D;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import lombok.CustomLog;

@CustomLog
public class ReticleCursor extends ImageCursor {

	private final Reticle reticle;

	public ReticleCursor( Reticle reticle ) {
		super( toImage( reticle.getIcon() ), 0.5 * (reticle.getIcon().getWidth()) - 1, 0.5 * (reticle.getIcon().getHeight() - 1) );
		this.reticle = reticle;
	}

	protected static Image toImage( RenderedIcon icon ) {
		Dimension2D size = ImageCursor.getBestSize( 64, 64 );
		icon.regrid( size.getWidth(), size.getHeight() );
		icon.resize( size.getWidth(), size.getHeight() );
		return icon.getImage();
	}

	@Override
	public String toString() {
		return reticle.name();
	}

}
