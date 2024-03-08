package com.avereon.cartesia.ui;

import com.avereon.product.Rb;
import com.avereon.util.TextUtil;
import com.avereon.xenon.RbKey;
import lombok.Getter;

@Getter
public class PaintMode {

	public static final PaintMode NONE;

	public static final PaintMode LAYER;

	public static final PaintMode LINEAR;

	public static final PaintMode RADIAL;

	public static final PaintMode PALETTE_BASIC;

	public static final PaintMode PALETTE_MATERIAL;

	private final String key;

	private final String label;

	private final boolean palette;

	static {
		NONE = new PaintMode( "none", Rb.text( RbKey.LABEL, "none" ), false );
		LAYER = new PaintMode( "layer", Rb.text( RbKey.LABEL, "layer" ), false );
		LINEAR = new PaintMode( "linear", Rb.text( RbKey.LABEL, "linear" ), false );
		RADIAL = new PaintMode( "radial", Rb.text( RbKey.LABEL, "radial" ), false );
		PALETTE_BASIC = new PaintMode( "basic", Rb.text( RbKey.LABEL, "palette-basic" ), true );
		PALETTE_MATERIAL = new PaintMode( "material", Rb.text( RbKey.LABEL, "palette-material" ), true );
	}

	public PaintMode( String key, String label, boolean palette ) {
		this.key = key;
		this.label = label;
		this.palette = palette;
	}

	public static PaintMode getPaintMode( String paint ) {
		if( TextUtil.isEmpty( paint ) ) return PaintMode.NONE;

		if( PaintMode.LAYER.getKey().equals( paint ) ) return PaintMode.LAYER;
		if( paint.startsWith( "0x" ) ) return PaintMode.PALETTE_BASIC;

		return switch( paint.charAt( 0 ) ) {
			case '#' -> PaintMode.PALETTE_BASIC;
			case '[' -> PaintMode.LINEAR;
			case '(' -> PaintMode.RADIAL;
			default -> throw new IllegalStateException( "Unexpected paint mode: " + paint );
		};
	}

	@Override
	public String toString() {
		return getLabel();
	}

}
