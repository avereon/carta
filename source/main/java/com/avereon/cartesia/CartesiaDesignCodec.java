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

public abstract class CartesiaDesignCodec extends Codec {

	private static final System.Logger log = Log.get();

	static final ObjectMapper JSON_MAPPER;

	static final String CODEC_VERSION_KEY = "codec-version";

	static final String CODEC_VERSION = "1";

	private static final Map<String, String> savePaintMapping;
	private static final Map<String, String> loadPaintMapping;
	private static final Map<String, String> saveLayerToNullMapping;
	private static final Map<String, String> loadNullToLayerMapping;

	private final Product product;

	static {
		JSON_MAPPER = new ObjectMapper();
		JSON_MAPPER.registerModule( new SimpleModule().addSerializer( Point2D.class, new Point2DSerializer() ) );
		JSON_MAPPER.registerModule( new SimpleModule().addSerializer( Point3D.class, new Point3DSerializer() ) );
		JSON_MAPPER.registerModule( new SimpleModule().addSerializer( Color.class, new ColorSerializer() ) );

		savePaintMapping = Map.of( "null", "none", DesignDrawable.MODE_LAYER, "null" );
		loadPaintMapping = Map.of( "none", "null", "null", DesignDrawable.MODE_LAYER );
		saveLayerToNullMapping = Map.of( DesignDrawable.MODE_LAYER, "null" );
		loadNullToLayerMapping = Map.of( "null", DesignDrawable.MODE_LAYER );
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

		Map<String, Object> map = JSON_MAPPER.readValue( input, new TypeReference<>() {} );

		log.log( Log.DEBUG, "Design codec version: " + map.get( CODEC_VERSION_KEY ) );

		Map<String, Map<String, Object>> layers = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.LAYERS, Map.of() );
		Map<String, Map<String, Object>> views = (Map<String, Map<String, Object>>)map.getOrDefault( Design.VIEWS, Map.of() );

		design.updateFrom( map );

		// Load layers
		layers.values().forEach( l -> loadLayer( design.getRootLayer(), l ) );

		// Load views
		views.values().forEach( v -> design.addView( new DesignView().updateFrom( v ) ) );

		asset.setModel( design );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public void save( Asset asset, OutputStream output ) throws IOException {
		Map<String, Object> map = ((Design)asset.getModel()).asDeepMap();

		remapLayers( map );

		map.put( CODEC_VERSION_KEY, CODEC_VERSION );
		JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValue( output, map );
	}

	@SuppressWarnings( "unchecked" )
	private void remapLayers( Map<String, Object> map ) {
		Map<String, Object> layers = (Map<String, Object>)map.get( "layers" );
		layers.values().parallelStream().map( o -> (Map<String, Object>)o ).forEach( m -> {
			remapLayer( m );
			remapLayers( m );
		} );
	}

	@SuppressWarnings( "unchecked" )
	private void remapLayer( Map<String, Object> map ) {
		Map<String, Map<String, Object>> geometry = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.SHAPES, Map.of() );
		geometry.values().forEach( g -> {
			// Value mapping
			remapValue( g, DesignDrawable.DRAW_PAINT, savePaintMapping );
			remapValue( g, DesignDrawable.DRAW_WIDTH, saveLayerToNullMapping );
			remapValue( g, DesignDrawable.DRAW_CAP, saveLayerToNullMapping );
			remapValue( g, DesignDrawable.DRAW_PATTERN, saveLayerToNullMapping );
			remapValue( g, DesignDrawable.FILL_PAINT, savePaintMapping );
		} );
	}

	@SuppressWarnings( "unchecked" )
	private void loadLayer( DesignLayer parent, Map<String, Object> map ) {
		DesignLayer layer = new DesignLayer().updateFrom( map );
		parent.addLayer( layer );

		// Add the shapes found in the layer
		Map<String, Map<String, Object>> geometry = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.SHAPES, Map.of() );
		geometry.values().forEach( g -> {
			// Old keys
			moveKey( g, "draw-color", DesignDrawable.DRAW_PAINT );
			moveKey( g, "fill-color", DesignDrawable.FILL_PAINT );

			// Value mapping
			remapValue( g, DesignDrawable.DRAW_PAINT, loadPaintMapping );
			remapValue( g, DesignDrawable.DRAW_WIDTH, loadNullToLayerMapping );
			remapValue( g, DesignDrawable.DRAW_CAP, loadNullToLayerMapping );
			remapValue( g, DesignDrawable.DRAW_PATTERN, loadNullToLayerMapping );
			remapValue( g, DesignDrawable.FILL_PAINT, loadPaintMapping );

			String type = String.valueOf( g.get( DesignShape.SHAPE ) );
			DesignShape shape = switch( type ) {
				case DesignMarker.MARKER, DesignMarker.POINT -> loadDesignMarker( g );
				case DesignLine.LINE -> loadDesignLine( g );
				case DesignEllipse.CIRCLE, DesignEllipse.ELLIPSE -> loadDesignEllipse( g );
				case DesignArc.ARC -> loadDesignArc( g );
				case DesignCurve.CURVE -> loadDesignCurve( g );
				default -> null;
			};
			layer.addShape( shape );
		} );

		Map<String, Map<String, Object>> layers = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.LAYERS, Map.of() );
		layers.values().forEach( l -> loadLayer( layer, l ) );
	}

	private DesignMarker loadDesignMarker( Map<String, Object> map ) {
		return new DesignMarker().updateFrom( map );
	}

	private DesignLine loadDesignLine( Map<String, Object> map ) {
		return new DesignLine().updateFrom( map );
	}

	private DesignEllipse loadDesignEllipse( Map<String, Object> map ) {
		return new DesignEllipse().updateFrom( map );
	}

	private DesignArc loadDesignArc( Map<String, Object> map ) {
		return new DesignArc().updateFrom( map );
	}

	private DesignCurve loadDesignCurve( Map<String, Object> map ) {
		return new DesignCurve().updateFrom( map );
	}

	private void moveKey( Map<String, Object> map, String oldKey, String newKey ) {
		if( !map.containsKey( oldKey ) ) return;
		map.put( newKey, map.get( oldKey ) );
		map.remove( oldKey );
	}

	private void remapValue( Map<String, Object> map, String key, Map<?, ?> values ) {
		Object currentValue = map.get( key );
		if( currentValue == null ) currentValue = "null";

		Object newValue = values.get( currentValue );
		if( newValue == null ) return;

		if( "null".equals( newValue ) ) {
			map.remove( key );
		} else {
			map.put( key, newValue );
		}
	}

	@SuppressWarnings( "unused" )
	String prettyPrint( byte[] buffer ) throws Exception {
		JsonNode node = JSON_MAPPER.readValue( buffer, JsonNode.class );
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValue( output, node );
		return output.toString( TextUtil.CHARSET );
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
