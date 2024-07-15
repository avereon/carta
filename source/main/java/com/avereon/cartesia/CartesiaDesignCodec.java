package com.avereon.cartesia;

import com.avereon.cartesia.data.*;
import com.avereon.data.IdNode;
import com.avereon.data.Node;
import com.avereon.log.LazyEval;
import com.avereon.product.Product;
import com.avereon.util.TextUtil;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.Codec;
import com.avereon.zarra.font.FontUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.CustomLog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.avereon.cartesia.data.DesignCubic.CUBIC;

@CustomLog
public abstract class CartesiaDesignCodec extends Codec {

	static final ObjectMapper JSON_MAPPER;

	static final String CODEC_VERSION_KEY = "codec-version";

	static final String CODEC_VERSION = "1";

	private static final String POINT = "point";

	/**
	 * @deprecated Maintained for backward compatibility only
	 */
	@Deprecated
	@SuppressWarnings( "DeprecatedIsStillUsed" )
	private static final String RADIUS = "radius";

	/**
	 * @deprecated Maintained for backward compatibility only
	 */
	@Deprecated
	@SuppressWarnings( "DeprecatedIsStillUsed" )
	private static final String X_RADIUS = "x-radius";

	/**
	 * @deprecated Maintained for backward compatibility only
	 */
	@Deprecated
	@SuppressWarnings( "DeprecatedIsStillUsed" )
	private static final String Y_RADIUS = "y-radius";

	static final Map<String, String> saveLayerPaintMapping;

	static final Map<String, String> loadLayerPaintMapping;

	static final Map<String, String> saveLayerPropertyMapping;

	static final Map<String, String> loadLayerPropertyMapping;

	static final Map<String, String> savePaintMapping;

	static final Map<String, String> loadPaintMapping;

	static final Map<String, String> savePropertyMapping;

	static final Map<String, String> loadPropertyMapping;

	private final Product product;

	private final Map<Class<? extends DesignShape>, Function<DesignShape, Map<String, Object>>> geometryMappers;

	static {
		JSON_MAPPER = new ObjectMapper();
		JSON_MAPPER.configure( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true );
		JSON_MAPPER.registerModule( new SimpleModule().addSerializer( Color.class, new ColorSerializer() ) );
		JSON_MAPPER.registerModule( new SimpleModule().addSerializer( Font.class, new FontSerializer() ) );
		JSON_MAPPER.registerModule( new SimpleModule().addSerializer( Point2D.class, new Point2DSerializer() ) );
		JSON_MAPPER.registerModule( new SimpleModule().addSerializer( Point3D.class, new Point3DSerializer() ) );

		saveLayerPaintMapping = Map.of( "none", "null" );
		loadLayerPaintMapping = Map.of();
		saveLayerPropertyMapping = Map.of();
		loadLayerPropertyMapping = Map.of( "null", "null" );

		savePaintMapping = Map.of( "null", "none", DesignDrawable.MODE_LAYER, "null" );
		loadPaintMapping = Map.of( "none", "null", "null", DesignDrawable.MODE_LAYER );
		savePropertyMapping = Map.of( DesignDrawable.MODE_LAYER, "null" );
		loadPropertyMapping = Map.of( "null", DesignDrawable.MODE_LAYER );
	}

