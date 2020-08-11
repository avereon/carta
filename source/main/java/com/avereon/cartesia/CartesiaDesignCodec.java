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
import java.util.HashMap;
import java.util.Map;

public abstract class CartesiaDesignCodec extends Codec {

	private static final System.Logger log = Log.get();

	static final ObjectMapper JSON_MAPPER;

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
		Design design = asset.getModel();
		Map<String, Object> map = JSON_MAPPER.readValue( input, new TypeReference<>() {} );

		Map<String, Map<String, Object>> layers = (Map<String, Map<String, Object>>)map.getOrDefault( Design.LAYERS, Map.of() );
		Map<String, Map<String, Object>> views = (Map<String, Map<String, Object>>)map.getOrDefault( Design.VIEWS, Map.of() );

		design.updateFrom( map );
		layers.values().forEach( l -> {
			DesignLayer layer = new DesignLayer().updateFrom( toStringMap(l) );
			design.addLayer( layer );

			Map<String, Map<String, String>> shapes = (Map<String, Map<String, String>>)l.getOrDefault( DesignLayer.SHAPES, Map.of() );

			// Add the shapes found in the layer
			shapes.values().forEach( s -> {
				String type = String.valueOf( s.get( CsaShape.SHAPE ) );
				CsaShape shape = switch( type ) {
					case "point" -> loadCsaPoint( s );
					case "line" -> loadCsaLine( s );
					default -> null;
				};
				layer.addShape( shape );
			} );
		} );
		views.values().forEach( l -> design.addView( new DesignView().updateFrom( l ) ) );
	}

	private Map<String,String> toStringMap( Map<String,Object> map ) {
		Map<String,String> stringMap = new HashMap<>();
		map.forEach( (k,v) -> {
			if( v != null ) stringMap.put( k, v.toString() );
		} );
		return stringMap;
	}

	private CsaPoint loadCsaPoint( Map<String,String> map ) {
		return new CsaPoint().updateFrom( map );
	}

	private CsaLine loadCsaLine( Map<String,String> map ) {
		return new CsaLine().updateFrom( map );
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {
		JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValue( output, ((Design)asset.getModel()).asDeepMap() );
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
