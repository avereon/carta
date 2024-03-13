package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.BaseCartesiaUiTest;
import com.avereon.cartesia.Design2dAssetType;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.asset.Asset;
import com.avereon.zarra.event.FxEventWatcher;
import lombok.CustomLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.Future;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@CustomLog
public class FxRenderDesignToolTest extends BaseCartesiaUiTest {

	private FxRenderDesignTool tool;

	private Asset asset;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();

		URI uri = Objects.requireNonNull( getClass().getResource( "/design-tool-test.cartesia2d" ) ).toURI();
		Future<ProgramTool> future = getProgram().getAssetManager().openAsset( uri, FxRenderDesignTool.class );
		tool = (FxRenderDesignTool)future.get();
		asset = tool.getAsset();
		assertNotNull( tool );
		assertNotNull( asset );

		// Wait for the design to be ready
		FxEventWatcher eventWatcher = new FxEventWatcher();
		tool.addEventHandler( DesignToolEvent.DESIGN_READY, eventWatcher );
		eventWatcher.waitForEvent( DesignToolEvent.DESIGN_READY );
	}

	@Test
	void testAssetTypeResolvedCorrectly() {
		assertThat( tool.getAsset().getType() ).isInstanceOf( Design2dAssetType.class );
	}

	@Test
//	@Disabled
	void testVisibleLayers() {
		// FIXME Why are there no layers?
		assertThat( tool.getDesign().getAllLayers().size() ).isEqualTo( 1 );
		assertThat( tool.getVisibleLayers().size() ).isEqualTo( 1 );
	}

}
