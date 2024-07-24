package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.BaseCartesiaUiTest;
import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.data.Design;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.ProgramToolEvent;
import com.avereon.xenon.asset.Asset;
import com.avereon.zarra.event.FxEventWatcher;
import com.avereon.zarra.javafx.Fx;
import javafx.stage.Screen;
import lombok.CustomLog;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Getter
@CustomLog
public abstract class DesignToolV2BaseUIT extends BaseCartesiaUiTest {

	private DesignToolV2 tool;

	private Asset asset;

	private Design design;

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

		this.asset = tool.getAsset();
		this.design = tool.getDesign();

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
		assertThat( getTool().getVisibleLayers().size() ).isEqualTo( 0 );
		assertThat( getTool().getEnabledLayers().size() ).isEqualTo( 0 );
		assertThat( getTool().getVisibleShapes().size() ).isEqualTo( 0 );

		assertThat( getTool().getSelectTolerance().getValue() ).isEqualTo( 2 );
		assertThat( getTool().getSelectTolerance().getUnit() ).isEqualTo( DesignUnit.MILLIMETER );
	}

	protected double getWorldSelectTolerance() {
		return getTool().getSelectTolerance().to( getDesign().calcDesignUnit() ).getValue() / getTool().getZoom();
	}

	protected void useBoxLayer() throws TimeoutException, InterruptedException {
		getDesign().findLayerById( "a56cede9-ee12-40d0-a86c-b3701146c0e6" ).ifPresent( l -> Fx.run( () -> getTool().setLayerVisible( l, true ) ) );
		Fx.waitForWithExceptions( 1000 );
	}

	protected void useLineLayer() throws TimeoutException, InterruptedException {
		getDesign().findLayerById( "a56cede9-ee12-40d0-a86c-b3701146c0e7" ).ifPresent( l -> Fx.run( () -> getTool().setLayerVisible( l, true ) ) );
		Fx.waitForWithExceptions( 1000 );
	}

	protected void useEllipseLayer() throws TimeoutException, InterruptedException {
		getDesign().findLayerById( "a56cede9-ee12-40d0-a86c-b3701146c0e9" ).ifPresent( l -> Fx.run( () -> getTool().setLayerVisible( l, true ) ) );
		Fx.waitForWithExceptions( 1000 );
	}

	protected void useArcLayer() throws TimeoutException, InterruptedException {
		getDesign().findLayerById( "a56cede9-ee12-40d0-a86c-b3701146c0e8" ).ifPresent( l -> Fx.run( () -> getTool().setLayerVisible( l, true ) ) );
		Fx.waitForWithExceptions( 1000 );
	}

	protected void useQuadLayer() throws TimeoutException, InterruptedException {
		getDesign().findLayerById( "a56cede9-ee12-40d0-a86c-b3701146c0ea" ).ifPresent( l -> Fx.run( () -> getTool().setLayerVisible( l, true ) ) );
		Fx.waitForWithExceptions( 1000 );
	}

	protected void useCubicLayer() throws TimeoutException, InterruptedException {
		getDesign().findLayerById( "a56cede9-ee12-40d0-a86c-b3701146c0eb" ).ifPresent( l -> Fx.run( () -> getTool().setLayerVisible( l, true ) ) );
		Fx.waitForWithExceptions( 1000 );
	}

	protected void usePathLayer() throws TimeoutException, InterruptedException {
		getDesign().findLayerById( "a56cede9-ee12-40d0-a86c-b3701146c0ec" ).ifPresent( l -> Fx.run( () -> getTool().setLayerVisible( l, true ) ) );
		Fx.waitForWithExceptions( 1000 );
	}

	protected void useMarkerLayer() throws TimeoutException, InterruptedException {
		getDesign().findLayerById( "a56cede9-ee12-40d0-a86c-b3701146c0ed" ).ifPresent( l -> Fx.run( () -> getTool().setLayerVisible( l, true ) ) );
		Fx.waitForWithExceptions( 1000 );
	}

	protected void useTextLayer() throws TimeoutException, InterruptedException {
		getDesign().findLayerById( "a56cede9-ee12-40d0-a86c-b3701146c0ee" ).ifPresent( l -> Fx.run( () -> getTool().setLayerVisible( l, true ) ) );
		Fx.waitForWithExceptions( 1000 );
	}

}
