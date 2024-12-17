package com.avereon.cartesia;

import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.ProgramFlag;
import com.avereon.xenon.ProgramScreenshots;
import com.avereon.xenon.workpane.Tool;
import com.avereon.zarra.javafx.Fx;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.robot.Robot;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class CartesiaScreenshots extends ProgramScreenshots {

	private static final long FX_WAIT = 10000;

	public static void main( String[] args ) {
		new CartesiaScreenshots().generate( args );
	}

	//	@Override
	//	protected String getLogLevel() {
	//		return ProgramFlag.DEBUG;
	//	}

	@Override
	protected List<String> getProgramParameters() {
		List<String> parameters = new ArrayList<>( super.getProgramParameters() );

		parameters.add( ProgramFlag.ENABLE_MOD );
		parameters.add( CartesiaMod.class.getModule().getName() );

		return parameters;
	}

	@Override
	protected void generateScreenshots() throws InterruptedException, TimeoutException, ExecutionException {
		generateDesignToolSnapshot( Path.of( "sample/design/jet.cartesia2d" ).toUri(), "sample-jet" );
	}

	private void generateDesignToolSnapshot( URI uri, String name ) throws InterruptedException, TimeoutException, ExecutionException {
		openAsset( uri );

		Tool tool = getProgram().getWorkspaceManager().getActiveWorkspace().getActiveWorkarea().getWorkpane().getActiveTool();
		DesignTool designTool = (DesignTool)tool;

		double zoom = getRenderScale() / 4.0;

		clickCenter( tool );
		runCommand( designTool, "yy" );
		runCommand( designTool, "vl" );
		runCommand( designTool, "zm " + zoom );
		runCommand( designTool, "vp 0,-4" );

		screenshot( name );
	}

	private void clickCenter( Node node ) {
		Fx.run( () -> {
			Bounds b = node.localToScreen( node.getLayoutBounds() );
			Robot robot = new Robot();
			robot.mouseMove( b.getCenterX(), b.getCenterY() );
			robot.mouseClick( MouseButton.PRIMARY );
		} );
		Fx.waitFor( FX_WAIT );
	}

	private void runCommand( DesignTool designTool, String command ) {
		Fx.run( () -> designTool.getCommandContext().submit( designTool, command ) );
		Fx.waitFor( FX_WAIT );
		getProgram().getTaskManager().waitFor( FX_WAIT );
	}

}
