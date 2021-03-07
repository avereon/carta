package com.avereon.cartesia.settings;

import com.avereon.cartesia.BundleKey;
import com.avereon.product.Rb;

class PaintMode {

	public static final PaintMode NONE;
	public static final PaintMode SOLID;
	public static final PaintMode LINEAR;
	public static final PaintMode RADIAL;

	private final String key;

	private final String label;

	private String value;

	static {
		NONE = new PaintMode( "none", Rb.text( BundleKey.LABEL, "none" ), null );
		SOLID = new PaintMode( "solid", Rb.text( BundleKey.LABEL, "solid" ), null );
		LINEAR = new PaintMode( "linear", Rb.text( BundleKey.LABEL, "linear" ), null );
		RADIAL = new PaintMode( "radial", Rb.text( BundleKey.LABEL, "radial" ), null );
	}

	public PaintMode( String key, String label, String value ) {
		this.key = key;
		this.label = label;
		this.value = value;

		//		String none = product.rb().textOr( BundleKey.LABEL, "none", "None" );
		//		String solid = product.rb().textOr( BundleKey.LABEL, "solid", "Solid Color" );
		//		String linear = product.rb().textOr( BundleKey.LABEL, "linear-gradient", "Linear Gradient" );
		//		String radial = product.rb().textOr( BundleKey.LABEL, "radial-gradient", "Radial Gradient" );
	}

	public String getKey() {
		return key;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	public void setValue( String value ) {
		this.value = value;
	}

	@Override
	public String toString() {
		return label;
	}

}
