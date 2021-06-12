package com.avereon.cartesia;

import com.avereon.cartesia.data.*;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.data.IdNode;
import com.avereon.data.Node;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CartesiaDesignCodec extends Codec {

	private static final System.Logger log = Log.get();

	static final ObjectMapper JSON_MAPPER;

	static final String CODEC_VERSION_KEY = "codec-version";

	static final String CODEC_VERSION = "1";

	private static final String POINT = "point";

	private static final String RADIUS = "radius";

	private static final Map<String, String> savePaintMapping;

	private static final Map<String, String> loadPaintMapping;

	private static final Map<String, String> saveLayerToNullMapping;

	private static final Map<String, String> loadNullToLayerMapping;

	private final Product product;

	private final Map<Class<? extends DesignShape>, Function<DesignShape, Map<String, Object>>> geometryMappers;

	static {
		JSON_MAPPER = new ObjectMapper();
		JSON_MAPPER.registerModule( new SimpleModule().addSerializer( Point2D.class, new Point2DSerializer() ) );
		JSON_MAPPER.registerModule( new SimpleModule().addSerializer( Point3D.class, new Point3DSerializer() ) );
		JSON_MAPPER.registerModule( new SimpleModule().addSerializer( Color.class, new ColorSerializer() ) );

		savePaintMapping = Map.of( DesignDrawable.MODE_LAYER, "null", "null", "none" );
		loadPaintMapping = Map.of( "none", "null", "null", DesignDrawable.MODE_LAYER );
		saveLayerToNullMapping = Map.of( DesignDrawable.MODE_LAYER, "null", "null", "none" );
		loadNullToLayerMapping = Map.of( "none", "null", "null", DesignDrawable.MODE_LAYER );
	}

	public CartesiaDesignCodec( Product product ) {
		this.product = product;

		geometryMappers = new HashMap<>();
		geometryMappers.put( DesignMarker.class, m -> mapMarker( (DesignMarker)m ) );
		geometryMappers.put( DesignLine.class, m -> mapLine( (DesignLine)m ) );
		geometryMappers.put( DesignEllipse.class, m -> mapEllipse( (DesignEllipse)m ) );
		geometryMappers.put( DesignArc.class, m -> mapArc( (DesignArc)m ) );
		geometryMappers.put( DesignCurve.class, m -> mapCurve( (DesignCurve)m ) );
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
		Design2D design = asset.setModel( new Design2D() );

		Map<String, Object> map = JSON_MAPPER.readValue( input, new TypeReference<>() {} );

		log.log( Log.DEBUG, "Design codec version: " + map.get( CODEC_VERSION_KEY ) );

		Map<String, Map<String, Object>> layers = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.LAYERS, Map.of() );
		Map<String, Map<String, Object>> views = (Map<String, Map<String, Object>>)map.getOrDefault( Design.VIEWS, Map.of() );

		design.updateFrom( map );

		// Load layers
		layers.values().forEach( l -> loadLayer( design.getRootLayer(), l ) );

		// Load views
		views.values().forEach( v -> design.addView( new DesignView().updateFrom( v ) ) );

		asset.getUndoManager().forgetHistory();
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {
		Map<String, Object> map = mapDesign( asset.getModel() );
		map.put( CODEC_VERSION_KEY, CODEC_VERSION );
		JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValue( output, map );
		//System.err.println( JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString( map ) );
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
				case DesignMarker.MARKER, POINT -> loadDesignMarker( g );
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

	private void loadDesignNode( Map<String, Object> map, DesignNode node ) {
		if( map.containsKey( DesignNode.ID ) ) node.setId( (String)map.get( DesignNode.ID ) );
	}

	private void loadDesignDrawable( Map<String, Object> map, DesignDrawable drawable ) {
		loadDesignNode( map, drawable );

		// Fix bad data
		String drawPattern = (String)map.get( DesignDrawable.DRAW_PATTERN );
		if( "0".equals( drawPattern ) ) drawPattern = null;
		if( "".equals( drawPattern ) ) drawPattern = null;

		if( map.containsKey( DesignDrawable.ORDER ) ) drawable.setOrder( (Integer)map.get( DesignDrawable.ORDER ) );
		drawable.setDrawPaint( map.containsKey( DesignDrawable.DRAW_PAINT ) ? (String)map.get( DesignDrawable.DRAW_PAINT ) : null );
		if( map.containsKey( DesignDrawable.DRAW_WIDTH ) ) drawable.setDrawWidth( (String)map.get( DesignDrawable.DRAW_WIDTH ) );
		if( map.containsKey( DesignDrawable.DRAW_CAP ) ) drawable.setDrawCap( (String)map.get( DesignDrawable.DRAW_CAP ) );
		if( map.containsKey( DesignDrawable.DRAW_PATTERN ) ) drawable.setDrawPattern( drawPattern );
		drawable.setFillPaint( map.containsKey( DesignDrawable.FILL_PAINT ) ? (String)map.get( DesignDrawable.FILL_PAINT ) : null );
	}

	private <T extends DesignShape> T loadDesignShape( Map<String, Object> map, T shape ) {
		loadDesignDrawable( map, shape );
		if( map.containsKey( DesignShape.ORIGIN ) ) shape.setOrigin( ParseUtil.parsePoint3D( (String)map.get( DesignShape.ORIGIN ) ) );
		return shape;
	}

	private DesignMarker loadDesignMarker( Map<String, Object> map ) {
		DesignMarker marker = loadDesignShape( map, new DesignMarker() );
		marker.setSize( (String)map.get( DesignMarker.SIZE ) );
		marker.setType( (String)map.get( DesignMarker.TYPE ) );
		return marker;
	}

	private DesignLine loadDesignLine( Map<String, Object> map ) {
		DesignLine line = loadDesignShape( map, new DesignLine() );
		line.setPoint( ParseUtil.parsePoint3D( (String)map.get( DesignLine.POINT ) ) );
		return line;
	}

	private <T extends DesignEllipse> T loadDesignEllipse( Map<String, Object> map, T ellipse ) {
		if( map.containsKey( RADIUS ) ) ellipse.setXRadius( (Double)map.get( RADIUS ) );
		if( map.containsKey( RADIUS ) ) ellipse.setYRadius( (Double)map.get( RADIUS ) );
		if( map.containsKey( DesignEllipse.X_RADIUS ) ) ellipse.setXRadius( (Double)map.get( DesignEllipse.X_RADIUS ) );
		if( map.containsKey( DesignEllipse.Y_RADIUS ) ) ellipse.setYRadius( (Double)map.get( DesignEllipse.Y_RADIUS ) );
		if( map.containsKey( DesignEllipse.ROTATE ) ) ellipse.setRotate( (Double)map.get( DesignEllipse.ROTATE ) );
		return ellipse;
	}

	private DesignEllipse loadDesignEllipse( Map<String, Object> map ) {
		return loadDesignEllipse( map, loadDesignShape( map, new DesignEllipse() ) );
	}

	private DesignArc loadDesignArc( Map<String, Object> map ) {
		DesignArc arc = loadDesignEllipse( map, loadDesignShape( map, new DesignArc() ) );
		if( map.containsKey( DesignArc.START ) ) arc.setStart( (Double)map.get( DesignArc.START ) );
		if( map.containsKey( DesignArc.EXTENT ) ) arc.setExtent( (Double)map.get( DesignArc.EXTENT ) );
		if( map.containsKey( DesignArc.TYPE ) ) arc.setType( DesignArc.Type.valueOf( ((String)map.get( DesignArc.TYPE )).toUpperCase() ) );
		return arc;
	}

	private DesignCurve loadDesignCurve( Map<String, Object> map ) {
		DesignCurve curve = loadDesignShape( map, new DesignCurve() );
		curve.setOriginControl( ParseUtil.parsePoint3D( (String)map.get( DesignCurve.ORIGIN_CONTROL ) ) );
		curve.setPointControl( ParseUtil.parsePoint3D( (String)map.get( DesignCurve.POINT_CONTROL ) ) );
		curve.setPoint( ParseUtil.parsePoint3D( (String)map.get( DesignCurve.POINT ) ) );
		return curve;
	}

	private void moveKey( Map<String, Object> map, String oldKey, String newKey ) {
		if( !map.containsKey( oldKey ) ) return;
		map.put( newKey, map.get( oldKey ) );
		map.remove( oldKey );
	}

	private Map<String, Object> mapDesign( Design design ) {
		Map<String, Object> map = new HashMap<>( asMap( design, Design.ID, Design.NAME ) );
		map.put( DesignLayer.LAYERS, design.getRootLayer().getLayers().stream().collect( Collectors.toMap( IdNode::getId, this::mapLayer ) ) );
		return map;
	}

	private Map<String, Object> mapLayer( DesignLayer layer ) {
		Map<String, Object> map = new HashMap<>( asMap( layer, mapDrawable( layer ), Design.NAME ) );
		map.put( DesignLayer.LAYERS, layer.getLayers().stream().collect( Collectors.toMap( IdNode::getId, this::mapLayer ) ) );
		map.put( DesignLayer.SHAPES, layer.getShapes().stream().filter( s -> !s.isReference() ).collect( Collectors.toMap( IdNode::getId, this::mapGeometry ) ) );
		return map;
	}

	private Map<String, Object> mapDesignNode( DesignNode node ) {
		return asMap( node, DesignNode.ID );
	}

	private Map<String, Object> mapDrawable( DesignDrawable drawable ) {
		return asMap(
			drawable,
			mapDesignNode( drawable ),
			DesignDrawable.ORDER,
			DesignDrawable.DRAW_PAINT,
			DesignDrawable.DRAW_WIDTH,
			DesignDrawable.DRAW_PATTERN,
			DesignDrawable.DRAW_CAP,
			DesignDrawable.FILL_PAINT
		);
	}

	private Map<String, Object> mapGeometry( DesignShape shape ) {
		return geometryMappers.get( shape.getClass() ).apply( shape );
	}

	private Map<String, Object> mapShape( DesignShape shape, String type ) {
		Map<String, Object> map = asMap( shape, mapDrawable( shape ), DesignShape.ORIGIN );
		map.put( DesignShape.SHAPE, type );

		// Value mapping
		remapValue( map, DesignDrawable.DRAW_PAINT, savePaintMapping );
		remapValue( map, DesignDrawable.DRAW_WIDTH, saveLayerToNullMapping );
		remapValue( map, DesignDrawable.DRAW_CAP, saveLayerToNullMapping );
		remapValue( map, DesignDrawable.DRAW_PATTERN, saveLayerToNullMapping );
		remapValue( map, DesignDrawable.FILL_PAINT, savePaintMapping );

		return map;
	}

	private Map<String, Object> mapMarker( DesignMarker marker ) {
		return asMap( marker, mapShape( marker, DesignMarker.MARKER ), DesignMarker.SIZE, DesignMarker.TYPE );
	}

	private Map<String, Object> mapLine( DesignLine line ) {
		return asMap( line, mapShape( line, DesignLine.LINE ), DesignLine.POINT );
	}

	private Map<String, Object> mapRadius( DesignEllipse ellipse ) {
		Map<String, Object> map = new HashMap<>();
		if( CadGeometry.areSameSize( ellipse.getXRadius(), ellipse.getYRadius() ) ) {
			map.put( RADIUS, ellipse.getValue( DesignEllipse.X_RADIUS ) );
		} else {
			map.putAll( asMap( ellipse, DesignEllipse.X_RADIUS, DesignEllipse.Y_RADIUS ) );
		}
		return map;
	}

	private Map<String, Object> mapEllipse( DesignEllipse ellipse ) {
		String shape = Objects.equals( ellipse.getXRadius(), ellipse.getYRadius() ) ? DesignEllipse.CIRCLE : DesignEllipse.ELLIPSE;
		Map<String, Object> map = asMap( ellipse, mapShape( ellipse, shape ), DesignEllipse.ROTATE );
		map.putAll( mapRadius( ellipse ) );
		return map;
	}

	private Map<String, Object> mapArc( DesignArc arc ) {
		Map<String, Object> map = asMap( arc, mapShape( arc, DesignArc.ARC ), DesignArc.ROTATE, DesignArc.START, DesignArc.EXTENT, DesignArc.TYPE );
		map.putAll( mapRadius( arc ) );
		return map;
	}

	private Map<String, Object> mapCurve( DesignCurve curve ) {
		return asMap( curve, mapShape( curve, DesignCurve.CURVE ), DesignCurve.ORIGIN_CONTROL, DesignCurve.POINT_CONTROL, DesignCurve.POINT );
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

	private static Map<String, Object> asMap( Node node, String... keys ) {
		return asMap( node, Map.of(), keys );
	}

	private static Map<String, Object> asMap( Node node, Map<String, Object> map, String... keys ) {
		Map<String, Object> result = new HashMap<>( map );
		result.putAll( Arrays.stream( keys ).filter( k -> node.getValue( k ) != null ).collect( Collectors.toMap( k -> k, node::getValue ) ) );
		return result;
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
