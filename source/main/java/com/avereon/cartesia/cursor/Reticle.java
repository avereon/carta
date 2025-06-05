package com.avereon.cartesia.cursor;

import com.avereon.xenon.XenonProgram;
import com.avereon.zerra.image.RenderedIcon;
import lombok.CustomLog;
import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

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

	private static final Map<Reticle, ReticleCursor> cursorCache = new EnumMap<>( Reticle.class );

	final RenderedIcon icon;

	Reticle( RenderedIcon icon ) {
		this.icon = icon;
	}

	public ReticleCursor getCursor( XenonProgram program ) {
		if( cursorCache.containsKey( this ) ) return cursorCache.get( this );
		icon.setTheme( program.getWorkspaceManager().getThemeMetadata().getMotif() );
		ReticleCursor cursor = new ReticleCursor( this );
		cursorCache.put( this, cursor );
		return cursor;
	}

	public static void clearCursorCache() {
		cursorCache.clear();
	}

}
