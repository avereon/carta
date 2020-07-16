package com.avereon.cartesia;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class CommandPrompt extends BorderPane {

	private final DesignTool tool;

	private final TextField command;

	public CommandPrompt( DesignTool tool ) {
		this.tool = tool;
		getStyleClass().add( "cartesia-command");
		setLeft( new Label( tool.getProduct().rb().text( "prompt", "command" ) ) );
		setCenter( command = new TextField() );
	}

	public void clear() {
		// Just clear the current text
		command.setText( "" );
	}

	public void update( KeyEvent event ) {
		command.fireEvent( event );

		if( event.getEventType() == KeyEvent.KEY_PRESSED ) {
			switch( event.getCode() ) {
				case ESCAPE: {
					// Cancel the command stack
					clear();
					break;
				}
				case ENTER: {
					process( command.getText() );
					clear();
					break;
				}
			}
		}
	}

	private void process( String command ) {
		Design design = tool.getAssetModel();
		// TODO This needs to get the command stack/processor for the asset and push the command
		//design.getCommandProcessor().evaluate( command );
	}

}
