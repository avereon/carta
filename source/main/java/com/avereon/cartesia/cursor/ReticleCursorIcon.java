package com.avereon.cartesia.cursor;

import com.avereon.zarra.image.RenderedIcon;
import com.avereon.zarra.javafx.Fx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReticleCursorIcon extends IconCursor {

	private static final Map<ReticleCursor, ReticleCursorIcon> cache;

	static {
		cache = new ConcurrentHashMap<>();
		refresh();
	}

	protected ReticleCursorIcon( RenderedIcon icon ) {
		super( icon );
	}

	public static ReticleCursorIcon get( ReticleCursor cursor ) {
		return cache.get( cursor );
	}

	public static void refresh() {
		Fx.run(ReticleCursorIcon::doRefresh);
	}

	private static void doRefresh() {
		cache.clear();
		cache.put( ReticleCursor.DUPLEX, new ReticleCursorIcon( new DuplexReticle( 0.8 ) ) );
		cache.put( ReticleCursor.DUPLEX_WIDE, new ReticleCursorIcon( new DuplexReticle( 0.7 ) ) );
		cache.put( ReticleCursor.DUPLEX_60, new ReticleCursorIcon( new DuplexReticle( 0.6 ) ) );
		cache.put( ReticleCursor.DUPLEX_40, new ReticleCursorIcon( new DuplexReticle( 0.4 ) ) );
		cache.put( ReticleCursor.DUPLEX_20, new ReticleCursorIcon( new DuplexReticle( 0.2 ) ) );
		cache.put( ReticleCursor.DUPLEX_PIXEL, new ReticleCursorIcon( new DuplexPixelReticle( 0.7 ) ) );
		cache.put( ReticleCursor.DUPLEX_DOT, new ReticleCursorIcon( new DuplexDotReticle( 0.7 ) ) );
		cache.put( ReticleCursor.DUPLEX_CIRCLE, new ReticleCursorIcon( new DuplexCircleReticle( 0.8 ) ) );
		cache.put( ReticleCursor.CROSSHAIR, new ReticleCursorIcon( new CrosshairReticle() ) );
	}

}
