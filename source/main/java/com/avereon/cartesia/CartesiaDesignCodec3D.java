package com.avereon.cartesia;

import com.avereon.product.Product;

public class CartesiaDesignCodec3D extends CartesiaDesignCodec {

	static final String MEDIA_TYPE = "application/vnd.avereon.cartesia.design.3d";

	public CartesiaDesignCodec3D( Product product ) {
		super( product );
		setDefaultExtension( "cartesia3d" );
		addSupported( Pattern.MEDIATYPE, MEDIA_TYPE );
	}

	@Override
	public String getKey() {
		return MEDIA_TYPE;
	}

	@Override
	public String getName() {
		return getProduct().rb().text( "asset", "codec-cartesia3d-name" );
	}

}
