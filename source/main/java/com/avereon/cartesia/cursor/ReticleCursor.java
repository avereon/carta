package com.avereon.cartesia.cursor;

import com.avereon.zarra.image.RenderedIcon;
import com.avereon.zarra.javafx.Fx;

import java.util.concurrent.CompletableFuture;

public enum ReticleCursor {

	DUPLEX( new DuplexReticle( 0.8 ) ),
	DUPLEX_WIDE( new DuplexReticle( 0.7 ) ),
	DUPLEX_60( new DuplexReticle( 0.6 ) ),
	DUPLEX_40( new DuplexReticle( 0.4 ) ),
	DUPLEX_20( new DuplexReticle( 0.2 ) ),
	DUPLEX_PIXEL( new DuplexPixelReticle( 0.7 ) ),
	DUPLEX_DOT( new DuplexDotReticle( 0.7 ) ),
	DUPLEX_CIRCLE( new DuplexCircleReticle( 0.8 ) ),
	CROSSHAIR( new CrosshairReticle() );

	private final RenderedIcon icon;

	ReticleCursor( RenderedIcon icon ) {
		this.icon = icon;
	}

	public IconCursor getCursorIcon( String stylesheet ) {
		CompletableFuture<IconCursor> future = new CompletableFuture<>();
		Fx.run( () -> future.complete( new IconCursor( icon, stylesheet ) ) );
		try {
			return future.get();
		} catch( Exception exception ) {
			throw new RuntimeException( exception );
		}
	}

}
