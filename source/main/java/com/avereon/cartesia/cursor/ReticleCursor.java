package com.avereon.cartesia.cursor;

import com.avereon.xenon.Program;
import com.avereon.zarra.image.RenderedIcon;
import com.avereon.zarra.javafx.Fx;
import com.avereon.zarra.style.Theme;

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

	public IconCursor getCursorIcon( Program program ) {
		// NEXT Get the cursor color to change with the theme

		/*
		I think I have figured out how I planned the cursor color to
		change. Because the cursor is rendered in its own scene it does
		not get any information from the workspace theme. There is,
		however, a setTheme() method to force light or dark. I just
		need to figure out how to link the workspace theme to the cursor
		theme.

		This also means that all the work that I put in to passing in
		a stylesheet may not be needed or desired. There is a conflict
		with setting the CSS directly with setTheme() and indirectly
		with getStylesheets().add()
		 */
		icon.setTheme( program.getWorkspaceManager().getThemeMetadata().isDark() ? Theme.DARK : Theme.LIGHT );
		CompletableFuture<IconCursor> future = new CompletableFuture<>();
		Fx.run( () -> future.complete( new IconCursor( icon ) ) );
		try {
			return future.get();
		} catch( Exception exception ) {
			throw new RuntimeException( exception );
		}
	}

}
