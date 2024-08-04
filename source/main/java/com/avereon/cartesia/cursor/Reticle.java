package com.avereon.cartesia.cursor;

import com.avereon.xenon.XenonProgram;
import com.avereon.zarra.image.RenderedIcon;
import com.avereon.zarra.javafx.Fx;
import lombok.CustomLog;

import java.util.concurrent.CompletableFuture;

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

	private final RenderedIcon icon;

	Reticle( RenderedIcon icon ) {
		this.icon = icon;
	}

	public ReticleCursor getCursor( XenonProgram program ) {
		// Change the cursor color according to the theme
		icon.setTheme( program.getWorkspaceManager().getThemeMetadata().getMotif() );

		final CompletableFuture<ReticleCursor> future = new CompletableFuture<>();

		program.task( "Create reticle cursor", () -> {
			log.atConfig().log( "Step A" );
			Fx.run( () -> {
				//log.atConfig().log( "Step C" );

				new CursorBuilder( icon, future );
			} );
			log.atConfig().log( "Step B" );
		} );

		try {
			// NEXT Why is it that calling this a second time causes the program to hang?
			log.atWarn().log( "Thread=%s", Thread.currentThread().getName() );
			Thread.yield();
			return future.get();
		} catch( Exception exception ) {
			throw new RuntimeException( exception );
		}
	}

	private static class CursorBuilder implements Runnable {

		private final RenderedIcon icon;

		private final CompletableFuture<ReticleCursor> future;

		public CursorBuilder( RenderedIcon icon, CompletableFuture<ReticleCursor> future ) {
			this.icon = icon;
			this.future = future;
		}

		@Override
		public void run() {
			try {
				future.complete( new ReticleCursor( icon ) );
			} catch(Throwable throwable ) {
				log.atError().withCause( throwable ).log("AHHH");
			}
		}
	}

}
