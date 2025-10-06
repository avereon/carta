package com.avereon.cartesia;

import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.ResourceType;
import com.avereon.xenon.asset.Codec;
import com.avereon.xenon.asset.PlaceholderCodec;

public class ShapePropertiesResourceType extends ResourceType {

	private static final String uriPattern = CartesiaScheme.ID + ":/shape-properties";

	public static final java.net.URI URI = java.net.URI.create( uriPattern );

	public ShapePropertiesResourceType( XenonProgramProduct product ) {
		super( product, "shape-properties" );

		PlaceholderCodec codec = new PlaceholderCodec();
		codec.addSupported( Codec.Pattern.URI, uriPattern );
		setDefaultCodec( codec );
	}

	@Override
	public String getKey() {
		return uriPattern;
	}

	@Override
	public boolean isUserType() {
		return false;
	}

}
