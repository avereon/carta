package com.avereon.cartesia.tool;

import com.avereon.cartesia.BaseCartesiaTest;
import com.avereon.cartesia.Design2dAssetType;
import com.avereon.xenon.ProgramTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.concurrent.Future;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FxRenderDesignToolTest extends BaseCartesiaTest {

	private FxRenderDesignTool tool;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();

		URI uri = getClass().getResource( "/design-tool-test.cartesia2d" ).toURI();
		Future<ProgramTool> future = getProgram().getAssetManager().openAsset( uri, FxRenderDesignTool.class );
		tool = (FxRenderDesignTool)future.get();
		assertNotNull( tool );
	}

	@Test
	void testAssetTypeResolvedCorrectly() {
		assertThat( tool.getAsset().getType() ).isInstanceOf( Design2dAssetType.class );
	}

}
