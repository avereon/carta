package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.BaseCartesiaUiTest;
import com.avereon.cartesia.Design2dAssetType;
import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.ProgramToolEvent;
import com.avereon.xenon.asset.Asset;
import com.avereon.zarra.event.FxEventWatcher;
import com.avereon.zarra.javafx.Fx;
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
public class DesignToolV2TestUIT extends BaseCartesiaUiTest {

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

		Fx.run( () -> tool.setZoom( 2 ) );
		Fx.waitForWithExceptions( 1000 );

		// Ensure the test resources are available
		assertNotNull( getTool() );
		assertNotNull( getAsset() );
		assertNotNull( getDesign() );

		// Check the design state
		assertThat( getDesign().calcDesignUnit() ).isEqualTo( DesignUnit.CENTIMETER );
		assertThat( getDesign().getAllLayers().size() ).isEqualTo( 10 );

		// Check the tool state
		assertThat( getTool().getDpi() ).isEqualTo( Screen.getPrimary().getDpi() );
		assertThat( getTool().getZoom() ).isEqualTo( 2 );
		assertThat( getTool().getVisibleLayers().size() ).isEqualTo( 10 );
		assertThat( getTool().getVisibleGeometry().size() ).isEqualTo( 2 );
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
	}

	@Test
	void screenPointSelectWithMultipleSelectsMovingDownVisibleGeometry() {
		// given
		Point3D mouse = getTool().worldToScreen( new Point3D( 0, 0, 0 ) );

		// when - select once
		getTool().screenPointSelect( mouse, false );

		// then - the first line should be selected
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.size() ).isEqualTo( 1 );
		assertThat( selected.getFirst().getOrigin() ).isEqualTo( new Point3D( -2, 2, 0 ) );

		// when - select again
		getTool().screenPointSelect( mouse, false );

		// TODO Implement cascading select functionality

		//		// then - the second line should be selected
		//		selected = getTool().getSelectedGeometry();
		//		assertThat( selected.size() ).isEqualTo( 1 );
		//		assertThat( selected.getFirst().getOrigin() ).isEqualTo( new Point3D( -2, -2, 0 ) );
	}

	@Test
	void screenPointSelectLineWithMouseCloseEnough() {
		// given
		DesignValue selectTolerance = getTool().getSelectTolerance().to( getDesign().calcDesignUnit() );
		double offsetValue = selectTolerance.getValue() / getTool().getZoom();

		// Need to get the selector inside the stroke width of the line
		// 0.02 is just under half the line stroke width

		Point3D offset = new Point3D( 1.02 * offsetValue, 0, 0 );
		Point3D point = new Point3D( 2, 2, 0 ).add( offset );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.size() ).isEqualTo( 1 );
		assertThat( selected.getFirst().getOrigin() ).isEqualTo( new Point3D( -2, -2, 0 ) );
	}

	@Test
	void screenPointSelectLineWithMouseTooFarAway() {
		// given
		DesignValue selectTolerance = getTool().getSelectTolerance().to( getDesign().calcDesignUnit() );
		double offsetValue = selectTolerance.getValue() / getTool().getZoom();

		// Need to get the selector outside the stroke width of the line
		// 0.03 is just over half the line stroke width

		Point3D offset = new Point3D( 1.03 * offsetValue, 0, 0 );
		Point3D point = new Point3D( 2, 2, 0 ).add( offset );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.size() ).isEqualTo( 0 );
	}

	protected Asset getAsset() {
		return tool.getAsset();
	}

	protected Design getDesign() {
		return getAsset().getModel();
	}

}