	public CartesiaDesignCodec( Product product ) {
		this.product = product;

		geometryMappers = new HashMap<>();
		geometryMappers.put( DesignBox.class, m -> mapBox( (DesignBox)m ) );
		geometryMappers.put( DesignLine.class, m -> mapLine( (DesignLine)m ) );
		geometryMappers.put( DesignEllipse.class, m -> mapEllipse( (DesignEllipse)m ) );
		geometryMappers.put( DesignArc.class, m -> mapArc( (DesignArc)m ) );
		geometryMappers.put( DesignQuad.class, m -> mapQuad( (DesignQuad)m ) );
		geometryMappers.put( DesignCubic.class, m -> mapCurve( (DesignCubic)m ) );
		geometryMappers.put( DesignPath.class, m -> mapPath( (DesignPath)m ) );
		geometryMappers.put( DesignMarker.class, m -> mapMarker( (DesignMarker)m ) );
		geometryMappers.put( DesignText.class, m -> mapText( (DesignText)m ) );
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
		design.updateFrom( map );

		log.atFine().log( "Design codec version: %s", LazyEval.of( () -> map.get( CODEC_VERSION_KEY ) ) );

		Map<String, Map<String, Object>> layers = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.LAYERS, Map.of() );
		Map<String, Map<String, Object>> views = (Map<String, Map<String, Object>>)map.getOrDefault( Design.VIEWS, Map.of() );

		// Load layers
		layers.values().forEach( l -> loadLayer( design.getLayers(), l ) );

		// Load views
		views.values().forEach( v -> loadView( design, v ) );

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

		// Geometry value mapping
		remapValue( map, DesignLayer.DRAW_PAINT, loadPaintMapping );
		remapValue( map, DesignLayer.DRAW_WIDTH, loadPropertyMapping );
		remapValue( map, DesignLayer.DRAW_CAP, loadPropertyMapping );
		remapValue( map, DesignLayer.DRAW_PATTERN, loadPropertyMapping );
		remapValue( map, DesignLayer.FILL_PAINT, loadPaintMapping );

		// Text value mapping
		remapValue( map, DesignLayer.TEXT_SIZE, loadLayerPropertyMapping );
		remapValue( map, DesignLayer.TEXT_FILL_PAINT, loadLayerPaintMapping );
		remapValue( map, DesignLayer.TEXT_DRAW_PAINT, loadLayerPaintMapping );
		remapValue( map, DesignLayer.TEXT_DRAW_WIDTH, loadLayerPropertyMapping );
		remapValue( map, DesignLayer.TEXT_DRAW_CAP, loadLayerPropertyMapping );
		remapValue( map, DesignLayer.TEXT_DRAW_PATTERN, loadLayerPropertyMapping );

		remapValue( map, DesignLayer.FONT_NAME, loadLayerPropertyMapping );
		remapValue( map, DesignLayer.FONT_WEIGHT, loadLayerPropertyMapping );
		remapValue( map, DesignLayer.FONT_POSTURE, loadLayerPropertyMapping );
		remapValue( map, DesignLayer.FONT_UNDERLINE, loadLayerPropertyMapping );
		remapValue( map, DesignLayer.FONT_STRIKETHROUGH, loadLayerPropertyMapping );

		// Clean values
		cleanPatternValue( map, DesignLayer.TEXT_DRAW_PATTERN );

		// Load text values
		layer.setTextFillPaint( map.containsKey( DesignLayer.TEXT_FILL_PAINT ) ? (String)map.get( DesignLayer.TEXT_FILL_PAINT ) : null );
		layer.setTextDrawPaint( map.containsKey( DesignLayer.TEXT_DRAW_PAINT ) ? (String)map.get( DesignLayer.TEXT_DRAW_PAINT ) : null );
		if( map.containsKey( DesignLayer.TEXT_DRAW_WIDTH ) ) layer.setTextDrawWidth( (String)map.get( DesignLayer.TEXT_DRAW_WIDTH ) );
		if( map.containsKey( DesignLayer.TEXT_DRAW_CAP ) ) layer.setTextDrawCap( (String)map.get( DesignLayer.TEXT_DRAW_CAP ) );
		if( map.containsKey( DesignLayer.TEXT_DRAW_PATTERN ) ) layer.setTextDrawPattern( (String)map.get( DesignLayer.TEXT_DRAW_PATTERN ) );

