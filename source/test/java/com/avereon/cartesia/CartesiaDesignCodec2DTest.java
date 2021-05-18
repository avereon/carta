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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CartesiaDesignCodec2DTest extends BaseCartesiaTest {

	private static final ObjectMapper MAPPER = CartesiaDesignCodec.JSON_MAPPER;

	private CartesiaDesignCodec codec;

	private Asset asset;

	@BeforeEach
	void setup() {
		super.setup();
		codec = new CartesiaDesignCodec2D( getMod() );

		Path path = Paths.get( "target", "design.tmp" );
		asset = new Asset( path.toUri(), new Design2dAssetType( getMod() ) );
		asset.setModel( new Design2D() );
	}

	@Test
	void testMapper() throws Exception {
		ObjectWriter writer = MAPPER.writer();
		assertThat( writer.writeValueAsString( new Point2D( 1, 2 ) ), is( "\"1.0,2.0\"" ) );
		assertThat( writer.writeValueAsString( new Point3D( 3, 2, 1 ) ), is( "\"3.0,2.0,1.0\"" ) );
		assertThat( writer.writeValueAsString( Color.web( "0x20608080" ) ), is( "\"0x20608080\"" ) );
	}

	@Test
	void testLoad() throws Exception {
		// Generate a test design
		Design design = createTestDesign( new Design2D() );
		Map<String, Object> designMap = design.asDeepMap();

		Map<String, Object> map = new HashMap<>( designMap );
		map.put( CartesiaDesignCodec.CODEC_VERSION_KEY, CartesiaDesignCodec.CODEC_VERSION );

		// Load the design from a stream
		byte[] buffer = MAPPER.writer().writeValueAsBytes( map );
		codec.load( asset, new ByteArrayInputStream( buffer ) );
		//System.out.println( codec.prettyPrint( buffer ) );

		// Check the result
		assertThat( ((Design)asset.getModel()).asDeepMap(), is( designMap ) );
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
		assertThat( actual, is( expected ) );
	}

	private Design createTestDesign( Design design ) {
		design.setName( "Test Design" );
		DesignLayer layer0 = new DesignLayer().setName( "Layer 0 (Empty layer)" );
		design.getRootLayer().addLayer( layer0 );
		DesignLayer layer1 = new DesignLayer().setName( "Layer 1" );
		design.getRootLayer().addLayer( layer1 );
		DesignLayer layer2 = new DesignLayer().setName( "Layer 2" );
		layer1.addLayer( layer2 );
		DesignLayer layer3 = new DesignLayer().setName( "Layer 3" );
		layer1.addLayer( layer3 );
		DesignLayer layer4 = new DesignLayer().setName( "Layer 4" );
		layer1.addLayer( layer4 );
		DesignLayer layer5 = new DesignLayer().setName( "Layer 5" );
		layer1.addLayer( layer5 );

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

		return design;
	}

}
