package com.avereon.cartesia;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.Design2D;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.geometry.CsaLine;
import com.avereon.cartesia.geometry.CsaPoint;
import com.avereon.util.TextUtil;
import com.avereon.xenon.asset.Asset;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
		byte[] buffer = new ObjectMapper().writer().writeValueAsBytes( map );
		System.out.println( prettyPrint( buffer ) );
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

		//System.out.println( prettyPrint( output.toByteArray() ) );

		// Check the result
		Map<String, ?> map = new ObjectMapper().readValue( output.toByteArray(), new TypeReference<Map<String, Object>>() {} );
		assertThat( map, is( design.asDeepMap() ) );
	}

	private Design createTestDesign( Design design ) {
		design.setName( "Test Design" );
		DesignLayer layer0 = new DesignLayer().setName( "Layer 0 (And empty layer)" );
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

	private String prettyPrint( byte[] buffer ) throws Exception {
		JsonNode node = new ObjectMapper().readValue( buffer, JsonNode.class );
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue( output, node );
		return new String( output.toByteArray(), TextUtil.CHARSET );
	}

}
