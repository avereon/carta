package com.avereon.cartesia;

import com.avereon.cartesia.data.*;
import com.avereon.util.MapUtil;
import com.avereon.xenon.asset.Asset;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class CartesiaDesignCodec2DTest extends BaseCartesiaTest {

	private static final ObjectMapper MAPPER = CartesiaDesignCodec.JSON_MAPPER;

	private CartesiaDesignCodec codec;

	private Asset asset;

	private static final Map<String, String> savePaintMapping;

	private static final Map<String, String> loadPaintMapping;

	private static final Map<String, String> saveLayerToNullMapping;

	private static final Map<String, String> loadNullToLayerMapping;

	static {
		savePaintMapping = Map.of( "null", "none", DesignDrawable.MODE_LAYER, "null" );
		loadPaintMapping = Map.of( "none", "null", "null", DesignDrawable.MODE_LAYER );
		saveLayerToNullMapping = Map.of( DesignDrawable.MODE_LAYER, "null" );
		loadNullToLayerMapping = Map.of( "null", DesignDrawable.MODE_LAYER );
	}

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		codec = new CartesiaDesignCodec2D( getMod() );

		Path path = Paths.get( "target", "design.tmp" );
		asset = new Asset( new Design2dAssetType( getMod() ), path.toUri() );
		asset.setModel( new Design2D() );
	}

	@Test
	void testMapper() throws Exception {
		ObjectWriter writer = MAPPER.writer();
		assertThat( writer.writeValueAsString( new Point2D( 1, 2 ) ) ).isEqualTo( "\"1.0,2.0\"" );
		assertThat( writer.writeValueAsString( new Point3D( 3, 2, 1 ) ) ).isEqualTo( "\"3.0,2.0,1.0\"" );
		assertThat( writer.writeValueAsString( Color.web( "0x20608080" ) ) ).isEqualTo( "\"0x20608080\"" );
	}

	@Test
	void testLoad() throws Exception {
		// Generate a test design
		Design design = createTestDesign( new Design2D() );
		Map<String, Object> deepMap = design.asDeepMap();

		Map<String, Object> map = new HashMap<>( deepMap );
		map.put( CartesiaDesignCodec.CODEC_VERSION_KEY, CartesiaDesignCodec.CODEC_VERSION );
		remapLayers( map, this::remapShapeForLoad );

		// Load the design from a stream
		byte[] buffer = MAPPER.writer().writeValueAsBytes( map );
		codec.load( asset, new ByteArrayInputStream( buffer ) );

		// Check the result
		assertThat( ((Design)asset.getModel()).asDeepMap() ).isEqualTo( deepMap );
	}

	@Test
	@SuppressWarnings( "unchecked" )
	void testSave() throws Exception {
		// Generate a test design
		Design design = createTestDesign( asset.getModel() );

		// Save the design to a stream
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		codec.save( asset, output );
		//System.out.println( prettyPrint( output.toByteArray() ) );

		// Check the result
		Map<String, Object> expected = MAPPER.readValue( MAPPER.writeValueAsBytes( design.asDeepMap() ), new TypeReference<>() {} );
		expected.put( CartesiaDesignCodec.CODEC_VERSION_KEY, CartesiaDesignCodec.CODEC_VERSION );
		remapLayers( expected, this::remapShapeForSave );

		// Any shape property that has the value "layer" is removed
		MapUtil
			.flatten( expected, "layers", "shapes" )
			.map( o -> (Map<String, Object>)o )
			.filter( shapes -> !shapes.isEmpty() )
			.flatMap( shapes -> shapes.values().parallelStream() )
			.map( o -> (Map<String, Object>)o )
			.forEach( shape -> {
				for( String key : new HashSet<>( shape.keySet() ) ) {
					shape.computeIfPresent( key, ( k, v ) -> "layer".equals( v ) ? null : v );
				}
			} );

		Map<String, Object> actual = MAPPER.readValue( output.toByteArray(), new TypeReference<>() {} );
		assertThat( actual ).isEqualTo( expected );
	}

	private Design createTestDesign( Design design ) {
		design.setName( "Test Design" );
		design.setAuthor( "Test Author" );
		design.setDescription( "Test design for unit tests." );
		design.setDesignUnit( DesignUnit.METER );
		DesignLayer layer0 = new DesignLayer().setName( "Layer 0 (Empty layer)" );
		design.getLayers().addLayer( layer0 );
		DesignLayer layer1 = new DesignLayer().setName( "Layer 1 (Marker)" );
		design.getLayers().addLayer( layer1 );
		DesignLayer layer2 = new DesignLayer().setName( "Layer 2 (Line)" );
		layer1.addLayer( layer2 );
		DesignLayer layer3 = new DesignLayer().setName( "Layer 3 (Ellipse)" );
		layer1.addLayer( layer3 );
		DesignLayer layer4 = new DesignLayer().setName( "Layer 4 (Arc)" );
		layer1.addLayer( layer4 );
		DesignLayer layer5 = new DesignLayer().setName( "Layer 5 (Curve)" );
		layer1.addLayer( layer5 );
		DesignLayer layer6 = new DesignLayer().setName( "Layer 6 (Text)" );
		layer1.addLayer( layer6 );

		DesignView view0 = new DesignView().setName( "View 0" );
		view0.setOrigin( Point3D.ZERO );
		view0.setRotate( 0.0 );
		view0.setZoom( 0.5 );
		view0.setLayers( Set.of( layer0, layer1, layer2 ) );
		design.addView( view0 );

		DesignView view1 = new DesignView().setName( "View 1" );
		view1.setOrigin( new Point3D( 1, 2, 0 ) );
		view1.setRotate( 45.0 );
		view1.setZoom( 2.0 );
		view1.setLayers( Set.of( layer3, layer5 ) );
		design.addView( view1 );

		DesignMarker marker = new DesignMarker( new Point3D( 1, 2, 0 ) );
		marker.setType( "normal" );
		marker.setSize( "1.5" );
		marker.setDrawPaint( "0x0000ff80" );
		layer1.addShape( marker );

		DesignLine line1 = new DesignLine( new Point3D( 2, 3, 0 ), new Point3D( 3, 4, 0 ) );
		line1.setDrawPaint( "0x0000ff80" );
		layer2.addShape( line1 );
		DesignLine line2 = new DesignLine( new Point3D( 2, 5, 0 ), new Point3D( 3, 6, 0 ) );
		layer2.addShape( line2 );

		DesignEllipse ellipse1 = new DesignEllipse( new Point3D( -2, 4, 0 ), 5.0 );
		layer3.addShape( ellipse1 );
		DesignEllipse ellipse2 = new DesignEllipse( new Point3D( -2, -4, 0 ), 5.0, 3.0, 73.0 );
		layer3.addShape( ellipse2 );

		DesignArc arc1 = new DesignArc( new Point3D( -2, 4, 0 ), 5.0, 90.0, 135.0, DesignArc.Type.OPEN );
		layer4.addShape( arc1 );
		DesignArc arc2 = new DesignArc( new Point3D( -2, -4, 0 ), 5.0, 3.0, 73.0, 14.0, 28.0, DesignArc.Type.CHORD );
		layer4.addShape( arc2 );

		DesignCurve curve1 = new DesignCurve( new Point3D( -5, 0, 0 ), new Point3D( 1, 5, 0 ), new Point3D( -1, -5, 0 ), new Point3D( 5, 0, 0 ) );
		layer5.addShape( curve1 );

		DesignText text1 = new DesignText( new Point3D( 2, 1, 0 ), "Test Text", Font.getDefault(), 0.0 );
		layer6.addShape( text1 );

		return design;
	}

	@SuppressWarnings( "unchecked" )
	private void remapLayers( Map<String, Object> map, Consumer<Map<String, Object>> shapeMapper ) {
		Map<String, Object> layers = (Map<String, Object>)map.get( "layers" );
		layers.values().parallelStream().map( o -> (Map<String, Object>)o ).forEach( m -> {
			remapLayer( m, shapeMapper );
			remapLayers( m, shapeMapper );
		} );
	}

	@SuppressWarnings( "unchecked" )
	private void remapLayer( Map<String, Object> map, Consumer<Map<String, Object>> shapeMapper ) {
		Map<String, Map<String, Object>> shapes = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.SHAPES, Map.of() );
		shapes.values().forEach( shapeMapper );
	}

	private void remapShapeForLoad( Map<String, Object> map ) {
		remapValue( map, DesignDrawable.DRAW_PAINT, loadPaintMapping );
		remapValue( map, DesignDrawable.DRAW_WIDTH, loadNullToLayerMapping );
		remapValue( map, DesignDrawable.DRAW_CAP, loadNullToLayerMapping );
		remapValue( map, DesignDrawable.DRAW_PATTERN, loadNullToLayerMapping );
		remapValue( map, DesignDrawable.FILL_PAINT, loadPaintMapping );
		remapValue( map, DesignDrawable.TEXT_FONT, loadNullToLayerMapping );
	}

	private void remapShapeForSave( Map<String, Object> map ) {
		remapValue( map, DesignDrawable.DRAW_PAINT, savePaintMapping );
		remapValue( map, DesignDrawable.DRAW_WIDTH, saveLayerToNullMapping );
		remapValue( map, DesignDrawable.DRAW_CAP, saveLayerToNullMapping );
		remapValue( map, DesignDrawable.DRAW_PATTERN, saveLayerToNullMapping );
		remapValue( map, DesignDrawable.FILL_PAINT, savePaintMapping );
		remapValue( map, DesignDrawable.TEXT_FONT, saveLayerToNullMapping );
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

}
