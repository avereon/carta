package com.avereon.cartesia;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.Design2D;
import com.avereon.util.TextUtil;
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
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CartesiaDesignCodecTest extends BaseCartesiaTest {

	private Design2dAssetType assetType;

	private Asset asset;

	private Design design;

	@BeforeEach
	void setup() {
		super.setup();
		Path path = Paths.get( "target", "design.txt" );
		assetType = new Design2dAssetType( mod );
		asset = new Asset( path.toUri(), assetType );
		design = new Design2D();
	}

	@Test
	void testLoad() throws Exception {
		byte[] buffer = "{}".getBytes( TextUtil.CHARSET );
		CartesiaDesignCodec codec = new CartesiaDesignCodec( mod );
		ByteArrayInputStream input = new ByteArrayInputStream( buffer );
		codec.load( asset, input );

		// I can either check each piece
		// or create a duplicate model and compare
		// Not sure which
		//		Design design = asset.getModel();
		//		design.getLayers();
	}

	@Test
	void testSave() throws Exception {
		// Generate a test design
		Design design = new Design2D();
		design.setId( UUID.randomUUID().toString() );
		design.setName( "Test Design" );

		// Write the design to a stream
		CartesiaDesignCodec codec = new CartesiaDesignCodec( mod );
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		asset.setModel( design );
		codec.save( asset, output );

		// Parse the result
		Map<String, Object> map = new ObjectMapper().readValue( output.toByteArray(), new TypeReference<Map<String, Object>>() {} );

		// Verify the design values
		assertThat( map.get( "id" ), is( design.getId() ) );
		assertThat( map.get( "name" ), is( "Test Design" ) );
	}

}
