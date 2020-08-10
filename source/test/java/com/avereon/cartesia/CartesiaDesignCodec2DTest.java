package com.avereon.cartesia;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.Design2D;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.xenon.asset.Asset;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	void testLoad() throws Exception {
		// Generate a test design
		Design design = createTestDesign( new Design2D() );
		Map<String, ?> map = design.asDeepMap();

		// Load the design from a stream
		byte[] buffer = new ObjectMapper().writeValueAsBytes( map );
		codec.load( asset, new ByteArrayInputStream( buffer ) );

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

		// Check the result
		Map<String, ?> map = new ObjectMapper().readValue( output.toByteArray(), new TypeReference<Map<String, Object>>() {} );
		assertThat( map, is( design.asDeepMap() ) );
	}

	private Design createTestDesign( Design design ) {
		design.setName( "Test Design" );
		DesignLayer layer1 = new DesignLayer().setName( "Layer 1" );
		design.addLayer( layer1 );
		DesignLayer layer2 = new DesignLayer().setName( "Layer 2" );
		design.addLayer( layer2 );		return design;
	}
}
