package com.avereon.cartesia;

import com.avereon.product.Product;
import com.avereon.product.Rb;

public class CartesiaDesignCodec2D extends CartesiaDesignCodec {

	public static final String MEDIA_TYPE = "application/vnd.avereon.cartesia.design.2d";

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
		return Rb.text( "asset", "codec-cartesia2d-name" );
	}

}
