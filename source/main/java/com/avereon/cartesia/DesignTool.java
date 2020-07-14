package com.avereon.cartesia;

import com.avereon.cartesia.cursor.StandardCursor;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.xenon.workspace.Workspace;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public abstract class DesignTool extends ProgramTool {

	private final CommandPrompt prompt;

	private final CoordinateStatus coordinates;

	public DesignTool( ProgramProduct product, Asset asset ) {
		super( product, asset );

		addStylesheet( "cartesia.css" );

		this.prompt = new CommandPrompt( product );
		this.coordinates = new CoordinateStatus( this );

		// Initial values from settings
		setCursor( StandardCursor.valueOf( product.getSettings().get( "reticle", StandardCursor.DUPLEX.name() ).toUpperCase() ) );

		// Settings listeners
		product.getSettings().register( "reticle", e -> setCursor( StandardCursor.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );

		onMouseMovedProperty().set( coordinates::update );
	}

	protected abstract Point3D mouseToWorld( MouseEvent event );

	@Override
	protected void activate() throws ToolException {
		super.activate();
		Workspace workspace = getWorkspace();
		if( workspace != null ) {
			workspace.getStatusBar().addLeft( getCommandPrompt() );
			workspace.getStatusBar().addRight( coordinates );
		}
	}

	@Override
	protected void deactivate() throws ToolException {
		super.conceal();
		Workspace workspace = getWorkspace();
		if( workspace != null ) {
			workspace.getStatusBar().removeRight( coordinates );
			workspace.getStatusBar().removeLeft( getCommandPrompt() );
		}
	}

	private void setCursor( StandardCursor cursor ) {
		super.setCursor( cursor.get() );
	}

	private CommandPrompt getCommandPrompt() {
		return prompt;
	}

}
