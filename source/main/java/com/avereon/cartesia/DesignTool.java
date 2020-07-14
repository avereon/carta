package com.avereon.cartesia;

import com.avereon.cartesia.cursor.StandardCursor;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.xenon.workspace.Workspace;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public abstract class DesignTool extends ProgramTool {

	private final CommandPrompt prompt;

	private final BorderPane coordinates;

	public DesignTool( ProgramProduct product, Asset asset ) {
		super( product, asset );

		this.prompt = new CommandPrompt( product );
		this.coordinates = new BorderPane();
		coordinates.setLeft( new Label( "X:" ) );
		coordinates.setRight( new Label( "Y:" ) );

		// Initial values from settings
		setCursor( StandardCursor.valueOf( product.getSettings().get( "reticle", StandardCursor.DUPLEX.name() ).toUpperCase() ) );

		// Settings listeners
		product.getSettings().register( "reticle", e -> setCursor( StandardCursor.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );

		onMouseMovedProperty().set( e -> {
			// TODO Convert to design coordinates and update the coordinates view
		} );
	}

	private CommandPrompt getCommandPrompt() {
		return prompt;
	}

	@Override
	protected void activate() throws ToolException {
		super.activate();
		Workspace workspace = getWorkspace();
		workspace.getStatusBar().addLeft( getCommandPrompt() );
		workspace.getStatusBar().addRight( coordinates );
	}

	@Override
	protected void deactivate() throws ToolException {
		super.conceal();
		Workspace workspace = getWorkspace();
		workspace.getStatusBar().removeRight( coordinates );
		workspace.getStatusBar().removeLeft( getCommandPrompt() );
	}

	private void setCursor( StandardCursor cursor ) {
		super.setCursor( cursor.get() );
	}

}