		if( map.containsKey( DesignLayer.TEXT_SIZE ) ) layer.setTextSize( (String)map.get( DesignLayer.TEXT_SIZE ) );
		if( map.containsKey( DesignLayer.FONT_NAME ) ) layer.setFontName( (String)map.get( DesignLayer.FONT_NAME ) );
		if( map.containsKey( DesignLayer.FONT_WEIGHT ) ) layer.setFontWeight( (String)map.get( DesignLayer.FONT_WEIGHT ) );
		if( map.containsKey( DesignLayer.FONT_POSTURE ) ) layer.setFontPosture( (String)map.get( DesignLayer.FONT_POSTURE ) );
		if( map.containsKey( DesignLayer.FONT_UNDERLINE ) ) layer.setFontUnderline( String.valueOf( map.get( DesignLayer.FONT_UNDERLINE ) ) );
		if( map.containsKey( DesignLayer.FONT_STRIKETHROUGH ) ) layer.setFontStrikethrough( String.valueOf( map.get( DesignLayer.FONT_STRIKETHROUGH ) ) );

		// Load layer geometry
		Map<String, Map<String, Object>> geometry = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.SHAPES, Map.of() );
		geometry.values().forEach( g -> {
			// Old keys
			moveKey( g, "draw-color", DesignDrawable.DRAW_PAINT );
			moveKey( g, "fill-color", DesignDrawable.FILL_PAINT );

			String type = String.valueOf( g.get( DesignShape.SHAPE ) );
			DesignShape shape = switch( type ) {
				case DesignBox.BOX -> loadDesignBox( g );
				case DesignLine.LINE -> loadDesignLine( g );
				case DesignEllipse.CIRCLE, DesignEllipse.ELLIPSE -> loadDesignEllipse( g );
				case DesignArc.ARC -> loadDesignArc( g );
				case DesignQuad.QUAD -> loadDesignQuad( g );
				case DesignCubic.CURVE, CUBIC -> loadDesignCurve( g );
				case DesignPath.PATH -> loadDesignPath( g );
				case DesignMarker.MARKER, POINT -> loadDesignMarker( g );
				case DesignText.TEXT -> loadDesignText( g );
				default -> null;
			};
			layer.addShape( shape );
		} );

		// Load child layers
		Map<String, Map<String, Object>> layers = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.LAYERS, Map.of() );
		layers.values().forEach( l -> loadLayer( layer, l ) );
	}

	private void cleanPatternValue( Map<String, Object> map, String key ) {
		String value = (String)map.get( key );
		if( "0".equals( value ) ) value = null;
		if( "".equals( value ) ) value = null;
		map.put( key, value );
	}

	private void loadDesignNode( Map<String, Object> map, DesignNode node ) {
		if( map.containsKey( DesignNode.ID ) ) node.setId( (String)map.get( DesignNode.ID ) );
	}

	@SuppressWarnings( "unchecked" )
	private void loadView( Design design, Map<String, Object> map ) {
		DesignView view = new DesignView();
		loadDesignNode( map, view );
		if( map.containsKey( DesignView.NAME ) ) view.setName( (String)map.get( DesignView.NAME ) );
		if( map.containsKey( DesignView.ORDER ) ) view.setOrder( Integer.parseInt( (String)map.get( DesignView.ORDER ) ) );
		if( map.containsKey( DesignView.ORIGIN ) ) view.setOrigin( ParseUtil.parsePoint3D( (String)map.get( DesignView.ORIGIN ) ) );
		if( map.containsKey( DesignView.ROTATE ) ) view.setRotate( ((Number)map.get( DesignView.ROTATE )).doubleValue() );
		if( map.containsKey( DesignView.ZOOM ) ) view.setZoom( ((Number)map.get( DesignView.ZOOM )).doubleValue() );

		List<String> layers = (List<String>)map.getOrDefault( DesignView.LAYERS, Set.of() );
		view.setLayers( layers.stream().map( design::getLayerById ).filter( Objects::nonNull ).collect( Collectors.toSet() ) );
		design.addView( view );
	}

	private void loadDesignDrawable( Map<String, Object> map, DesignDrawable drawable ) {
		loadDesignNode( map, drawable );

		// Clean values
		cleanPatternValue( map, DesignLayer.DRAW_PATTERN );

		// Geometry value mapping
		remapValue( map, DesignDrawable.DRAW_PAINT, loadPaintMapping );
		remapValue( map, DesignDrawable.DRAW_WIDTH, loadPropertyMapping );
		remapValue( map, DesignDrawable.DRAW_CAP, loadPropertyMapping );
		remapValue( map, DesignDrawable.DRAW_PATTERN, loadPropertyMapping );
		remapValue( map, DesignDrawable.FILL_PAINT, loadPaintMapping );

		if( map.containsKey( DesignDrawable.ORDER ) ) drawable.setOrder( (Integer)map.get( DesignDrawable.ORDER ) );
		drawable.setDrawPaint( map.containsKey( DesignDrawable.DRAW_PAINT ) ? (String)map.get( DesignDrawable.DRAW_PAINT ) : null );
		if( map.containsKey( DesignDrawable.DRAW_WIDTH ) ) drawable.setDrawWidth( (String)map.get( DesignDrawable.DRAW_WIDTH ) );
		if( map.containsKey( DesignDrawable.DRAW_CAP ) ) drawable.setDrawCap( (String)map.get( DesignDrawable.DRAW_CAP ) );
		if( map.containsKey( DesignDrawable.DRAW_PATTERN ) ) drawable.setDrawPattern( String.valueOf( map.get( DesignDrawable.DRAW_PATTERN ) ) );
		drawable.setFillPaint( map.containsKey( DesignDrawable.FILL_PAINT ) ? (String)map.get( DesignDrawable.FILL_PAINT ) : null );
	}

	private <T extends DesignShape> T loadDesignShape( Map<String, Object> map, T shape ) {
		loadDesignDrawable( map, shape );
		if( map.containsKey( DesignShape.ORIGIN ) ) shape.setOrigin( ParseUtil.parsePoint3D( (String)map.get( DesignShape.ORIGIN ) ) );
		if( shape.getOrigin() == null ) throw new RuntimeException( "Shape missing origin: " + shape.getId() );
		if( map.containsKey( DesignShape.ROTATE ) ) {
			Object rotate = map.get( DesignShape.ROTATE );
			if( rotate != null ) shape.setRotate( String.valueOf( rotate ) );
		}
		return shape;
	}

	private DesignBox loadDesignBox( Map<String, Object> map ) {
		DesignBox box = loadDesignShape( map, new DesignBox() );
		box.setSize( ParseUtil.parsePoint3D( (String)map.get( DesignBox.SIZE ) ) );
		return box;
	}

	private DesignLine loadDesignLine( Map<String, Object> map ) {
		DesignLine line = loadDesignShape( map, new DesignLine() );
		line.setPoint( ParseUtil.parsePoint3D( (String)map.get( DesignLine.POINT ) ) );
		return line;
	}

	private DesignEllipse loadDesignEllipse( Map<String, Object> map ) {
		return loadDesignEllipse( map, loadDesignShape( map, new DesignEllipse() ) );
	}

	private DesignArc loadDesignArc( Map<String, Object> map ) {
		DesignArc arc = loadDesignEllipse( map, loadDesignShape( map, new DesignArc() ) );
		// FIXME These should be strings at some point to allow for expressions
		if( map.containsKey( DesignArc.START ) ) arc.setStart( ((Number)map.get( DesignArc.START )).doubleValue() );
		if( map.containsKey( DesignArc.EXTENT ) ) arc.setExtent( ((Number)map.get( DesignArc.EXTENT )).doubleValue() );
		if( map.containsKey( DesignArc.TYPE ) ) arc.setType( DesignArc.Type.valueOf( ((String)map.get( DesignArc.TYPE )).toUpperCase() ) );
		return arc;
	}

	private <T extends DesignEllipse> T loadDesignEllipse( Map<String, Object> map, T ellipse ) {
		if( map.containsKey( DesignEllipse.RADII ) ) {
			ellipse.setRadii( ParseUtil.parsePoint3D( (String)map.get( DesignEllipse.RADII ) ) );
		} else if( map.containsKey( RADIUS ) ) {
			double radius = ((Number)map.get( RADIUS )).doubleValue();
			ellipse.setRadii( new Point3D( radius, radius, 0 ) );
		} else if( map.containsKey( X_RADIUS ) && map.containsKey( Y_RADIUS ) ) {
			// For backward compatibility
			double xRadius = ((Number)map.get( X_RADIUS )).doubleValue();
			double yRadius = ((Number)map.get( Y_RADIUS )).doubleValue();
			ellipse.setRadii( new Point3D( xRadius, yRadius, 0 ) );
		}

		return ellipse;
	}

	private DesignQuad loadDesignQuad( Map<String, Object> map ) {
		DesignQuad quad = loadDesignShape( map, new DesignQuad() );
		quad.setControl( ParseUtil.parsePoint3D( (String)map.get( DesignQuad.CONTROL ) ) );
		quad.setPoint( ParseUtil.parsePoint3D( (String)map.get( DesignQuad.POINT ) ) );
		return quad;
	}

	private DesignCubic loadDesignCurve( Map<String, Object> map ) {
		DesignCubic curve = loadDesignShape( map, new DesignCubic() );
		curve.setOriginControl( ParseUtil.parsePoint3D( (String)map.get( DesignCubic.ORIGIN_CONTROL ) ) );
		curve.setPointControl( ParseUtil.parsePoint3D( (String)map.get( DesignCubic.POINT_CONTROL ) ) );
		curve.setPoint( ParseUtil.parsePoint3D( (String)map.get( DesignCubic.POINT ) ) );
		return curve;
	}

	private DesignPath loadDesignPath( Map<String, Object> map ) {
		DesignPath path = loadDesignShape( map, new DesignPath() );
		// TODO Load path geometry
		return path;
	}

	private DesignMarker loadDesignMarker( Map<String, Object> map ) {
		DesignMarker marker = loadDesignShape( map, new DesignMarker() );
		marker.setSize( (String)map.get( DesignMarker.SIZE ) );
		marker.setType( (String)map.get( DesignMarker.TYPE ) );
		return marker;
	}

	private DesignText loadDesignText( Map<String, Object> map ) {
		DesignText text = loadDesignShape( map, new DesignText() );

		// Geometry value mapping
		if( map.containsKey( DesignText.TEXT ) ) text.setText( (String)map.get( DesignText.TEXT ) );
		if( map.containsKey( DesignText.TEXT_SIZE ) ) text.setTextSize( (String)map.get( DesignText.TEXT_SIZE ) );
		if( map.containsKey( DesignText.FONT_NAME ) ) text.setFontName( (String)map.get( DesignText.FONT_NAME ) );
		if( map.containsKey( DesignText.FONT_WEIGHT ) ) text.setFontWeight( (String)map.get( DesignText.FONT_WEIGHT ) );
		if( map.containsKey( DesignText.FONT_POSTURE ) ) text.setFontPosture( (String)map.get( DesignText.FONT_POSTURE ) );
		if( map.containsKey( DesignText.FONT_UNDERLINE ) ) text.setFontUnderline( (String)map.get( DesignText.FONT_UNDERLINE ) );
		if( map.containsKey( DesignText.FONT_STRIKETHROUGH ) ) text.setFontStrikethrough( (String)map.get( DesignText.FONT_STRIKETHROUGH ) );

		return text;
	}

	private void moveKey( Map<String, Object> map, String oldKey, String newKey ) {
		if( !map.containsKey( oldKey ) ) return;
		map.put( newKey, map.get( oldKey ) );
		map.remove( oldKey );
	}

	private Map<String, Object> mapDesign( Design design ) {
		Map<String, Object> map = new HashMap<>( asMap( design, Design.ID, Design.NAME, Design.AUTHOR, Design.DESCRIPTION, Design.UNIT ) );
		map.put( Design.LAYERS, design.getLayers().getLayers().stream().collect( Collectors.toMap( IdNode::getId, this::mapLayer ) ) );
		if( !design.getViews().isEmpty() ) map.put( Design.VIEWS, design.getViews().stream().collect( Collectors.toMap( IdNode::getId, this::mapView ) ) );
		return map;
	}

	private Map<String, Object> mapLayer( DesignLayer layer ) {
		Map<String, Object> map = new HashMap<>( asMap(
			layer,
			mapDrawable( layer ),
			Design.NAME,
			DesignLayer.TEXT_FILL_PAINT,
			DesignLayer.TEXT_DRAW_PAINT,
			DesignLayer.TEXT_DRAW_WIDTH,
			DesignLayer.TEXT_DRAW_PATTERN,
			DesignLayer.TEXT_DRAW_CAP,
			DesignLayer.TEXT_SIZE,
			DesignLayer.FONT_NAME,
			DesignLayer.FONT_WEIGHT,
			DesignLayer.FONT_POSTURE,
			DesignLayer.FONT_UNDERLINE,
			DesignLayer.FONT_STRIKETHROUGH
		) );

		remapValue( map, DesignLayer.DRAW_PAINT, saveLayerPaintMapping );
		remapValue( map, DesignLayer.FILL_PAINT, saveLayerPaintMapping );

		//		remapValue( map, DesignLayer.TEXT_FILL_PAINT, saveLayerPaintMapping );
		//		remapValue( map, DesignLayer.TEXT_DRAW_PAINT, saveLayerPaintMapping );
		//		remapValue( map, DesignLayer.TEXT_DRAW_WIDTH, saveLayerPropertyMapping );
		//		remapValue( map, DesignLayer.TEXT_DRAW_CAP, saveLayerPropertyMapping );
		//		remapValue( map, DesignLayer.TEXT_DRAW_PATTERN, saveLayerPropertyMapping );

		map.put( DesignLayer.LAYERS, layer.getLayers().stream().collect( Collectors.toMap( IdNode::getId, this::mapLayer ) ) );
		map.put( DesignLayer.SHAPES, layer.getShapes().stream().filter( s -> !s.isReference() ).collect( Collectors.toMap( IdNode::getId, this::mapGeometry ) ) );
		return map;
	}

	private Map<String, Object> mapDesignNode( DesignNode node ) {
		return asMap( node, DesignNode.ID );
	}

	private Map<String, Object> mapView( DesignView view ) {
		Map<String, Object> map = new HashMap<>( asMap( view, mapDesignNode( view ), DesignView.ORDER, DesignView.NAME, DesignView.ORIGIN, DesignView.ROTATE, DesignView.ZOOM ) );
		map.put( DesignView.LAYERS, view.getLayers().stream().map( IdNode::getId ).collect( Collectors.toSet() ) );
		return map;
	}

	private Map<String, Object> mapDrawable( DesignDrawable drawable ) {
		Map<String, Object> map = new HashMap<>( asMap(
			drawable,
			mapDesignNode( drawable ),
			DesignDrawable.ORDER,
			DesignDrawable.DRAW_PAINT,
			DesignDrawable.DRAW_WIDTH,
			DesignDrawable.DRAW_PATTERN,
			DesignDrawable.DRAW_CAP,
			DesignDrawable.FILL_PAINT
		) );

		// Remap geometry values as needed
		remapValue( map, DesignDrawable.DRAW_PAINT, savePaintMapping );
		remapValue( map, DesignDrawable.DRAW_WIDTH, savePropertyMapping );
		remapValue( map, DesignDrawable.DRAW_CAP, savePropertyMapping );
		remapValue( map, DesignDrawable.DRAW_PATTERN, savePropertyMapping );
		remapValue( map, DesignDrawable.FILL_PAINT, savePaintMapping );

		return map;
	}

	private Map<String, Object> mapGeometry( DesignShape shape ) {
		Function<DesignShape, Map<String, Object>> mapper = geometryMappers.get( shape.getClass() );
		if( mapper == null ) throw new NullPointerException( "No mapper for shape: " + shape.getClass().getSimpleName() );
		return mapper.apply( shape );
	}

	private Map<String, Object> mapShape( DesignShape shape, String type ) {
		Map<String, Object> map = asMap( shape, mapDrawable( shape ), DesignShape.ORIGIN, DesignShape.ROTATE );
		map.put( DesignShape.SHAPE, type );
		return map;
	}

	private Map<String, Object> mapBox( DesignBox box ) {
		return asMap( box, mapShape( box, DesignBox.BOX ), DesignBox.SIZE );
	}

	private Map<String, Object> mapLine( DesignLine line ) {
		return asMap( line, mapShape( line, DesignLine.LINE ), DesignLine.POINT );
	}

	private Map<String, Object> mapEllipse( DesignEllipse ellipse ) {
		String shape = Objects.equals( ellipse.getXRadius(), ellipse.getYRadius() ) ? DesignEllipse.CIRCLE : DesignEllipse.ELLIPSE;
		Map<String, Object> map = asMap( ellipse, mapShape( ellipse, shape ), DesignEllipse.RADII );
		//map.putAll( mapRadii( ellipse ) );
		return map;
	}

	private Map<String, Object> mapArc( DesignArc arc ) {
		Map<String, Object> map = asMap( arc, mapShape( arc, DesignArc.ARC ), DesignArc.RADII, DesignArc.ROTATE, DesignArc.START, DesignArc.EXTENT, DesignArc.TYPE );
		//map.putAll( mapRadii( arc ) );
		return map;
	}

	private Map<String, Object> mapRadii( DesignEllipse ellipse ) {
		return asMap( ellipse, DesignEllipse.RADII );
	}

	private Map<String, Object> mapQuad( DesignQuad curve ) {
		return asMap( curve, mapShape( curve, DesignQuad.QUAD ), DesignQuad.CONTROL, DesignQuad.POINT );
	}

	private Map<String, Object> mapCurve( DesignCubic curve ) {
		return asMap( curve, mapShape( curve, DesignCubic.CURVE ), DesignCubic.ORIGIN_CONTROL, DesignCubic.POINT_CONTROL, DesignCubic.POINT );
	}

	private Map<String, Object> mapPath( DesignPath path ) {
		// TODO Map path geometry
		return asMap( path, mapShape( path, DesignPath.PATH ) );
	}

	private Map<String, Object> mapMarker( DesignMarker marker ) {
		return asMap( marker, mapShape( marker, DesignMarker.MARKER ), DesignMarker.SIZE, DesignMarker.TYPE );
	}

	private Map<String, Object> mapText( DesignText text ) {
		return asMap(
			text,
			mapShape( text, DesignText.TEXT ),
			DesignText.TEXT,
			DesignText.TEXT_SIZE,
			DesignText.FONT_NAME,
			DesignText.FONT_WEIGHT,
			DesignText.FONT_POSTURE,
			DesignText.FONT_UNDERLINE,
			DesignText.FONT_STRIKETHROUGH
		);
	}

	static void remapValue( Map<String, Object> map, String key, Map<?, ?> values ) {
		Object currentValue = map.get( key );
		if( currentValue == null ) currentValue = "null";

		// The currentValue becomes the key for the values map
		Object newValue = values.get( currentValue );

		// If there is not a new value there is nothing to do
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

	public static class FontSerializer extends JsonSerializer<Font> {

		@Override
		public void serialize( Font value, JsonGenerator generator, SerializerProvider provider ) throws IOException {
			generator.writeString( FontUtil.encode( value ) );
		}

	}

}
