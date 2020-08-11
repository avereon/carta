package com.avereon.cartesia;

import com.avereon.cartesia.data.*;
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
		codec = new CartesiaDesignCodec2D( mod );

		Path path = Paths.get( "target", "design.tmp" );
		asset = new Asset( path.toUri(), new Design2dAssetType( mod ) );
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
		Map<String, ?> map = design.asDeepMap();

		// Load the design from a stream
		byte[] buffer = MAPPER.writer().writeValueAsBytes( map );
		codec.load( asset, new ByteArrayInputStream( buffer ) );
		//System.out.println( codec.prettyPrint( buffer ) );

		// Check the result
		assertThat( ((Design)asset.getModel()).asDeepMap(), is( map ) );
	}

	@Test
	void testSave() throws Exception {
		// Generate a test design
		Design design = createTestDesign( asset.getModel() );

		// Save the design to a stream
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		codec.save( asset, output );
		//System.out.println( prettyPrint( output.toByteArray() ) );

		// Check the result
		Map<String, ?> actual = MAPPER.readValue( output.toByteArray(), new TypeReference<>() {} );
		Map<String,? > expected = MAPPER.readValue( MAPPER.writeValueAsBytes( design.asDeepMap() ), new TypeReference<>() {} );
		assertThat( actual, is( expected ) );
	}

	private Design createTestDesign( Design design ) {
		design.setName( "Test Design" );
		DesignLayer layer0 = new DesignLayer().setName( "Layer 0 (Empty layer)" );
		design.addLayer( layer0 );
		DesignLayer layer1 = new DesignLayer().setName( "Layer 1" );
		design.addLayer( layer1 );
		DesignLayer layer2 = new DesignLayer().setName( "Layer 2" );
		design.addLayer( layer2 );

		CsaPoint point = new CsaPoint( new Point3D( 1, 2, 0 ) );
		point.setDrawColor( Color.web( "0x0000ff80" ) );
		layer1.addShape( point );

		CsaLine line1 = new CsaLine( new Point3D( 2, 3, 0 ), new Point3D( 3, 4, 0 ) );
		line1.setDrawColor( Color.GREEN );
		layer2.addShape( line1 );
		CsaLine line2 = new CsaLine( new Point3D( 2, 5, 0 ), new Point3D( 3, 6, 0 ) );
		layer2.addShape( line2 );

		return design;
	}

}
