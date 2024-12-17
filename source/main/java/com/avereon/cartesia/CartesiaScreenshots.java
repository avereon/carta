package com.avereon.cartesia;

import com.avereon.product.ProgramFlag;
import com.avereon.xenon.ProgramScreenshots;
import com.avereon.xenon.workpane.Tool;
import com.avereon.zarra.javafx.Fx;
import javafx.geometry.Bounds;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.robot.Robot;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class CartesiaScreenshots extends ProgramScreenshots {

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
	protected void generateScreenshots() throws InterruptedException, TimeoutException {
		generateDesignToolSnapshot( Path.of( "sample/design/demo.cartesia2d" ).toUri(), "cartesia-demo" );
	}

	private void generateDesignToolSnapshot( URI uri, String name ) throws InterruptedException, TimeoutException {
		openAsset( uri );
		Fx.run( () -> {
			Robot robot = new Robot();
			Tool tool = getProgram().getWorkspaceManager().getActiveWorkspace().getActiveWorkarea().getWorkpane().getActiveTool();
			Bounds b = tool.localToScreen( tool.getLayoutBounds() );
			robot.mouseMove( b.getCenterX(), b.getCenterY() );
			robot.mouseClick( MouseButton.PRIMARY );
			robot.keyType( KeyCode.Y );
			robot.keyType( KeyCode.Y );
		} );
		Fx.waitFor( 1000 );
		screenshot( name );
	}

}
