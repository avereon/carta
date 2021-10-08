package com.avereon.cartesia.cursor;

import com.avereon.zarra.image.RenderedIcon;

public class ReticleCursor extends IconCursor {

	public static final ReticleCursor DUPLEX = new ReticleCursor( new DuplexReticle( 0.8 ) );

	public static final ReticleCursor DUPLEX_WIDE = new ReticleCursor( new DuplexReticle( 0.7 ) );

	public static final ReticleCursor DUPLEX_60 = new ReticleCursor( new DuplexReticle( 0.6 ) );

	public static final ReticleCursor DUPLEX_40 = new ReticleCursor( new DuplexReticle( 0.4 ) );

	public static final ReticleCursor DUPLEX_20 = new ReticleCursor( new DuplexReticle( 0.2 ) );

	public static final ReticleCursor DUPLEX_PIXEL = new ReticleCursor( new DuplexPixelReticle( 0.7 ) );

	public static final ReticleCursor DUPLEX_DOT = new ReticleCursor( new DuplexDotReticle( 0.7 ) );

	public static final ReticleCursor DUPLEX_CIRCLE = new ReticleCursor( new DuplexCircleReticle( 0.8 ) );

	public static final ReticleCursor CROSSHAIR = new ReticleCursor( new CrosshairReticle() );

	protected ReticleCursor( RenderedIcon icon ) {
		super( icon );
	}

	public static ReticleCursor valueOf( String string ) {
		return switch( string.toUpperCase() ) {
			case "DUPLEX_WIDE" -> DUPLEX_WIDE;
			case "CROSSHAIR" -> CROSSHAIR;
			case "DUPLEX_60" -> DUPLEX_60;
			case "DUPLEX_40" -> DUPLEX_40;
			case "DUPLEX_20" -> DUPLEX_20;
			case "DUPLEX_PIXEL" -> DUPLEX_PIXEL;
			case "DUPLEX_DOT" -> DUPLEX_DOT;
			case "DUPLEX_CIRCLE" -> DUPLEX_CIRCLE;
			default -> DUPLEX;
		};
	}

}
