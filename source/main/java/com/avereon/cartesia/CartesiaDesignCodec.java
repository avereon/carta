package com.avereon.cartesia;

import com.avereon.cartesia.data.*;
import com.avereon.product.Product;
import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.Codec;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;

public abstract class CartesiaDesignCodec extends Codec {

	private static final System.Logger log = Log.get();

	static final ObjectMapper JSON_MAPPER;

	static final String CODEC_VERSION_KEY = "codec-version";

	static final String CODEC_VERSION = "1";

	static final String CURRENT_LAYER_ID = "current-layer";

	private final Product product;

	static {
		JSON_MAPPER = new ObjectMapper();
		JSON_MAPPER.registerModule( new SimpleModule().addSerializer( Point2D.class, new Point2DSerializer() ) );
		JSON_MAPPER.registerModule( new SimpleModule().addSerializer( Point3D.class, new Point3DSerializer() ) );
		JSON_MAPPER.registerModule( new SimpleModule().addSerializer( Color.class, new ColorSerializer() ) );
	}

	public CartesiaDesignCodec( Product product ) {
		this.product = product;
	}

	protected Product getProduct() {
		return product;
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
		Design2D design = new Design2D();
		design.getRootLayer().removeLayer( design.getCurrentLayer() );

		Map<String, Object> map = JSON_MAPPER.readValue( input, new TypeReference<>() {} );

		log.log( Log.DEBUG, "Design codec version: " + map.get( CODEC_VERSION_KEY ) );

		Map<String, Map<String, Object>> layers = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.LAYERS, Map.of() );
		Map<String, Map<String, Object>> views = (Map<String, Map<String, Object>>)map.getOrDefault( Design.VIEWS, Map.of() );

		design.updateFrom( map );
		String currentLayerId = String.valueOf( map.get( Design.CURRENT_LAYER ) );

		// Load layers
		layers.values().forEach( l -> loadLayer( design, design.getRootLayer(), currentLayerId, l ));

		// Load views
		views.values().forEach( v -> design.addView( new DesignView().updateFrom( v ) ) );

		asset.setModel( design );
	}

	@SuppressWarnings( "unchecked" )
	private void loadLayer( Design design, DesignLayer parent, String currentLayerId, Map<String, Object> map ) {
		DesignLayer layer = new DesignLayer().updateFrom( map );
		parent.addLayer( layer );
		if( Objects.equals( currentLayerId, layer.getId() ) ) design.setCurrentLayer( layer );

		// Add the shapes found in the layer
		Map<String, Map<String, Object>> geometry = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.SHAPES, Map.of() );
		geometry.values().forEach( g -> {
			String type = String.valueOf( g.get( DesignShape.SHAPE ) );
			DesignShape shape = switch( type ) {
				case "point" -> loadCsaPoint( g );
				case "line" -> loadCsaLine( g );
				default -> null;
			};
			layer.addShape( shape );
		} );

		Map<String, Map<String, Object>> layers = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.LAYERS, Map.of() );
		layers.values().forEach( l -> loadLayer( design, layer, currentLayerId, l ));
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {
		Map<String, Object> map = ((Design)asset.getModel()).asDeepMap();
		map.put( CODEC_VERSION_KEY, CODEC_VERSION );
		JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValue( output, map );
	}

	private DesignPoint loadCsaPoint( Map<String, Object> map ) {
		return new DesignPoint().updateFrom( map );
	}

	private DesignLine loadCsaLine( Map<String, Object> map ) {
		return new DesignLine().updateFrom( map );
	}

	String prettyPrint( byte[] buffer ) throws Exception {
		JsonNode node = JSON_MAPPER.readValue( buffer, JsonNode.class );
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValue( output, node );
		return new String( output.toByteArray(), TextUtil.CHARSET );
	}

	public static class ColorSerializer extends JsonSerializer<Color> {

		@Override
		public void serialize( Color value, JsonGenerator generator, SerializerProvider provider ) throws IOException {
			generator.writeString( value.toString() );
		}

	}

	public static class Point2DSerializer extends JsonSerializer<Point2D> {

		@Override
		public void serialize( Point2D value, JsonGenerator generator, SerializerProvider provider ) throws IOException {
			generator.writeString( value.getX() + "," + value.getY() );
		}

	}

	public static class Point3DSerializer extends JsonSerializer<Point3D> {

		@Override
		public void serialize( Point3D value, JsonGenerator generator, SerializerProvider provider ) throws IOException {
			generator.writeString( value.getX() + "," + value.getY() + "," + value.getZ() );
		}

	}

}
