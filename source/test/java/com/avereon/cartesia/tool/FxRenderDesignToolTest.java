package com.avereon.cartesia.tool;

import com.avereon.cartesia.BaseCartesiaUIT;
import com.avereon.xenon.asset.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// NEXT Well, time to make some choices. Either tool tests really require the
// program to be running, or we need to simplify how tool tests are run.

public class FxRenderDesignToolTest extends BaseCartesiaUIT {

	private FxRenderDesignTool tool;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		//Fx.startup();

		//getProgram().init();
		//AssetManager manager = new AssetManager( getProgram() );

		//Path path = Paths.get( "target", "design-tool-test.cartesia2d" );
		//Asset asset = new Asset( new Design2dAssetType( getMod() ), path.toUri() );

		Asset asset = getProgram().getAssetManager().createAsset( getClass().getResource( "/design-tool-test.cartesia2d" ) );
		tool = new FxRenderDesignTool( getMod(), asset );
	}

	@Test
	void canRun() {
		assertNotNull( tool );
	}

}
