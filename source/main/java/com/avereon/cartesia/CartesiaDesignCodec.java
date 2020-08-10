package com.avereon.cartesia;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignView;
import com.avereon.product.Product;
import com.avereon.util.Log;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.Codec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

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
		Map<String, Object> map = new ObjectMapper().readValue( input, new TypeReference<>() {} );
		Map<String, Map<String, Object>> layers = (Map<String, Map<String, Object>>)map.getOrDefault( Design.LAYERS, Map.of() );
		Map<String, Map<String, Object>> views = (Map<String, Map<String, Object>>)map.getOrDefault( Design.VIEWS, Map.of() );

		design.updateFrom( map );
		layers.values().forEach( l -> design.addLayer( new DesignLayer().updateFrom( l ) ) );
		views.values().forEach( l -> design.addView( new DesignView().updateFrom( l ) ) );
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {
		new ObjectMapper().writer().writeValue( output, ((Design)asset.getModel()).asDeepMap() );
	}

}
