package com.avereon.cartesia.cursor;

import com.avereon.xenon.XenonProgram;
import com.avereon.zerra.image.RenderedIcon;
import com.avereon.zerra.javafx.Fx;
import lombok.CustomLog;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;

@Getter
@CustomLog
public enum Reticle {

	DUPLEX( new DuplexReticle( 0.8 ) ),
	DUPLEX_WIDE( new DuplexReticle( 0.7 ) ),
	DUPLEX_60( new DuplexReticle( 0.6 ) ),
	DUPLEX_40( new DuplexReticle( 0.4 ) ),
	DUPLEX_20( new DuplexReticle( 0.2 ) ),
	DUPLEX_PIXEL( new DuplexPixelReticle( 0.7 ) ),
	DUPLEX_DOT( new DuplexDotReticle( 0.7 ) ),
	DUPLEX_CIRCLE( new DuplexCircleReticle( 0.8 ) ),
	CROSSHAIR( new CrosshairReticle() );

	final RenderedIcon icon;

	Reticle( RenderedIcon icon ) {
		this.icon = icon;
	}

	public ReticleCursor getCursor( XenonProgram program ) {
		// Change the cursor color according to the theme
		icon.setTheme( program.getWorkspaceManager().getThemeMetadata().getMotif() );

		final CompletableFuture<ReticleCursor> future = new CompletableFuture<>();

		Fx.run( () -> future.complete( new ReticleCursor( this ) ) );

		try {
			return future.get();
		} catch( Exception exception ) {
			throw new RuntimeException( exception );
		}
	}

}
