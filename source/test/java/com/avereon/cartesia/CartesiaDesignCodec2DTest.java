package com.avereon.cartesia;

import com.avereon.cartesia.data.*;
import com.avereon.xenon.asset.Asset;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.avereon.cartesia.CartesiaDesignCodec.remapValue;
import static org.assertj.core.api.Assertions.assertThat;

public class CartesiaDesignCodec2DTest extends BaseCartesiaTest {

	private static final ObjectMapper MAPPER = CartesiaDesignCodec.JSON_MAPPER;

	private CartesiaDesignCodec codec;

	private Asset asset;

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
		Map<String, Object> expectedMap = new HashMap<>( design.asDeepMap() );
		expectedMap.put( CartesiaDesignCodec.CODEC_VERSION_KEY, CartesiaDesignCodec.CODEC_VERSION );
		remapLayersForLoad( expectedMap, this::remapShapeForLoad );

		// Load the design from a stream
		byte[] buffer = MAPPER.writer().writeValueAsBytes( expectedMap );
		codec.load( asset, new ByteArrayInputStream( buffer ) );
		Map<String, Object> actualMap = ((Design)asset.getModel()).asDeepMap();
		actualMap.put( CartesiaDesignCodec.CODEC_VERSION_KEY, CartesiaDesignCodec.CODEC_VERSION );

		// Convert the results to strings for comparison
		String expected = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString( expectedMap );
		String actual = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString( actualMap );

