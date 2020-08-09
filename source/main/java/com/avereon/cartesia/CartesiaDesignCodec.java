package com.avereon.cartesia;

import com.avereon.cartesia.data.Design;
import com.avereon.product.Product;
import com.avereon.util.Log;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.Codec;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CartesiaDesignCodec extends Codec {

	static final String MEDIA_TYPE = "application/vnd.avereon.recon.network.graph";

	private static final System.Logger log = Log.get();

	private final Product product;

	public CartesiaDesignCodec( Product product ) {
		this.product = product;
		setDefaultExtension( "cartesia2d" );
		addSupported( Pattern.MEDIATYPE, MEDIA_TYPE );
	}

	@Override
	public String getKey() {
		return MEDIA_TYPE;
	}

	@Override
	public String getName() {
		return product.rb().text( "asset", "codec-cartesia2d-name" );
	}

	@Override
	public boolean canLoad() {
		return true;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public void load( Asset asset, InputStream input ) throws IOException {
		Design design = asset.getModel();
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {
		Design design = asset.getModel();
		new ObjectMapper().writer().writeValue( output, design.asDeepMap() );
	}

	//private void saveLayer( DesignLayer layer, )

}
