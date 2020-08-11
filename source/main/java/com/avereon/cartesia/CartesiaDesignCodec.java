package com.avereon.cartesia;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.geometry.CsaPoint;
import com.avereon.product.Product;
import com.avereon.util.Log;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.Codec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Point3D;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public abstract class CartesiaDesignCodec extends Codec {

	private static final System.Logger log = Log.get();

	private final Product product;

	public CartesiaDesignCodec( Product product ) {
		this.product = product;
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
	@SuppressWarnings( "unchecked" )
	public void load( Asset asset, InputStream input ) throws IOException {
		Design design = asset.getModel();
		Map<String, Object> map = new ObjectMapper().readValue( input, new TypeReference<>() {} );
		Map<String, Map<String, Object>> layers = (Map<String, Map<String, Object>>)map.getOrDefault( Design.LAYERS, Map.of() );
		Map<String, Map<String, Object>> views = (Map<String, Map<String, Object>>)map.getOrDefault( Design.VIEWS, Map.of() );

		design.updateFrom( map );
		layers.values().forEach( l -> {
			design.addLayer( new DesignLayer().updateFrom( l ) );
			Map<String, Map<String, Object>> shapes = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.SHAPES, Map.of() );
			// NEXT Add the shapes found in the layer
			shapes.values().forEach( s -> {
				String type = String.valueOf( s.get( "type" ) );
				switch( type ) {
					case "point" : {
						// NEXT Need to parse the origin point
						Point3D origin = new Point3D(0,0,0);
						CsaPoint point = new CsaPoint( origin );
						break;
					}
					case "line" : {
						break;
					}
				}
			});
		} );
		views.values().forEach( l -> design.addView( new DesignView().updateFrom( l ) ) );
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {
		new ObjectMapper().writer().writeValue( output, ((Design)asset.getModel()).asDeepMap() );
	}

}