		assertThat( actual ).isEqualTo( expected );
	}

	@Test
	@SuppressWarnings( "unchecked" )
	void testSave() throws Exception {
		// Create the expected result
		Design design = createTestDesign( asset.getModel() );
		Map<String, Object> expectedMap = new HashMap<>( design.asDeepMap() );
		expectedMap.put( CartesiaDesignCodec.CODEC_VERSION_KEY, CartesiaDesignCodec.CODEC_VERSION );
		remapLayersForSave( expectedMap, this::remapShapeForSave );

		// Create the actual result
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		codec.save( asset, output );

		// Convert the results to strings for comparison
		String expected = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString( expectedMap );
		String actual = output.toString( StandardCharsets.UTF_8 );

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
		ellipse2.setDrawWidth( "1/50" );
		layer3.addShape( ellipse2 );

		DesignArc arc1 = new DesignArc( new Point3D( -2, 4, 0 ), 5.0, 90.0, 135.0, DesignArc.Type.OPEN );
		layer4.addShape( arc1 );
		DesignArc arc2 = new DesignArc( new Point3D( -2, -4, 0 ), 5.0, 3.0, 73.0, 14.0, 28.0, DesignArc.Type.CHORD );
		layer4.addShape( arc2 );

		DesignCurve curve1 = new DesignCurve( new Point3D( -5, 0, 0 ), new Point3D( 1, 5, 0 ), new Point3D( -1, -5, 0 ), new Point3D( 5, 0, 0 ) );
		layer5.addShape( curve1 );

		DesignText text1 = new DesignText( new Point3D( 2, 1, 0 ), "Test Text" );
		layer6.addShape( text1 );

		return design;
	}

	@SuppressWarnings( "unchecked" )
	private void remapLayersForLoad( Map<String, Object> map, Consumer<Map<String, Object>> shapeMapper ) {
		Map<String, Object> layers = (Map<String, Object>)map.get( "layers" );
		layers.values().parallelStream().map( o -> (Map<String, Object>)o ).forEach( m -> {
			remapLayerForLoad( m, shapeMapper );
			remapLayersForLoad( m, shapeMapper );
		} );
	}

	@SuppressWarnings( "unchecked" )
	private void remapLayerForLoad( Map<String, Object> map, Consumer<Map<String, Object>> shapeMapper ) {
		Map<String, Map<String, Object>> shapes = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.SHAPES, Map.of() );

		remapValue( map, DesignLayer.FONT_NAME, CartesiaDesignCodec.loadLayerPropertyMapping );
		remapValue( map, DesignLayer.TEXT_FILL_PAINT, CartesiaDesignCodec.loadLayerPaintMapping );
		remapValue( map, DesignLayer.TEXT_DRAW_PAINT, CartesiaDesignCodec.loadLayerPaintMapping );
		remapValue( map, DesignLayer.TEXT_DRAW_WIDTH, CartesiaDesignCodec.loadLayerPropertyMapping );
		remapValue( map, DesignLayer.TEXT_DRAW_CAP, CartesiaDesignCodec.loadLayerPropertyMapping );
		remapValue( map, DesignLayer.TEXT_DRAW_PATTERN, CartesiaDesignCodec.loadLayerPropertyMapping );

		shapes.values().forEach( shapeMapper );
	}

	private void remapShapeForLoad( Map<String, Object> map ) {
		remapValue( map, DesignDrawable.DRAW_PAINT, CartesiaDesignCodec.loadPaintMapping );
		remapValue( map, DesignDrawable.DRAW_WIDTH, CartesiaDesignCodec.loadPropertyMapping );
		remapValue( map, DesignDrawable.DRAW_CAP, CartesiaDesignCodec.loadPropertyMapping );
		remapValue( map, DesignDrawable.DRAW_PATTERN, CartesiaDesignCodec.loadPropertyMapping );
		remapValue( map, DesignDrawable.FILL_PAINT, CartesiaDesignCodec.loadPaintMapping );

		if( DesignText.TEXT.equals( map.get( DesignShape.SHAPE ) ) ) {
			remapValue( map, DesignLayer.FONT_NAME, CartesiaDesignCodec.loadPaintMapping );
		}
	}

	@SuppressWarnings( "unchecked" )
	private void remapLayersForSave( Map<String, Object> map, Consumer<Map<String, Object>> shapeMapper ) {
		Map<String, Object> layers = (Map<String, Object>)map.get( "layers" );
		layers.values().parallelStream().map( o -> (Map<String, Object>)o ).forEach( m -> {
			remapLayerForSave( m, shapeMapper );
			remapLayersForSave( m, shapeMapper );
		} );
	}

	@SuppressWarnings( "unchecked" )
	private void remapLayerForSave( Map<String, Object> map, Consumer<Map<String, Object>> shapeMapper ) {
		Map<String, Map<String, Object>> shapes = (Map<String, Map<String, Object>>)map.getOrDefault( DesignLayer.SHAPES, Map.of() );

		remapValue( map, DesignLayer.TEXT_FILL_PAINT, CartesiaDesignCodec.saveLayerPaintMapping );
		remapValue( map, DesignLayer.TEXT_DRAW_PAINT, CartesiaDesignCodec.saveLayerPaintMapping );
		remapValue( map, DesignLayer.TEXT_DRAW_WIDTH, CartesiaDesignCodec.saveLayerPropertyMapping );
		remapValue( map, DesignLayer.TEXT_DRAW_CAP, CartesiaDesignCodec.saveLayerPropertyMapping );
		remapValue( map, DesignLayer.TEXT_DRAW_PATTERN, CartesiaDesignCodec.saveLayerPropertyMapping );
		remapValue( map, DesignLayer.FONT_NAME, CartesiaDesignCodec.savePropertyMapping );

		shapes.values().forEach( shapeMapper );
	}

	private void remapShapeForSave( Map<String, Object> map ) {
		remapValue( map, DesignDrawable.DRAW_PAINT, CartesiaDesignCodec.savePaintMapping );
		remapValue( map, DesignDrawable.DRAW_WIDTH, CartesiaDesignCodec.savePropertyMapping );
		remapValue( map, DesignDrawable.DRAW_CAP, CartesiaDesignCodec.savePropertyMapping );
		remapValue( map, DesignDrawable.DRAW_PATTERN, CartesiaDesignCodec.savePropertyMapping );
		remapValue( map, DesignDrawable.FILL_PAINT, CartesiaDesignCodec.savePaintMapping );
	}

}
