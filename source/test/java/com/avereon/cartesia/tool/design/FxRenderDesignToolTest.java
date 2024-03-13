package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.BaseCartesiaUiTest;
import com.avereon.cartesia.Design2dAssetType;
import com.avereon.cartesia.data.Design;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.ProgramToolEvent;
import com.avereon.xenon.asset.Asset;
import com.avereon.zarra.event.FxEventWatcher;
import lombok.CustomLog;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.Future;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Getter
@CustomLog
public class FxRenderDesignToolTest extends BaseCartesiaUiTest {

	private FxRenderDesignTool tool;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();

		// Load the design asset into a tool
		URI uri = Objects.requireNonNull( getClass().getResource( "/design-tool-test.cartesia2d" ) ).toURI();
		Future<ProgramTool> future = getProgram().getAssetManager().openAsset( uri, FxRenderDesignTool.class );
		tool = (FxRenderDesignTool)future.get();

		// Wait for the tool to be ready
		FxEventWatcher eventWatcher = new FxEventWatcher();
		tool.addEventHandler( ProgramToolEvent.READY, eventWatcher );
		eventWatcher.waitForEvent( ProgramToolEvent.READY );

		// Ensure the test resources are available
		assertNotNull( getTool() );
		assertNotNull( getAsset() );
		assertNotNull( getDesign() );
	}

	@Test
	void testAssetTypeResolvedCorrectly() {
		assertThat( getAsset().getType() ).isInstanceOf( Design2dAssetType.class );
	}

	@Test
	void testVisibleLayers() {
		assertThat( getDesign().getAllLayers().size() ).isEqualTo( 1 );
		assertThat( getTool().getVisibleLayers().size() ).isEqualTo( 1 );
	}

	protected Asset getAsset() {
		return tool.getAsset();
	}

	protected Design getDesign() {
		return getAsset().getModel();
	}

}
