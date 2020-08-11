package com.avereon.cartesia;

import com.avereon.product.Product;

public class CartesiaDesignCodec2D extends CartesiaDesignCodec {

	static final String MEDIA_TYPE = "application/vnd.avereon.cartesia.design.2d";

	public CartesiaDesignCodec2D( Product product ) {
		super( product );
		setDefaultExtension( "cartesia2d" );
		addSupported( Pattern.MEDIATYPE, MEDIA_TYPE );
	}

	@Override
	public String getKey() {
		return MEDIA_TYPE;
	}

	@Override
	public String getName() {
		return getProduct().rb().text( "asset", "codec-cartesia2d-name" );
	}

}
