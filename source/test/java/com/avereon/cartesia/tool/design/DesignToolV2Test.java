package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.BaseCartesiaUiTest;
import com.avereon.cartesia.Design2dAssetType;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.ProgramToolEvent;
import com.avereon.xenon.asset.Asset;
import com.avereon.zarra.event.FxEventWatcher;
import javafx.geometry.Point3D;
import javafx.stage.Screen;
import lombok.CustomLog;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Getter
@CustomLog
public class DesignToolV2Test extends BaseCartesiaUiTest {

	private DesignToolV2 tool;

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();

		// Load the design asset into a tool
		URI uri = Objects.requireNonNull( getClass().getResource( "/design-tool-test.cartesia2d" ) ).toURI();
		Future<ProgramTool> future = getProgram().getAssetManager().openAsset( uri, DesignToolV2.class );
		tool = (DesignToolV2)future.get();

		// Wait for the tool to be ready
		FxEventWatcher eventWatcher = new FxEventWatcher();
		tool.addEventHandler( ProgramToolEvent.READY, eventWatcher );
		eventWatcher.waitForEvent( ProgramToolEvent.READY );

		// Ensure the test resources are available
		assertNotNull( getTool() );
		assertNotNull( getAsset() );
		assertNotNull( getDesign() );

		// Check the design state
		assertThat( getDesign().getAllLayers().size() ).isEqualTo( 10 );

		// Check the tool state
		assertThat( getTool().getVisibleLayers().size() ).isEqualTo( 10 );
		assertThat( getTool().getVisibleGeometry().size() ).isEqualTo( 2 );

		System.out.println( "Primary DPI=" + Screen.getPrimary().getDpi() );
		System.out.println( "Tool    DPI=" + getTool().getDpi() );
		//getTool().getDpi();
	}

	@Test
	void assetTypeResolvesCorrectly() {
		assertThat( getAsset().getType() ).isInstanceOf( Design2dAssetType.class );
	}

	@Test
	void getVisibleLayers() {
		assertThat( getTool().getVisibleLayers().size() ).isEqualTo( 10 );
	}

	@Test
	void getCurrentLayer() {
		DesignLayer firstLayer = getDesign().getAllLayers().getFirst();
		assertThat( getTool().getCurrentLayer() ).isEqualTo( firstLayer );
	}

	@Test
	void screenPointSelect() {
		// given
		Point3D mouse = getTool().worldToScreen( new Point3D( 0, 0, 0 ) );

		// when - select once
		getTool().screenPointSelect( mouse, false );

		// then - the first line should be selected
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.size() ).isEqualTo( 1 );
		assertThat( selected.getFirst().getOrigin() ).isEqualTo( new Point3D( -2, 2, 0 ) );

//		// when - select again
//		getTool().screenPointSelect( mouse, false );
//
//		// then - the second line should be selected
//		selected = getTool().getSelectedGeometry();
//		assertThat( selected.size() ).isEqualTo( 1 );
//		assertThat( selected.getFirst().getOrigin() ).isEqualTo( new Point3D( -2, -2, 0 ) );
	}

	protected Asset getAsset() {
		return tool.getAsset();
	}

	protected Design getDesign() {
		return getAsset().getModel();
	}

}
