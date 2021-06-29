package com.avereon.cartesia;

import com.avereon.product.Product;
import com.avereon.product.Rb;

public class CartesiaDesignCodec3D extends CartesiaDesignCodec {

	public static final String MEDIA_TYPE = "application/vnd.avereon.cartesia.design.3d";

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
		return Rb.text( "asset", "codec-cartesia3d-name" );
	}

}
